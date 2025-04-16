package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static me.dunescifye.lunaritems.utils.Utils.runConsoleCommands;

@SuppressWarnings("UnstableApiUsage")
public class SakuraListener implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof WindCharge windCharge)) return;
        if (!(windCharge.getShooter() instanceof Player p)) return;

        Set<String> tags = windCharge.getScoreboardTags();

        if (tags.contains("sakuraKunai")) {
            e.setCancelled(true);
            windCharge.remove();
            if (ThreadLocalRandom.current().nextInt(2) == 0)
                runConsoleCommands("ei give " + p.getName() + " sakurakunai 1");
            DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(p).build();
            if (e.getHitEntity() instanceof LivingEntity livingEntity) livingEntity.damage(6, damageSource);
        } else if (tags.contains("sakuraSidestep")) {
            e.setCancelled(true);
            windCharge.remove();
            if (e.getHitBlock() != null && e.getHitBlockFace() != null) {
                Location loc = e.getHitBlock().getLocation().add(e.getHitBlockFace().getDirection());
                loc.setYaw(p.getYaw());
                loc.setPitch(p.getPitch());
                p.teleport(loc);
            } else if (e.getHitEntity() instanceof LivingEntity livingEntity) {
                p.teleport(livingEntity);
            }
        }
    }

    @EventHandler
    public void onWindChargeExplosion(EntityExplodeEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof WindCharge windCharge)) return;
        Set<String> tags = windCharge.getScoreboardTags();
        if (tags.contains("sakuraKunai") || tags.contains("sakuraSidestep"))
            e.setCancelled(true);
    }

    @EventHandler
    public void onProjectileThrow(ProjectileLaunchEvent e) {
        if (e.isCancelled()) return;
        Projectile projectile = e.getEntity();
        if (!(projectile.getShooter() instanceof Player p)) return;

        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String eiID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);

        if (eiID == null) return;
        if (eiID.equals("sakurakunai")) {
            projectile.addScoreboardTag("sakuraKunai");
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (projectile.isDead()) {
                        this.cancel();
                    } else {
                        projectile.getWorld().spawnParticle(Particle.CHERRY_LEAVES, projectile.getLocation(), 1);
                    }
                }
            }.runTaskTimer(LunarItems.getPlugin(), 0, 1);
        } else if (eiID.equals("sakurasidestep")) {
            projectile.addScoreboardTag("sakuraSidestep");
        }
    }

    //Sakura Shulker
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerBlockBreak(BlockDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInOffHand();

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null || !itemID.contains("sakurashulker")) return;

        List<Item> drops = e.getItems();
        if (drops.size() != 1) return; // Only breaking ores

        ItemStack drop = drops.getFirst().getItemStack();
        String type = drop.getType().toString();

        String name = p.getName();
        int amount = drop.getAmount();
        int maxAmount = itemID.equals("sakurashulker") ? 512 : 2048; // regular version holds 512, mega holds 2048

        if (type.contains("DIAMOND") && pdc.getOrDefault(new NamespacedKey("score", "score-diamond"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 diamond " + amount);
            e.setCancelled(true);
        } else if (type.contains("IRON") && pdc.getOrDefault(new NamespacedKey("score", "score-iron"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 iron " + amount);
            e.setCancelled(true);
        } else if (type.contains("GOLD") && pdc.getOrDefault(new NamespacedKey("score", "score-gold"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 gold " + amount);
            e.setCancelled(true);
        } else if (type.contains("REDSTONE") && pdc.getOrDefault(new NamespacedKey("score", "score-redstone"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 redstone " + amount);
            e.setCancelled(true);
        } else if (type.contains("COPPER") && pdc.getOrDefault(new NamespacedKey("score", "score-copper"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 copper " + amount);
            e.setCancelled(true);
        } else if (type.contains("EMERALD") && pdc.getOrDefault(new NamespacedKey("score", "score-emerald"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 emerald " + amount);
            e.setCancelled(true);
        } else if (type.contains("COAL") && pdc.getOrDefault(new NamespacedKey("score", "score-coal"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 coal " + amount);
            e.setCancelled(true);
        } else if (type.contains("LAPIS") && pdc.getOrDefault(new NamespacedKey("score", "score-lapis"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 lapis " + amount);
            e.setCancelled(true);
        } else if (type.contains("DEBRIS") && pdc.getOrDefault(new NamespacedKey("score", "score-debris"), PersistentDataType.DOUBLE, 0.0) < maxAmount) {
            runConsoleCommands("ei console-modification modification variable " + name + " 40 debris " + amount);
            e.setCancelled(true);
        }
    }

}
