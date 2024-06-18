package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class BlockPistonExtendListener implements Listener {

    public void blockPistonExtendHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        List<Block> blocks = e.getBlocks();
        for (Block block : blocks) {
            PersistentDataContainer container = new CustomBlockData(block, LunarItems.getPlugin());
            String blockID = container.get(LunarItems.keyID, PersistentDataType.STRING);
            if (blockID != null) {
                switch (blockID) {
                    case "teleport_pad":
                        e.setCancelled(true);
                        return;
                }
            }
        }

    }

}
