package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerItemHeldListener implements Listener {

    public void playerItemHeldHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
        Player p = e.getPlayer();
        ItemStack oldItem = p.getInventory().getItemInMainHand();
        ItemStack newItem = p.getInventory().getItem(e.getNewSlot());

        if (newItem != null && newItem.hasItemMeta()) {
            ItemMeta meta = newItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID != null) {
                switch (itemID) {
                    case "nexuspick" ->
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, -1, 2));
                }
            }
        }
        if (oldItem.hasItemMeta()) {
            ItemMeta meta = oldItem.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID != null) {
                switch (itemID) {
                    case "nexuspick" ->
                        p.removePotionEffect(PotionEffectType.SPEED);
                }
            }
        }
    }
}
