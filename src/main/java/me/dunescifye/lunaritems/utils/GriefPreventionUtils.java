package me.dunescifye.lunaritems.utils;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Isolated GriefPrevention utility class.
 * Only loaded when GriefPrevention is present on the server,
 * preventing NoClassDefFoundError in FUtils.
 */
public class GriefPreventionUtils {

    public static boolean hasPermission(Player player, Location location) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claim != null && !claim.getOwnerID().equals(player.getUniqueId()) && !claim.hasExplicitPermission(player, ClaimPermission.Build)) {
            return false;
        }
        return true;
    }
}
