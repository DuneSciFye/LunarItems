package me.dunescifye.lunaritems.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class PlayerJumpListener implements Listener {

    public void playerJumpHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        Block b = p.getLocation().getBlock();
        if (b.getType() != Material.AIR) {
            PersistentDataContainer container = new CustomBlockData(b, LunarItems.getPlugin());
            if (container.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                String blockID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);

                assert blockID != null;
                switch (blockID) {
                    case "elevator" -> {
                        for (int y = 1; y < 100; y++) {
                            Block relative = b.getRelative(0, y, 0);
                            PersistentDataContainer relativeContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                            String relativeID = relativeContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
                            if (relativeID == null) continue;
                            p.teleport(relative.getLocation().toCenterLocation());
                            break;
                        }
                    }
                }
            }
        }
    }

}
