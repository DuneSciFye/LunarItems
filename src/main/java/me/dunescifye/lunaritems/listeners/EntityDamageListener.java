package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class EntityDamageListener implements Listener {

    public void entityDamageHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (e.getCause() == EntityDamageEvent.DamageCause.THORNS) {
            if (e.getDamageSource().getCausingEntity() instanceof Player target) {
                ItemStack mainHand = target.getInventory().getItemInMainHand();
                ItemStack offhand = target.getInventory().getItemInOffHand();
                String itemID = null;

                testBothHands: {
                    if (mainHand.hasItemMeta()) {
                        ItemMeta meta = mainHand.getItemMeta();
                        PersistentDataContainer pdc = meta.getPersistentDataContainer();
                        itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                        if (itemID != null) {
                            break testBothHands;
                        }
                    }
                    if (offhand.hasItemMeta()) {
                        ItemMeta meta = offhand.getItemMeta();
                        PersistentDataContainer pdc = meta.getPersistentDataContainer();
                        itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                    }
                }

                if (itemID != null) {
                    switch (itemID) {
                        case
                            "aethershield" -> {
                            if (ThreadLocalRandom.current().nextInt(4) == 0)
                                e.setDamage(e.getDamage() * 2);
                        }
                    }
                }
            }
        }
    }

}
