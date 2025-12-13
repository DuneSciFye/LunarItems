package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Reflection-based wrapper for BaseRaiders API.
 * Allows compilation without the BaseRaiders jar.
 */
public class BaseRaidersUtils {

    private static final Logger logger = Bukkit.getLogger();
    private static boolean initialized = false;
    private static boolean available = false;

    // Reflection references
    private static Object recordsProtection; // Records.protection
    private static Method getFromWithinLocationMethod;
    private static Field membersMapField;
    private static Method getOwnerUuidMethod;

    // Set to true to enable debug logging
    public static boolean debug = false;

    /**
     * Initialize the reflection hooks for BaseRaiders API.
     * Call this once during plugin enable.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            if (debug) logger.info("[LunarItems] BaseRaiders: Looking for Records class...");

            // Get Records class and the static 'protection' field
            Class<?> recordsClass = Class.forName("me.fivekfubi.baseraiders.Records");
            Field protectionField = recordsClass.getField("protection");
            recordsProtection = protectionField.get(null);
            if (debug) logger.info("[LunarItems] BaseRaiders: Got Records.protection: " + recordsProtection.getClass().getName());

            // Get the getFromWithinLocation method
            getFromWithinLocationMethod = recordsProtection.getClass().getMethod("getFromWithinLocation", Location.class, int.class);
            if (debug) logger.info("[LunarItems] BaseRaiders: Found getFromWithinLocation method");

            // Get DATA_Protection class and its fields/methods
            Class<?> dataProtectionClass = Class.forName("me.fivekfubi.baseraiders.Protection.Data.DATA_Protection");
            membersMapField = dataProtectionClass.getField("members_map");
            if (debug) logger.info("[LunarItems] BaseRaiders: Found members_map field");

            getOwnerUuidMethod = dataProtectionClass.getMethod("get_owner_uuid");
            if (debug) logger.info("[LunarItems] BaseRaiders: Found get_owner_uuid method");

            available = true;
            if (debug) logger.info("[LunarItems] BaseRaiders: API initialized successfully!");
        } catch (ClassNotFoundException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Class not found - " + e.getMessage());
            available = false;
        } catch (NoSuchMethodException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Method not found - " + e.getMessage());
            available = false;
        } catch (NoSuchFieldException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Field not found - " + e.getMessage());
            available = false;
        } catch (IllegalAccessException e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Access error - " + e.getMessage());
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
     * Check if player can break blocks at location.
     * Returns true if:
     * - No protection at location (wilderness)
     * - Player is the owner of the protection
     * - Player is a member of the protection
     *
     * Returns false if:
     * - Location is protected and player is an outsider
     */
    @SuppressWarnings("unchecked")
    public static boolean hasPermission(Player player, Location location, String permission) {
        if (!available) {
            if (debug) logger.info("[LunarItems] BaseRaiders: API not available, allowing action");
            return true;
        }

        try {
            // Get protection at location
            Object protectionData = getFromWithinLocationMethod.invoke(recordsProtection, location, 0);

            if (protectionData == null) {
                // No protection here - wilderness, allow
                if (debug) logger.info("[LunarItems] BaseRaiders: No protection at " +
                    location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + " - allowing");
                return true;
            }

            String playerUuid = player.getUniqueId().toString();

            // Check if player is owner
            String ownerUuid = (String) getOwnerUuidMethod.invoke(protectionData);
            if (playerUuid.equals(ownerUuid)) {
                if (debug) logger.info("[LunarItems] BaseRaiders: Player " + player.getName() + " is OWNER - allowing");
                return true;
            }

            // Check if player is a member
            Map<String, String> membersMap = (Map<String, String>) membersMapField.get(protectionData);
            if (membersMap != null && membersMap.containsKey(playerUuid)) {
                if (debug) logger.info("[LunarItems] BaseRaiders: Player " + player.getName() + " is MEMBER - allowing");
                return true;
            }

            // Player is an outsider - deny
            if (debug) logger.info("[LunarItems] BaseRaiders: Player " + player.getName() + " is OUTSIDER at " +
                location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + " - DENYING");
            return false;

        } catch (Exception e) {
            if (debug) logger.warning("[LunarItems] BaseRaiders: Error checking permission - " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return true;
        }
    }
}
