package me.dunescifye.lunaritems.gui;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.utils.VaultUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ColorGUI implements Listener, InventoryHolder {

    private final Inventory inventory;
    private final Location blockLocation;
    private final String blockType; // "elevator" or "teleport_pad"

    // Track which players have which GUI open
    private static final Map<UUID, ColorGUI> openGuis = new HashMap<>();

    public ColorGUI(Player player, Block block, String blockType) {
        this.blockLocation = block.getLocation();
        this.blockType = blockType;

        // Create inventory with configured title
        this.inventory = Bukkit.createInventory(this, 27,
            LegacyComponentSerializer.legacyAmpersand().deserialize(BlocksConfig.colorGuiTitle));

        // Populate with color options
        for (Map.Entry<String, BlocksConfig.ColorOption> entry : BlocksConfig.colorOptions.entrySet()) {
            BlocksConfig.ColorOption option = entry.getValue();
            if (option.slot >= 0 && option.slot < 27) {
                inventory.setItem(option.slot, option.createGuiItem());
            }
        }

        openGuis.put(player.getUniqueId(), this);
        player.openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public static void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(new ColorGUIListener(), plugin);
    }

    private static class ColorGUIListener implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;
            if (!(event.getInventory().getHolder() instanceof ColorGUI gui)) return;

            event.setCancelled(true);

            int slot = event.getRawSlot();
            if (slot < 0 || slot >= 27) return;

            // Find which color was clicked
            BlocksConfig.ColorOption clickedOption = null;
            String clickedColorKey = null;
            for (Map.Entry<String, BlocksConfig.ColorOption> entry : BlocksConfig.colorOptions.entrySet()) {
                if (entry.getValue().slot == slot) {
                    clickedOption = entry.getValue();
                    clickedColorKey = entry.getKey();
                    break;
                }
            }

            if (clickedOption == null) return;

            // Check permission
            if (!player.hasPermission(clickedOption.permission)) {
                player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(BlocksConfig.colorChangeNoPermissionMessage));
                return;
            }

            // Check and withdraw money (if cost > 0)
            if (clickedOption.cost > 0) {
                if (!VaultUtils.isEconomyEnabled()) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize("&cEconomy is not available!"));
                    return;
                }

                if (!VaultUtils.has(player, clickedOption.cost)) {
                    player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(BlocksConfig.colorChangeNoMoneyMessage
                            .replace("%cost%", VaultUtils.format(clickedOption.cost))));
                    return;
                }

                VaultUtils.withdraw(player, clickedOption.cost);
            }

            // Change the block color
            Block block = gui.blockLocation.getBlock();

            // Preserve the block's custom data
            PersistentDataContainer blockContainer = new CustomBlockData(block, LunarItems.getPlugin());
            String blockID = blockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
            String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
            Location targetLocation = blockContainer.get(LunarItems.keyLocation, DataType.LOCATION);

            // Set the new shulker box type
            block.setType(clickedOption.shulkerMaterial);

            // Restore the custom data (setType clears it)
            blockContainer = new CustomBlockData(block, LunarItems.getPlugin());
            if (blockID != null) {
                blockContainer.set(LunarItems.keyEIID, PersistentDataType.STRING, blockID);
            }
            if (hologramID != null) {
                blockContainer.set(LunarItems.keyUUID, PersistentDataType.STRING, hologramID);
            }
            if (targetLocation != null) {
                blockContainer.set(LunarItems.keyLocation, DataType.LOCATION, targetLocation);
            }

            // Send success message
            player.sendMessage(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(BlocksConfig.colorChangeSuccessMessage
                    .replace("%color%", clickedOption.displayName)));

            player.closeInventory();
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event) {
            if (!(event.getPlayer() instanceof Player player)) return;
            if (event.getInventory().getHolder() instanceof ColorGUI) {
                openGuis.remove(player.getUniqueId());
            }
        }
    }
}
