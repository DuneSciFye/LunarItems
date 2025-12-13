package me.dunescifye.lunaritems.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Reflection-based wrapper for BaseRaiders API.
 * Allows compilation without the BaseRaiders jar.
 */
public class BaseRaidersUtils {

    private static boolean initialized = false;
    private static boolean available = false;
    private static Method getMethod;
    private static Method hasPermissionMethod;

    /**
     * Initialize the reflection hooks for BaseRaiders API.
     * Call this once during plugin enable.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            Class<?> apiClass = Class.forName("me.fivekfubi.api.BaseRaidersAPI");
            getMethod = apiClass.getMethod("get");

            Class<?> providerClass = Class.forName("me.fivekfubi.api.BaseRaidersProvider");
            hasPermissionMethod = providerClass.getMethod("has_permission", Player.class, Location.class, String.class);

            available = true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
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
        if (!available) return true;

        try {
            Object provider = getMethod.invoke(null);
            Object result = hasPermissionMethod.invoke(provider, player, location, permission);
            return (Boolean) result;
        } catch (Exception e) {
            // If anything goes wrong, allow the action
            return true;
        }
    }
}
