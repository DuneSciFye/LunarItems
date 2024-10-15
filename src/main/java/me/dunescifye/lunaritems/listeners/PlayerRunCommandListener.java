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

import static me.dunescifye.lunaritems.files.Config.prefix;

public class PlayerRunCommandListener implements Listener {

    public void playerRunCommandHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerRunCommand(PlayerCommandPreprocessEvent e) {
        if (!e.getMessage().contains("/hat")) return;
        Player p = e.getPlayer();
        ItemStack helmet = p.getInventory().getHelmet();
        if (helmet == null || helmet.getType() != Material.LEATHER_HELMET || !helmet.hasItemMeta()) return;
        ItemMeta meta = helmet.getItemMeta();
        String itemID = meta.getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;
        e.setCancelled(true);
        p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.cannotHatMessage));
    }

}
