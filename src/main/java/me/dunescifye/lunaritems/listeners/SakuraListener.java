package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WindCharge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("UnstableApiUsage")
public class SakuraListener implements Listener {

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.isCancelled()) return;
        if (!(e.getEntity() instanceof WindCharge windCharge)) return;
        if (!windCharge.getScoreboardTags().contains("sakuraKunai")) return;
        if (!(windCharge.getShooter() instanceof Player p)) return;

        e.setCancelled(true);
        windCharge.remove();
        if (ThreadLocalRandom.current().nextInt(2) == 0)
            Utils.runConsoleCommands("ei give " + p.getName() + "sakurakunai 1");
        DamageSource damageSource = DamageSource.builder(DamageType.PLAYER_ATTACK).withDirectEntity(p).build();
        if (e.getHitEntity() instanceof LivingEntity livingEntity) livingEntity.damage(6, damageSource);

    }

}
