package me.dunescifye.lunaritems.listeners;

import com.jeff_media.morepersistentdatatypes.DataType;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.persistence.PersistentDataContainer;

public class EntityDamageByEntityListener implements Listener {

    public void entityDamageByEntityHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        Entity damager = e.getDamager();
        if (damager instanceof Firework fw) {
            if (fw.hasMetadata("nodamage")) {
                PersistentDataContainer container = fw.getPersistentDataContainer();
                Player noDamagePlayer = container.get(LunarItems.keyNoDamagePlayer, DataType.PLAYER);
                if (noDamagePlayer != null) {
                    if (entity == noDamagePlayer) {
                        e.setCancelled(true);
                    }
                } else {
                    e.setCancelled(true);
                }
            }
        }


    }

}
