package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InventoryClickListener implements Listener {

    public void inventoryClickHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        ItemStack cursorItem = e.getCurrentItem();
        ItemStack slotItem = e.getCursor();

        //Click is in selected slot
        if (p.getInventory().getHeldItemSlot() == e.getSlot()) {
            //Item going out of slot
            if (cursorItem != null && cursorItem.hasItemMeta()) {
                ItemMeta meta = cursorItem.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
                if (itemID != null) {
                    switch (itemID) {
                        case "nexuspick" ->
                            p.removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
            //Item going into slot
            if (slotItem.hasItemMeta()) {
                ItemMeta meta = slotItem.getItemMeta();
                PersistentDataContainer container = meta.getPersistentDataContainer();
                String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
                if (itemID != null) {
                    switch (itemID) {
                        case "nexuspick" ->
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 2));
                    }
                }
            }
        }

    }

}
