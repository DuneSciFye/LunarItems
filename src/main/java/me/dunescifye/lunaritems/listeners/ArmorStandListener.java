package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ArmorStandListener implements Listener {

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        ItemStack item1 = e.getArmorStandItem();
        if (item1.hasItemMeta()) {
            PersistentDataContainer pdc = item1.getItemMeta().getPersistentDataContainer();
            if (pdc.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                e.getPlayer().clearActivePotionEffects();
            }
        }
        ItemStack item2 = e.getPlayerItem();
        if (item2.hasItemMeta()) {
            PersistentDataContainer pdc = item2.getItemMeta().getPersistentDataContainer();
            if (pdc.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                e.getPlayer().clearActivePotionEffects();
            }
        }
    }

}
