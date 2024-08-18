package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import eu.decentsoftware.holograms.api.DHAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockPlaceListener implements Listener {

    public void blockPlaceHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private final Map<Player, Location> teleportPadLocations = new HashMap<>();

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (e.isCancelled()) return;
        Block b = e.getBlockPlaced();
        ItemStack item = e.getItemInHand();
        Player p = e.getPlayer();

        if (item.hasItemMeta()) {
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(LunarItems.keyEIID)) {
                String blockID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
                assert blockID != null;
                switch (blockID) {
                    case "teleport_pad" -> {
                        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
                        blockContainer.set(LunarItems.keyEIID, PersistentDataType.STRING, "teleport_pad");
                        String hologramID = UUID.randomUUID().toString();
                        blockContainer.set(LunarItems.keyUUID, PersistentDataType.STRING, hologramID);

                        //Hologram
                        if (LunarItems.decentHologramsEnabled) {
                            DHAPI.createHologram(hologramID, b.getLocation().toCenterLocation().add(0, BlocksConfig.teleport_padHologramOffset, 0), true, BlocksConfig.teleport_padHologram);
                        }

                        //Second TP Pad
                        if (teleportPadLocations.containsKey(p)) {
                            Location loc = teleportPadLocations.get(p);
                            blockContainer.set(LunarItems.keyLocation, DataType.LOCATION, loc);
                            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                PlaceholderAPI.setPlaceholders(p, Config.prefix + Config.teleportPadPlaceSecondMessage
                                    .replace("%x%", String.valueOf(loc.getBlockX()))
                                    .replace("%y%", String.valueOf(loc.getBlockY()))
                                    .replace("%z%", String.valueOf(loc.getBlockZ()))
                                )));
                            teleportPadLocations.remove(p);
                            Block secondTPPad = b.getWorld().getBlockAt(loc);
                            PersistentDataContainer secondTPPadContainer = new CustomBlockData(secondTPPad, LunarItems.getPlugin());
                            secondTPPadContainer.set(LunarItems.keyLocation, DataType.LOCATION, b.getLocation().toCenterLocation());
                        }
                        //First TP Pad
                        else {
                            teleportPadLocations.put(p, b.getLocation().toCenterLocation());
                            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholders(p, Config.prefix + Config.teleportPadPlaceFirstMessage)));
                        }
                    }
                    case "elevator" -> {
                        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
                        blockContainer.set(LunarItems.keyEIID, PersistentDataType.STRING, "elevator");
                        String hologramID = UUID.randomUUID().toString();
                        blockContainer.set(LunarItems.keyUUID, PersistentDataType.STRING, hologramID);

                        //Hologram
                        if (LunarItems.decentHologramsEnabled) {
                            DHAPI.createHologram(hologramID, b.getLocation().toCenterLocation().add(0, BlocksConfig.elevatorHologramOffset, 0), true, BlocksConfig.elevatorHologram);
                        }
                    }
                }
            }
        }
    }

}
