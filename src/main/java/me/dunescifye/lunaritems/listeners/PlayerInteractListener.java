package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.CooldownManager;
import me.dunescifye.lunaritems.utils.Utils;
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

public class PlayerInteractListener implements Listener {

    public void playerInteractHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;

        if (itemID.contains("nexushoe")) {
            if (e.getAction().isRightClick() && p.isSneaking()) {
                //If has cooldown
                if (CooldownManager.hasCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId()))
                    CooldownManager.sendCooldownMessage(p, CooldownManager.getRemainingCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId()));
                    //If doesn't have cooldown
                else {
                    double uses = container.get(LunarItems.keyUses, PersistentDataType.DOUBLE);
                    BlockUtils.boneMealRadius(p.getLocation(), 5);
                    // If out of uses, set cooldown
                    if (uses == 1) {
                        CooldownManager.setCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId(), Duration.ofMinutes(10));
                        container.set(LunarItems.keyUses, PersistentDataType.DOUBLE, 10.0);
                    } else
                        container.set(LunarItems.keyUses, PersistentDataType.DOUBLE, uses - 1);
                    item.setItemMeta(meta);
                }
            }
        } else if (itemID.contains("nexuspick") || itemID.contains("nexusaxe") || itemID.contains("nexusshovel") || itemID.contains("ancienttpick") || itemID.contains("ancienttshovel") ||itemID.contains("ancienttaxe")) {
            if (e.getAction().isRightClick() && p.isSneaking())
                Utils.updateKey(p, item, meta, container, LunarItems.keyLoreRadius, LunarItems.keyDepth, LunarItems.keyRadius, "Mining Mode", "1x1", 1.0, 0.0, "1x2", 2.0, 0.0, "3x2", 2.0, 1.0, "3x3", 3.0, 1.0, "3x4", 4.0, 1.0, "5x4", 4.0, 2.0, "5x5", 5.0, 2.0);
        }
    }
}
