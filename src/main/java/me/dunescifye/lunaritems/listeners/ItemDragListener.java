package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDragListener implements Listener {

  final List<String> entityNames = Arrays.asList(
    "allay", "armadillo", "axolotl", "bat", "bee", "blaze", "bogged", "breeze", "camel", "cat",
    "cave_spider", "chicken", "cod", "cow", "creaking", "creeper", "dolphin", "donkey", "drowned",
    "elder_guardian", "enderman", "endermite", "evoker", "fox", "frog", "ghast", "glow_squid",
    "goat", "guardian", "happy_ghast", "hoglin", "horse", "husk", "iron_golem", "llama",
    "magma_cube", "mooshroom", "mule", "ocelot", "panda", "parrot", "phantom", "pig", "piglin",
    "piglin_brute", "pillager", "polar_bear", "pufferfish", "rabbit", "ravager", "salmon", "sheep",
    "shulker", "silverfish", "skeleton", "skeleton_horse", "slime", "sniffer", "snow_golem",
    "spider", "squid", "stray", "strider", "tadpole", "trader_llama", "tropical_fish", "turtle",
    "vex", "villager", "vindicator", "wandering_trader", "warden", "witch", "wither_skeleton",
    "wolf", "zoglin", "zombie", "zombie_horse", "zombie_villager", "zombified_piglin"
  );


  @EventHandler(ignoreCancelled = true)
  public void onDragItem(InventoryClickEvent e) {
    ItemStack clickedItem = e.getCurrentItem();
    if (clickedItem == null) return;

    ItemMeta meta = clickedItem.getItemMeta();
    if (meta == null) return;

    PersistentDataContainer pdc = meta.getPersistentDataContainer();
    String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
    if (itemID == null) return;

    int slot = e.getSlot();
    HumanEntity p = e.getWhoClicked();
    ItemStack cursorItem = e.getCursor();

    if (itemID.equals("sunportablemobspawner")) {
      if (!cursorItem.getType().toString().contains("_SPAWN_EGG")) return;

      int uniqueEggs = 0;
      ArrayList<String> eggNames = new ArrayList<>();

      for (String entityName : entityNames) {
        Double spawnEggAmount = pdc.get(new NamespacedKey("score", "score-" + entityName),
          PersistentDataType.DOUBLE);
        if (spawnEggAmount != null && spawnEggAmount > 0) {
          uniqueEggs++;
          eggNames.add(entityName);
        }
      }

      String spawnEgg = cursorItem.getType().toString().replace("_SPAWN_EGG", "");

      e.setCancelled(true);

      if (uniqueEggs >= 16 && !eggNames.contains(spawnEgg)) return;

      Double spawnEggAmount = pdc.get(new NamespacedKey("score", "score-" + spawnEgg.toLowerCase()),
        PersistentDataType.DOUBLE);

      if (spawnEggAmount == null || spawnEggAmount >= 32) return;

      cursorItem.setAmount(cursorItem.getAmount() - 1);
      Utils.runConsoleCommands("ei console-modification modification variable " + p.getName() + " " + slot + " " + spawnEgg + " 1");
    } else if (itemID.equals("sunportablemobspawnerm")) {
      if (!cursorItem.getType().toString().contains("_SPAWN_EGG")) return;

      int uniqueEggs = 0;
      ArrayList<String> eggNames = new ArrayList<>();

      for (String entityName : entityNames) {
        Double spawnEggAmount = pdc.get(new NamespacedKey("score", "score-" + entityName),
          PersistentDataType.DOUBLE);
        if (spawnEggAmount != null && spawnEggAmount > 0) {
          uniqueEggs++;
          eggNames.add(entityName);
        }
      }

      String spawnEgg = cursorItem.getType().toString().replace("_SPAWN_EGG", "");

      e.setCancelled(true);

      if (uniqueEggs >= 32 && !eggNames.contains(spawnEgg)) return;

      Double spawnEggAmount = pdc.get(new NamespacedKey("score", "score-" + spawnEgg.toLowerCase()),
        PersistentDataType.DOUBLE);

      if (spawnEggAmount == null || spawnEggAmount >= 64) return;

      cursorItem.setAmount(cursorItem.getAmount() - 1);
      Utils.runConsoleCommands("ei console-modification modification variable " + p.getName() + " " + slot + " " + spawnEgg + " 1");
    }
  }

}
