package me.dunescifye.lunaritems.listeners;

import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
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

import static me.dunescifye.lunaritems.files.Config.prefix;
import static me.dunescifye.lunaritems.utils.Utils.getCoreProtect;

public class ItemFrameToInvTracker implements Listener {

    public static boolean enabled;
    public static Component message;

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
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
