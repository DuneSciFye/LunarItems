package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.concurrent.ThreadLocalRandom;

public class SeraphimListener implements Listener {

  public static final NamespacedKey pyroFishingTierKey = new NamespacedKey("pyrofishingpro", "tier");

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

    if (itemID.contains("seraphimrod")) {

      ItemStack fish = caughtItem.getItemStack();
      if (!fish.hasItemMeta()) return;
      PersistentDataContainer fpdc = fish.getItemMeta().getPersistentDataContainer();
      String tier = fpdc.get(pyroFishingTierKey, PersistentDataType.STRING);
      if (tier == null) return;
      if (tier.equals("bronze") || tier.equals("silver")) {
        if (ThreadLocalRandom.current().nextInt(300) == 1) {
          fish.setAmount(fish.getAmount() + 1);
        }
      }
    }
  }
}
