package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class AntiDropTracker implements Listener {

    ItemStack item;
    int count;
    int requiredCount;

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        System.out.println(item);
        if (!item.hasItemMeta()) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(LunarItems.keyEIID)) return;

        if (item.equals(this.item)) {
            System.out.println("a");
            count++;
        }
        else {
            System.out.println("b");
            count = 1;
            this.item = item;
            e.setCancelled(true);
            return;
        }

        if (count >= requiredCount) {
            System.out.println("c");
            count = 0;
        }
        else {
            e.setCancelled(true);
            System.out.println("d");
        }
    }

}
