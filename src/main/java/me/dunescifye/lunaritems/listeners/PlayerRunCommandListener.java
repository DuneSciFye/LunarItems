package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import static me.dunescifye.lunaritems.files.Config.prefix;

public class PlayerRunCommandListener implements Listener {

    public void playerRunCommandHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRunCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (e.getMessage().contains("/hat")) {
            ItemStack helmet = p.getInventory().getHelmet();
            if (helmet == null || helmet.getType() != Material.LEATHER_HELMET || !helmet.hasItemMeta()) return;
            ItemMeta meta = helmet.getItemMeta();
            String itemID = meta.getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID == null) return;
            e.setCancelled(true);
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.cannotHatMessage));
        } else if (e.getMessage().contains("/auction sell") || e.getMessage().contains("/ah sell")) {
            ItemStack item = p.getInventory().getItemInMainHand();
            if (!item.hasItemMeta()) return;
            ItemMeta meta = item.getItemMeta();
            String itemID = meta.getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID == null) return;
            if (p.hasPotionEffect(PotionEffectType.SPEED) && p.getPotionEffect(PotionEffectType.SPEED).getAmplifier() >= 2) {
                p.removePotionEffect(PotionEffectType.SPEED);
                p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.speedRemovedMessage));
            }
        }
    }

}
