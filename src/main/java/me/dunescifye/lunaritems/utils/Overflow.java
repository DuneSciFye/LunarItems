package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.util.Map;

public class Overflow {

    private static Constructor<?> overflowConstructor;
    private static boolean available;

    static {
        try {
            Class<?> clazz = Class.forName("org.dreeam.leaf.event.player.PlayerInventoryOverflowEvent");
            overflowConstructor = clazz.getConstructor(Player.class, Map.class);
            available = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            available = false;
        }
    }

    public static void addOverflow(Player p, Map<Integer, ItemStack> items) {
        if (!available) return;
        try {
            Event event = (Event) overflowConstructor.newInstance(p, items);
            Bukkit.getPluginManager().callEvent(event);
        } catch (Exception ignored) {
        }
    }
}
