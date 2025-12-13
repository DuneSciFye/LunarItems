package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Reflection-based wrapper for BaseRaiders API.
 * Allows compilation without the BaseRaiders jar.
 */
public class BaseRaidersUtils {

    private static final Logger logger = Bukkit.getLogger();
    private static boolean initialized = false;
    private static boolean available = false;
    private static Method getMethod;
    private static Method hasPermissionMethod;

    // Set to true to enable debug logging
    public static boolean debug = true;

    /**
     * Initialize the reflection hooks for BaseRaiders API.
     * Call this once during plugin enable.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            if (debug) logger.info("[LunarItems] BaseRaiders: Looking for API class...");
            Class<?> apiClass = Class.forName("me.fivekfubi.api.BaseRaidersAPI");
            if (debug) logger.info("[LunarItems] BaseRaiders: Found API class: " + apiClass.getName());

            getMethod = apiClass.getMethod("get");
            if (debug) logger.info("[LunarItems] BaseRaiders: Found get() method");

            Class<?> providerClass = Class.forName("me.fivekfubi.api.BaseRaidersProvider");
            if (debug) logger.info("[LunarItems] BaseRaiders: Found Provider class: " + providerClass.getName());

            hasPermissionMethod = providerClass.getMethod("has_permission", Player.class, Location.class, String.class);
            if (debug) logger.info("[LunarItems] BaseRaiders: Found has_permission() method");

            available = true;
            if (debug) logger.info("[LunarItems] BaseRaiders: API initialized successfully!");
        } catch (ClassNotFoundException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Class not found - " + e.getMessage());
            available = false;
        } catch (NoSuchMethodException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Method not found - " + e.getMessage());
            available = false;
        }
    }

    /**
     * Check if BaseRaiders API is available.
     */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Check if player has permission at location.
     * @param player The player to check
     * @param location The location to check
     * @param permission The permission string (e.g., "break")
     * @return true if player has permission, or if API is unavailable
     */
    public static boolean hasPermission(Player player, Location location, String permission) {
        if (!available) {
            if (debug) logger.info("[LunarItems] BaseRaiders: API not available, allowing action");
            return true;
        }

        try {
            Object provider = getMethod.invoke(null);
            if (debug) logger.info("[LunarItems] BaseRaiders: Got provider: " + (provider != null ? provider.getClass().getName() : "null"));

            if (provider == null) {
                if (debug) logger.warning("[LunarItems] BaseRaiders: Provider is null!");
                return true;
            }

            Object result = hasPermissionMethod.invoke(provider, player, location, permission);
            boolean hasPermission = (Boolean) result;

            if (debug) logger.info("[LunarItems] BaseRaiders: has_permission(" + player.getName() + ", " +
                location.getWorld().getName() + " " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() +
                ", \"" + permission + "\") = " + hasPermission);

            return hasPermission;
        } catch (Exception e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Error checking permission - " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}
