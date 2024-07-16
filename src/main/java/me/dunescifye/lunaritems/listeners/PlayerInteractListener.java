package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.CooldownManager;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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

        if (e.getAction().isRightClick()) {
            if (itemID.contains("nexushoe")) {
                if (p.isSneaking()) {
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
            } else if (itemID.contains("aetheraxe")) {
                Snowball snowball = p.getWorld().spawn(p.getLocation(), Snowball.class);
                snowball.setItem(new ItemStack(Material.NETHERITE_AXE));
            }
        }

    }
}
