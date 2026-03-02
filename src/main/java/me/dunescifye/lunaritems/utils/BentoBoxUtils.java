package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
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
    private static Method getMemberSetMethod;

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

            // Get getMemberSet(int) method from Island class - filters by minimum rank
            Class<?> islandClass = Class.forName("world.bentobox.bentobox.database.objects.Island");
            getMemberSetMethod = islandClass.getMethod("getMemberSet", int.class);

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

    // BentoBox rank constants
    private static final int MEMBER_RANK = 500;

    /**
     * Check if player has permission to modify blocks at location.
     * Returns true if:
     * - No island at location (wilderness)
     * - Player is in the island's member set with rank >= MEMBER (owner, sub-owner, member)
     *
     * Returns false if:
     * - Location is on an island and player is not at least a member
     * - Trusted players (rank 400) are excluded - they cannot break blocks
     */
    @SuppressWarnings("unchecked")
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

                // Get member set with minimum rank of MEMBER (500)
                // This excludes TRUSTED (400) and lower ranks
                Set<UUID> memberSet = (Set<UUID>) getMemberSetMethod.invoke(island, MEMBER_RANK);
                return memberSet.contains(player.getUniqueId());
            }

            return true;
        } catch (Exception e) {
            logger.warning("[LunarItems] BentoBox: Error checking permission - " + e.getMessage());
            return true;
        }
    }
}
