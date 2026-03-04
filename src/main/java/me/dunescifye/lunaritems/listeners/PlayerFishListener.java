package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.dunescifye.lunaritems.LunarItems.keyAutoSell;
import static me.dunescifye.lunaritems.utils.Utils.*;

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

    String autoSmelt = pdc.get(autoSmeltScoreKey, PersistentDataType.STRING);
    if ("Enabled".equals(autoSmelt)) {
      ItemStack itemStack = caughtItem.getItemStack();
      if (!itemStack.hasItemMeta()) {
        caughtItem.setItemStack(new ItemStack(Utils.smeltMaterial(itemStack.getType()), itemStack.getAmount()));
      }
    }
  }
}
