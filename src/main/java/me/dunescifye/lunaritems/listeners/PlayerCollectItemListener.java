package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerCollectItemListener implements Listener {

  public static final NamespacedKey keyVoidMaterial = new NamespacedKey("score", "score-void_material");
  public static final NamespacedKey keyVoidMaterial2 = new NamespacedKey("score", "score-void_material2");
  public static final NamespacedKey keyVoidMaterial3 = new NamespacedKey("score", "score-void_material3");
  public static final NamespacedKey keyBlackListMaterial = new NamespacedKey("score", "score-blacklist_material");

  @EventHandler
  public void onPlayerCollectItem(EntityPickupItemEvent e) {
    if (!(e.getEntity() instanceof Player p)) return;
    PlayerInventory inv = p.getInventory();
    for (ItemStack invItem : inv.getContents()) {
      if (invItem != null && invItem.hasItemMeta()) {
        ItemMeta meta = invItem.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null || !itemID.contains("sunblackhole")) continue;

        String mat = pdc.get(keyVoidMaterial, PersistentDataType.STRING);
        String mat2 = pdc.get(keyVoidMaterial2, PersistentDataType.STRING);
        String mat3 = pdc.get(keyVoidMaterial3, PersistentDataType.STRING);

        Item item = e.getItem();
        Material itemMat = item.getItemStack().getType();

        if ((mat != null && itemMat.equals(Material.getMaterial(mat))) || (mat2 != null && itemMat.equals(Material.getMaterial(mat2))) || (mat3 != null && itemMat.equals(Material.getMaterial(mat3)))) {
          e.setCancelled(true);
          e.getItem().remove();
        }
      }
    }

    ItemStack offhand = inv.getItemInOffHand();
    ItemMeta meta = offhand.getItemMeta();
    if (meta != null) {
      PersistentDataContainer pdc = meta.getPersistentDataContainer();
      String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
      if (itemID != null) {
        if (itemID.contains("autumnsmoker")) {
          ItemStack item = e.getItem().getItemStack();
          if (Utils.rawOres.containsKey(item.getType())) {
            inv.addItem(item.withType(Utils.rawOres.get(item.getType())));
            e.setCancelled(true);
            e.getItem().remove();
            Utils.runConsoleCommands("ei console-modification modification variable " + p.getName() + " 40 cookedfood " + item.getAmount());
          } else if (Utils.smeltedFoods.containsKey(item.getType())) {
            inv.addItem(item.withType(Utils.smeltedFoods.get(item.getType())));
            e.setCancelled(true);
            e.getItem().remove();
            Utils.runConsoleCommands("ei console-modification modification variable " + p.getName() + " 40 cookedfood " + item.getAmount());
          }
        }
      }
    }

    ItemStack hand = inv.getItemInMainHand();
    ItemMeta meta2 = hand.getItemMeta();
    if (meta2 != null) {
      PersistentDataContainer pdc2 = meta2.getPersistentDataContainer();
      String itemID2 = pdc2.get(LunarItems.keyEIID, PersistentDataType.STRING);
      if (itemID2 != null) {
        if (itemID2.contains("starlightpick")) {
          String matString = pdc2.get(keyBlackListMaterial, PersistentDataType.STRING);
          if (matString != null) {
            Material mat = Material.getMaterial(matString);
            ItemStack item = e.getItem().getItemStack();
            if (item.getType().equals(mat)) {
              e.setCancelled(true);
            }
          }
        }
      }
    }

  }

}
