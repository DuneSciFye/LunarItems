package me.dunescifye.lunaritems.listeners;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.dunescifye.lunaritems.LunarItems;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class ItemFrameToInvTracker implements Listener {

    public static boolean enabled;
    public static Component message;

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemFrame(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame itemFrame) || !(e.getDamager() instanceof Player p)) return;
        ItemStack item = itemFrame.getItem();
        if (!item.hasItemMeta()) return;
        if (p.getInventory().addItem(item).isEmpty()) return;
        e.setCancelled(true);
        p.sendMessage(message);
    }

    @EventHandler(ignoreCancelled = true)
    public void onItemFrameDrop(PlayerItemFrameChangeEvent e) {
        ItemStack item = e.getItemStack();
        if (!item.hasItemMeta() || e.getAction() != PlayerItemFrameChangeEvent.ItemFrameChangeAction.REMOVE) return;
        e.setItemStack(null);
    }

}
