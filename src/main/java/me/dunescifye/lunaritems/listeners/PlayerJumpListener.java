package me.dunescifye.lunaritems.listeners;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJumpListener implements Listener {

    public void playerJumpHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent e) {
        Player p = e.getPlayer();
        System.out.println(p.getLocation().getBlock());
    }

}
