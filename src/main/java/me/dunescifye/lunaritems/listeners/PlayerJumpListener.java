package me.dunescifye.lunaritems.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJumpListener implements Listener {

    public void playerJumpHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        Block b = p.getLocation().getBlock();

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
                case "elevator" -> {
                    for (int y = 1; y < 450; y++) {
                        Block relative = b.getRelative(0, y, 0);
                        PersistentDataContainer relativeContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        String relativeID = relativeContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
                        if (relativeID == null) continue;
                        Location targetLoc = relative.getLocation().toCenterLocation();
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
