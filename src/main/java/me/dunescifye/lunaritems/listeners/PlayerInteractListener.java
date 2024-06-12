package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;

import static me.dunescifye.lunaritems.utils.BlockUtils.boneMealRadius;
import static me.dunescifye.lunaritems.utils.CooldownManager.*;
import static me.dunescifye.lunaritems.utils.Utils.keyItemID;
import static me.dunescifye.lunaritems.utils.Utils.keyUses;

public class PlayerInteractListener implements Listener {

    public void playerInteractHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.HAND) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                if (container.has(keyItemID, PersistentDataType.STRING)) {
                    String itemID = container.get(keyItemID, PersistentDataType.STRING);
                    if (itemID.equals("nexushoe")) {
                        if (e.getAction().isRightClick() && p.isSneaking()) {
                            if (hasCooldown(nexusHoeCooldowns, p.getUniqueId())) { //If has cooldown
                                sendCooldownMessage(p, getRemainingCooldown(nexusHoeCooldowns, p.getUniqueId()));
                            } else { //If doesn't have cooldown
                                double uses = container.get(keyUses, PersistentDataType.DOUBLE);
                                boneMealRadius(p.getLocation(), 5);
                                if (uses == 1) { // If out of uses, set cooldown
                                    setCooldown(nexusHoeCooldowns, p.getUniqueId(), Duration.ofMinutes(10));
                                    container.set(keyUses, PersistentDataType.DOUBLE, 10.0);
                                } else {
                                    container.set(keyUses, PersistentDataType.DOUBLE, uses - 1);
                                }
                                item.setItemMeta(meta);
                            }
                        }
                    }
                    else if (itemID.equals("nexushoeo")) {
                        if (e.getAction().isRightClick() && p.isSneaking()) {
                            if (hasCooldown(nexusHoeCooldowns, p.getUniqueId())) { //If has cooldown
                                sendCooldownMessage(p, getRemainingCooldown(nexusHoeCooldowns, p.getUniqueId()));
                            } else { //If doesn't have cooldown
                                double uses = container.get(keyUses, PersistentDataType.DOUBLE);
                                boneMealRadius(p.getLocation(), 5);
                                if (uses == 1) { // If out of uses, set cooldown
                                    setCooldown(nexusHoeCooldowns, p.getUniqueId(), Duration.ofMinutes(10));
                                    container.set(keyUses, PersistentDataType.DOUBLE, 10.0);
                                } else {
                                    container.set(keyUses, PersistentDataType.DOUBLE, uses - 1);
                                }
                                item.setItemMeta(meta);
                            }
                        }
                    }
                    if (itemID.equals("nexushoemega")) {
                        if (e.getAction().isRightClick() && p.isSneaking()) {
                            if (hasCooldown(nexusHoeCooldowns, p.getUniqueId())) { //If has cooldown
                                sendCooldownMessage(p, getRemainingCooldown(nexusHoeCooldowns, p.getUniqueId()));
                            } else { //If doesn't have cooldown
                                double uses = container.get(keyUses, PersistentDataType.DOUBLE);
                                boneMealRadius(p.getLocation(), 5);
                                if (uses == 1) { // If out of uses, set cooldown
                                    setCooldown(nexusHoeCooldowns, p.getUniqueId(), Duration.ofMinutes(10));
                                    container.set(keyUses, PersistentDataType.DOUBLE, 10.0);
                                } else {
                                    container.set(keyUses, PersistentDataType.DOUBLE, uses - 1);
                                }
                                item.setItemMeta(meta);
                            }
                        }
                    }
                }
            }
        }
    }

}
