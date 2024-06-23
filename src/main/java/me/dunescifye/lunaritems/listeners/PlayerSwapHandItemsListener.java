package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerSwapHandItemsListener implements Listener {

    public void playerSwapHandItemsHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
        ItemStack mainHandItem = e.getMainHandItem();
        ItemStack offHandItem = e.getOffHandItem();
        Player p = e.getPlayer();
        //Item going to offhand
        if (offHandItem.hasItemMeta()){
            PersistentDataContainer container = offHandItem.getItemMeta().getPersistentDataContainer();
            String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID != null) {
                switch (itemID) {
                    case "nexuspick" ->
                        p.removePotionEffect(PotionEffectType.SPEED);
                    }
                }
            }
        //Item going to mainhand
        if (mainHandItem.hasItemMeta()){
            PersistentDataContainer container = mainHandItem.getItemMeta().getPersistentDataContainer();
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
