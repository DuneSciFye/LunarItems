package me.dunescifye.lunaritems.listeners;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

import static me.dunescifye.lunaritems.utils.Utils.*;

public class PlayerLaunchProjectileListener implements Listener {
  @EventHandler(ignoreCancelled = true)
  public void onProjectileLaunch(EntityShootBowEvent e) {
    Player p = (Player) e.getEntity();
    ItemStack bow = e.getBow();
    if (bow == null) return;

    ItemMeta meta = bow.getItemMeta();
    if (meta == null) return;

    PersistentDataContainer container = meta.getPersistentDataContainer();
    String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
    if (itemID == null) return;


    // Do functions based on keys
    // Chance to not consume an arrow
    if (container.has(noArrowConsumeKey)) {
      Double chance = container.get(noArrowConsumeKey, PersistentDataType.DOUBLE);
      if (e.shouldConsumeItem() && chance != null && chance > 0 && ThreadLocalRandom.current().nextDouble(1.0) <= chance) {
        ItemStack arrow = e.getConsumable();
        if (arrow != null) {
          // Ignore infinity and normal arrows
          if (!bow.containsEnchantment(Enchantment.INFINITY) || arrow.getType() != Material.ARROW) {
            p.getInventory().addItem(arrow);
          }
        }
      }
    }

  }



}
