package me.dunescifye.lunaritems.listeners;

import io.papermc.paper.entity.TeleportFlag;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

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
                Utils.runConsoleCommands("ei give " + p.getName() + " sakurakunai 1");
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

        PersistentDataContainer pdc = p.getInventory().getItemInMainHand().getItemMeta().getPersistentDataContainer();
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

}
