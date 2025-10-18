package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class HungerListener implements Listener {

  @EventHandler
  public void onPlayerFoodLevelChange(FoodLevelChangeEvent e) {
    Entity entity = e.getEntity();
    if (!(entity instanceof Player p)) return;
    ItemStack item = p.getInventory().getItemInOffHand();

    if (!item.hasItemMeta()) return;
    PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
    String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
    if (itemID == null) return;

    if (itemID.contains("seraphimwings")) {
      if (ThreadLocalRandom.current().nextInt(2) == 0) {
        e.setCancelled(true);
      }
    } else if (itemID.contains("autumnhoe")) {
      long pTime = p.getPlayerTime() % 24000L;
      // If ptime is day increase hunger, otherwise no hunger
      if (pTime < 12300 || pTime > 23850) {
        p.setFoodLevel(p.getFoodLevel() - 2);
      } else {
        e.setCancelled(true);
      }
    }
  }

}
