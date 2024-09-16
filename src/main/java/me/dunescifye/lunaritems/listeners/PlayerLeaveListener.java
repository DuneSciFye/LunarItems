package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeaveListener implements Listener {

    public void playerLeaveListener(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        AttributeInstance attribute = p.getAttribute(Attribute.GENERIC_SCALE);
        if (attribute == null) return;
        for (AttributeModifier attributeModifier : attribute.getModifiers()) {
            if (attributeModifier.getName().equals("soulshrinker")) {
                attribute.removeModifier(attributeModifier);
                return;
            }
        }
    }

}
