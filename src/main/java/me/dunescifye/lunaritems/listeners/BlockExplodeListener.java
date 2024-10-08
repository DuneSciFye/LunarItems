package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;

public class BlockExplodeListener implements Listener {

    public void blockExplodeHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        for (Block block : new ArrayList<>(e.blockList())) {
            PersistentDataContainer container = new CustomBlockData(block, LunarItems.getPlugin());
            if (container.has(LunarItems.keyEIID))
                e.blockList().remove(block);
        }

    }

}
