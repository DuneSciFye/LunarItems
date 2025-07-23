package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class EntityDamageListener implements Listener {

    public void entityDamageHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static final NamespacedKey keyKills = new NamespacedKey("score", "score-kills");

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();

        if (e.getDamageSource().getCausingEntity() instanceof Player damager && entity instanceof Player victim) {
            ItemStack item = damager.getInventory().getItemInMainHand();
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                PersistentDataContainer pdc = meta.getPersistentDataContainer();
                String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                if (itemID != null) {
                    if (itemID.contains("abyssmace")) {
                        Double kills = pdc.getOrDefault(keyKills, PersistentDataType.DOUBLE, 0.0);
                        if (kills >= 50 && ThreadLocalRandom.current().nextInt(40) == 0) {
                            Entity skeleton = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.SKELETON, CreatureSpawnEvent.SpawnReason.CUSTOM);
                            skeleton.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
                        }
                    }
                }
            }
        }

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
