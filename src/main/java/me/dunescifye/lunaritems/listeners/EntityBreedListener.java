package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class EntityBreedListener implements Listener {

  @EventHandler
    public void onEntityBreed(EntityBreedEvent e) {
    if (!(e.getBreeder() instanceof Player p)) return;
    Entity entity = e.getEntity();

    PlayerInventory inv = p.getInventory();

    ItemMeta mainhandMeta = inv.getItemInMainHand().getItemMeta();
    ItemMeta offhandMeta = inv.getItemInOffHand().getItemMeta();

    if (mainhandMeta != null) {
      String itemID = mainhandMeta.getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
      if (itemID != null && itemID.contains("amberlightmobwand") && ThreadLocalRandom.current().nextInt(100) == 0) {
        Material spawnEgg = Material.getMaterial(entity.getType().name() + "_SPAWN_EGG");
        if (spawnEgg != null) Utils.dropItems(p.getLocation(), p.getUniqueId(), new ItemStack(spawnEgg));
      }
    }

    if (offhandMeta != null) {
      String itemID = offhandMeta.getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
      if (itemID != null && itemID.contains("amberlightmobwand") && ThreadLocalRandom.current().nextInt(100) == 0) {
        Material spawnEgg = Material.getMaterial(entity.getType().name() + "_SPAWN_EGG");
        if (spawnEgg != null) Utils.dropItems(p.getLocation(), p.getUniqueId(), new ItemStack(spawnEgg));
      }
    }
  }

}
