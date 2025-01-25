package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class ItemFrameToInvTracker implements Listener {

    public static boolean enabled;
    public static String message;

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onItemFrame(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof ItemFrame itemFrame) || !(e.getDamager() instanceof Player p)) return;
        ItemStack item = itemFrame.getItem();
        if (!item.hasItemMeta()) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        if (!pdc.has(LunarItems.keyEIID)) return;
        e.setCancelled(true);
        if (p.getInventory().addItem(item).isEmpty()) itemFrame.setItem(new ItemStack(Material.AIR));
        else p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

}
