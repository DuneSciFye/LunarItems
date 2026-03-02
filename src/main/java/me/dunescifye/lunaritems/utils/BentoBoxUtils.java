package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Reflection-based wrapper for BentoBox API.
 * Allows compilation without the BentoBox jar.
 */
public class BentoBoxUtils {

    private static final Logger logger = Bukkit.getLogger();
    private static boolean initialized = false;
    private static boolean available = false;

    // Reflection references
    private static Object islandsManager;
    private static Method getIslandAtMethod;
    private static Method isAllowedMethod;
    private static Method userGetInstanceMethod;
    private static Object breakBlocksFlag;

    /**
     * Initialize the reflection hooks for BentoBox API.
     * Call this once during plugin enable.
     */
    public static void init() {
        if (initialized) return;
        initialized = true;

        try {
            // Get BentoBox instance
            Class<?> bentoBoxClass = Class.forName("world.bentobox.bentobox.BentoBox");
            Method getInstanceMethod = bentoBoxClass.getMethod("getInstance");
            Object bentoBox = getInstanceMethod.invoke(null);

            // Get IslandsManager
            Method getIslandsMethod = bentoBoxClass.getMethod("getIslands");
            islandsManager = getIslandsMethod.invoke(bentoBox);

            // Get getIslandAt(Location) method from IslandsManager
            getIslandAtMethod = islandsManager.getClass().getMethod("getIslandAt", Location.class);

            // Get User.getInstance(Player) for converting Player to BentoBox User
            Class<?> userClass = Class.forName("world.bentobox.bentobox.api.user.User");
            userGetInstanceMethod = userClass.getMethod("getInstance", Player.class);

            // Get Flags.BREAK_BLOCKS flag
            Class<?> flagsClass = Class.forName("world.bentobox.bentobox.lists.Flags");
            Field breakBlocksField = flagsClass.getField("BREAK_BLOCKS");
            breakBlocksFlag = breakBlocksField.get(null);

            // Get Island.isAllowed(User, Flag) method
            Class<?> islandClass = Class.forName("world.bentobox.bentobox.database.objects.Island");
            Class<?> flagClass = Class.forName("world.bentobox.bentobox.api.flags.Flag");
            isAllowedMethod = islandClass.getMethod("isAllowed", userClass, flagClass);

            available = true;
            logger.info("[LunarItems] BentoBox: API initialized successfully!");
        } catch (ClassNotFoundException e) {
            logger.warning("[LunarItems] BentoBox: Class not found - " + e.getMessage());
            available = false;
        } catch (NoSuchMethodException e) {
            logger.warning("[LunarItems] BentoBox: Method not found - " + e.getMessage());
            available = false;
        } catch (Exception e) {
            logger.warning("[LunarItems] BentoBox: Error initializing - " + e.getMessage());
            available = false;
        }
    }

    /**
     * Check if BentoBox API is available.
     */
    public static boolean isAvailable() {
        return available;
    }

    /**
     * Check if player has permission to break blocks at location.
     * Uses BentoBox's island flag system which respects per-island settings.
     * Returns true if:
     * - No island at location (wilderness)
     * - Player is allowed to break blocks based on island's BREAK_BLOCKS flag setting
     *   (respects island owner's configured rank permissions for trusted/member/etc.)
     *
     * Returns false if:
     * - Location is on an island and player's rank is below the island's BREAK_BLOCKS threshold
     */
    public static boolean hasPermission(Player player, Location location) {
        if (!available) return true;

        try {
            // Get island at location - returns Optional<Island>
            Object optionalIsland = getIslandAtMethod.invoke(islandsManager, location);

            if (optionalIsland instanceof Optional<?> opt) {
                if (opt.isEmpty()) {
                    return true; // No island = wilderness, allow
                }

                Object island = opt.get();

                // Convert Player to BentoBox User
                Object user = userGetInstanceMethod.invoke(null, player);

                // Check if user is allowed to break blocks on this island
                // This respects the island's BREAK_BLOCKS flag rank setting
                return (boolean) isAllowedMethod.invoke(island, user, breakBlocksFlag);
            }

            return true;
        } catch (Exception e) {
            logger.warning("[LunarItems] BentoBox: Error checking permission - " + e.getMessage());
            return true;
        }
    }
}
