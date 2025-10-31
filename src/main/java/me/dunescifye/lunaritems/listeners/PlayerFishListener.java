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

import java.math.BigDecimal;
import java.math.RoundingMode;

import static me.dunescifye.lunaritems.LunarItems.keyAutoSell;
import static me.dunescifye.lunaritems.LunarItems.keyMoney;
import static me.dunescifye.lunaritems.utils.Utils.runConsoleCommands;

public class PlayerFishListener implements Listener {


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

    if (itemID.contains("halloweenrod")) {
      if (("Enabled").equals(pdc.get(keyAutoSell, PersistentDataType.STRING)) && !caughtItem.getItemStack().hasItemMeta()) {
        runConsoleCommands("ei console-modification modification variable " + p.getName() + " -1 fish 1");
        caught.remove();
      }
    }
  }
}
