package me.dunescifye.lunaritems.listeners;

import com.destroystokyo.paper.event.entity.WitchThrowPotionEvent;
import io.papermc.paper.event.entity.WardenAngerChangeEvent;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static me.dunescifye.lunaritems.LunarItems.keyAutoSell;
import static me.dunescifye.lunaritems.LunarItems.keyMoney;
import static me.dunescifye.lunaritems.utils.Utils.runConsoleCommands;

public class AbyssListener implements Listener {
  public static final NamespacedKey keyKills = new NamespacedKey("score", "score-kills");
  public static final NamespacedKey keyFlyingSpeed = new NamespacedKey("lunaritems", "abyssarmor");

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
            double kills = pdc.getOrDefault(keyKills, PersistentDataType.DOUBLE, 0.0);
            if (kills >= 50 && ThreadLocalRandom.current().nextInt(40) == 0) {
              Entity skeleton = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.SKELETON, CreatureSpawnEvent.SpawnReason.CUSTOM);
              skeleton.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
              ((Mob) skeleton).setTarget(victim);

              Bukkit.getScheduler().runTaskLater(LunarItems.getPlugin(), () -> {
                if (!skeleton.isDead()) skeleton.remove();
              }, 20L * 60L);
            }
            if (kills >= 90 && ThreadLocalRandom.current().nextInt(40) == 0) {
              Entity zombie = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.ZOMBIE,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
              zombie.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
              ((Mob) zombie).setTarget(victim);

              Bukkit.getScheduler().runTaskLater(LunarItems.getPlugin(), () -> {
                if (!zombie.isDead()) zombie.remove();
              }, 20L * 60L);
            }
            if (kills >= 150 && ThreadLocalRandom.current().nextInt(40) == 0) {
              Entity endermite = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.ENDERMITE,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
              endermite.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
              ((Mob) endermite).setTarget(victim);

              Bukkit.getScheduler().runTaskLater(LunarItems.getPlugin(), () -> {
                if (!endermite.isDead()) endermite.remove();
              }, 20L * 60L);
            }
            if (kills >= 300 && ThreadLocalRandom.current().nextInt(40) == 0) {
              Entity blaze1 = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.BLAZE,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
              Entity blaze2 = damager.getWorld().spawnEntity(damager.getLocation(), EntityType.BLAZE,
                CreatureSpawnEvent.SpawnReason.CUSTOM);
              blaze1.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
              ((Mob) blaze1).setTarget(victim);
              blaze2.setMetadata("abyssmace", new FixedMetadataValue(LunarItems.getPlugin(), 1));
              ((Mob) blaze2).setTarget(victim);

              Bukkit.getScheduler().runTaskLater(LunarItems.getPlugin(), () -> {
                if (!blaze1.isDead()) blaze1.remove();
                if (!blaze2.isDead()) blaze2.remove();
              }, 20L * 60L * 2L);
            }
          }
        }
      }
    }
  }

  @EventHandler
  public void onEntityShoot(EntityShootBowEvent e) {
    Entity entity = e.getEntity();
    if (entity instanceof Skeleton skeleton && entity.hasMetadata("abyssmace")) {
      Fireball fireball = entity.getWorld().spawn(entity.getLocation().add(0, 1, 0), Fireball.class);

      fireball.setVelocity(e.getProjectile().getVelocity().normalize().multiply(0.6));
      fireball.setShooter(skeleton);
      fireball.setYield(2);
      fireball.setIsIncendiary(true);

      e.setProjectile(fireball);
    } else if (entity instanceof Blaze blaze && entity.hasMetadata("abyssmace")) {
      WitherSkull skull = (WitherSkull) entity.getWorld().spawnEntity(entity.getLocation(), EntityType.WITHER_SKULL);
      skull.setShooter(blaze);

      e.setProjectile(skull);
    }
  }

  @EventHandler
  public void onEntityHit(EntityDamageByEntityEvent e) {
    if (!(e.getEntity() instanceof Player victim)) return;
    if (e.getDamager() instanceof Zombie damager && damager.hasMetadata("abyssmace")) {
      PotionEffect potionEffect = new PotionEffect(PotionEffectType.POISON, 40, 0);
      victim.addPotionEffect(potionEffect);
    } else if (e.getDamager() instanceof Endermite damager && damager.hasMetadata("abyssmace")) {
      victim.setVelocity(damager.getLocation().toVector().subtract(victim.getLocation().toVector()).normalize().multiply(2));
    }
  }

  @EventHandler
  public void onWitchThrowPotion(WitchThrowPotionEvent e) {
    Witch witch = e.getEntity();
    if (witch.hasMetadata("healingabysswitch")) {
      List<MetadataValue> values = witch.getMetadata("healingabysswitch");
      MetadataValue value = values.getFirst();
      String stringUUID = value.asString();
      witch.setTarget(Bukkit.getPlayer(UUID.fromString(stringUUID)));
      ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
      PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
      potionMeta.setBasePotionType(PotionType.REGENERATION);
      potion.setItemMeta(potionMeta);
      e.setPotion(potion);
    } else if (witch.hasMetadata("damageabysswitch")) {
      List<MetadataValue> values = witch.getMetadata("damageabysswitch");
      MetadataValue value = values.getFirst();
      String stringUUID = value.asString();
      witch.setTarget(Bukkit.getPlayer(UUID.fromString(stringUUID)));
      ItemStack potion = new ItemStack(Material.SPLASH_POTION, 1);
      PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
      potionMeta.setBasePotionType(PotionType.POISON);
      potion.setItemMeta(potionMeta);
      e.setPotion(potion);
    }
  }

  @EventHandler
  public void onHappyGhastRide(PlayerInteractEntityEvent e) {
    Entity entity = e.getRightClicked();
    if (!(entity instanceof HappyGhast happyGhast)) return;
    Player p = e.getPlayer();

    double speed = 0.0;

    for (ItemStack armor : p.getInventory().getArmorContents()) {
      if (armor == null || !armor.hasItemMeta()) continue;
      PersistentDataContainer pdc = armor.getItemMeta().getPersistentDataContainer();
      String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
      if (itemID == null) continue;
      if (itemID.contains("abyss")) {
        speed += 0.05;
      }
    }

    AttributeInstance attributeInstance = happyGhast.getAttribute(Attribute.FLYING_SPEED);
    AttributeModifier attributeModifier = new AttributeModifier(keyFlyingSpeed, speed, AttributeModifier.Operation.ADD_SCALAR);
    attributeInstance.removeModifier(attributeModifier);
    if (speed > 0) attributeInstance.addModifier(attributeModifier);
  }

  @EventHandler
  public void onEntityTarget(EntityTargetLivingEntityEvent e) {
    if (e.getEntity().hasMetadata("passive")) {
      e.setCancelled(true);
    }
  }
  @EventHandler
  public void onWardenAnger(WardenAngerChangeEvent e) {
    if (e.getEntity().hasMetadata("passive")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onWardenDarkness(EntityPotionEffectEvent e) {
    if (e.getCause().equals(EntityPotionEffectEvent.Cause.WARDEN)) {
      Entity effectReceiver = e.getEntity();
      if (effectReceiver instanceof Player p) {
        for (Entity entity : p.getNearbyEntities(30, 30, 30)) {
          if (entity instanceof Warden warden && warden.hasMetadata("passive")) {
            e.setCancelled(true);
            return;
          }
        }
      }
    }
  }

  @EventHandler
  public void onPlayerFishFish(PlayerFishEvent e) {
    Player p = e.getPlayer();
    if (e.getHand() == null) return;
    ItemStack item = p.getInventory().getItem(e.getHand());

    if (!item.hasItemMeta()) return;
    PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
    String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
    if (itemID == null) return;

    Entity caught = e.getCaught();
    if (!(caught instanceof Item caughtItem)) return;

    if (itemID.contains("abyssrod")) {
      if (("Enabled").equals(pdc.get(keyAutoSell, PersistentDataType.STRING))) {
        BigDecimal totalPrice = BigDecimal.valueOf(pdc.get(keyMoney, PersistentDataType.DOUBLE));
        Double price = pdc.get(new NamespacedKey("score", "score-" + caughtItem.getItemStack().getType().toString().toLowerCase()), PersistentDataType.DOUBLE);
        if (price != null) {
          caughtItem.remove();
          totalPrice = totalPrice.add(BigDecimal.valueOf(price));
          String formattedTotal = totalPrice.setScale(4, RoundingMode.HALF_UP).toPlainString();
          runConsoleCommands("ei console-modification set variable " + p.getName() + " -1 " +
            "money " + formattedTotal);
        }
      }
    }
  }
}
