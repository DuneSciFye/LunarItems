package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.dreeam.leaf.event.player.PlayerInventoryOverflowEvent;

import java.util.Map;

public class Overflow {
    public static void addOverflow(Player p, Map<Integer, ItemStack> items) {
        Bukkit.getPluginManager().callEvent(new PlayerInventoryOverflowEvent(p, items));
    }
}
