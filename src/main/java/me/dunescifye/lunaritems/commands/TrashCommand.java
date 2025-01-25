package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.dunescifye.lunaritems.LunarItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.HashMap;
import java.util.UUID;

public class TrashCommand implements Listener {

    public static boolean enabled;
    public static Component inventoryName;
    private static final HashMap<UUID, Inventory> inventories = new HashMap<>();

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void register() {
        new CommandAPICommand("trash")
            .executesConsole((sender, args) -> {
                sender.sendMessage("Cannot use /trash from Console, this is a Player only command!");
            })
            .executesPlayer((p, args) -> {
                Inventory inv = Bukkit.createInventory(null, 54, inventoryName);
                p.openInventory(inv);
                inventories.put(p.getUniqueId(), inv);
            })
            .withPermission("lunaritems.command.trash")
            .register();
    }

    // Prevent any EI items from going into trash
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        if (!inv.equals(inventories.get(p.getUniqueId()))) return;

        final ItemStack cursorItem = e.getCursor();
        final ItemStack clickedItem = e.getCurrentItem();
        PersistentDataContainer pdc;

        if (e.getRawSlot() < 54) { // Clicked in the Container
            if (!cursorItem.hasItemMeta()) return;
            pdc = cursorItem.getItemMeta().getPersistentDataContainer();
        } else { // Clicked in Inventory
            if (!e.getClick().isShiftClick() || clickedItem == null || !clickedItem.hasItemMeta()) return;
            pdc = clickedItem.getItemMeta().getPersistentDataContainer();
        }
        if (!pdc.has(LunarItems.keyEIID)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        Inventory inv = e.getInventory();
        if (!inv.equals(inventories.get(uuid))) return;
        inventories.remove(uuid);
        for (ItemStack item : inv.getContents()) { // Give the player any EI items that somehow made it into the trash
            if (item == null || !item.hasItemMeta()) continue;
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            if (pdc.has(LunarItems.keyEIID)) e.getPlayer().getInventory().addItem(item);
        }
    }
}
