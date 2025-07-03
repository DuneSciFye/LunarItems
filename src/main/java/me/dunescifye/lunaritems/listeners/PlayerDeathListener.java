package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.CooldownManager;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDeathListener implements Listener {

    public static final Map<UUID, Instant> creakingShovelCDs = new HashMap<>();
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!item.hasItemMeta()) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();

        String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);

        if (itemID!= null && itemID.contains("creakingshovel") && e.getDamageSource().getDamageType().equals(DamageType.FALL)) {
            e.setCancelled(true);
            if (!CooldownManager.hasCooldown(creakingShovelCDs, p.getUniqueId())) {
                p.setVelocity(p.getVelocity().setY(3));
                CooldownManager.setCooldown(creakingShovelCDs, p.getUniqueId(), Duration.ofSeconds(5));
            }
        }
    }

}
