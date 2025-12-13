package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerToggleSneakListener implements Listener {

    public void playerToggleSneakHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
        Player p = e.getPlayer();
        Block b = p.getLocation().getBlock();
        if (e.isSneaking()) {
            // Check block at feet first, then block below (for shulker boxes player stands on top of)
            PersistentDataContainer container = new CustomBlockData(b, LunarItems.getPlugin());
            if (!container.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                b = b.getRelative(0, -1, 0);
                container = new CustomBlockData(b, LunarItems.getPlugin());
            }

            if (container.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                String blockID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);

                assert blockID != null;
                switch (blockID) {
                    case "teleport_pad" -> {
                        Location targetLoc = container.get(LunarItems.keyLocation, DataType.LOCATION);
                        if (targetLoc == null
                            || !Objects.equals(new CustomBlockData(targetLoc.getBlock(), LunarItems.getPlugin()).get(LunarItems.keyEIID, PersistentDataType.STRING), "teleport_pad")) {
                            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(
                                PlaceholderAPI.setPlaceholders(p, Config.prefix + Config.invalidTargetLocation)
                            ));
                        } else {
                            // Add 0.5 X/Z for center, +1 Y to land on top of shulker
                            Location tpLoc = targetLoc.clone().add(0.5, 1, 0.5);
                            tpLoc.setYaw(p.getYaw());
                            tpLoc.setPitch(p.getPitch());
                            p.teleport(tpLoc);
                        }
                    }
                    case "elevator" -> {
                        for (int y = -1; y > -450; y--) {
                            Block relative = b.getRelative(0, y, 0);
                            PersistentDataContainer relativeContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                            String relativeID = relativeContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
                            if (relativeID == null) continue;
                            Location targetLoc = relative.getLocation().add(0.5, 1, 0.5);
                            targetLoc.setYaw(p.getYaw());
                            targetLoc.setPitch(p.getPitch());
                            p.teleport(targetLoc);
                            break;
                        }
                    }
                }
            }
        }
    }

}
