package me.dunescifye.lunaritems.utils;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.plugin.Plugin;

import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class BlockUtils {


    public static boolean isNaturallyGenerated(Block block) {
        List<String[]> lookup = getCoreProtect().blockLookup(block, 2147483647);
        if (lookup != null && !lookup.isEmpty()) {
            CoreProtectAPI.ParseResult parseResult = getCoreProtect().parseResult(lookup.get(0));
            if(!parseResult.getPlayer().startsWith("#") && parseResult.getActionId() == 1 && !parseResult.isRolledBack()){
                return false;
            } else {
                return true;
            }
        }
        return true;
    }


    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (plugin == null || !(plugin instanceof CoreProtect)) {
            System.out.println("core protect plugin not found");
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (CoreProtect.isEnabled() == false) {
            System.out.println("core protect api is not enabled");
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 10) {
            System.out.println("core protect api version is not supported");
            return null;
        }

        return CoreProtect;
    }

    public static void boneMealRadius(Location location, int radius) {
        Block block = location.getBlock();
        for (int x = -radius; x <= radius; x++){
            for (int y = -radius; y <= radius; y++){
                for (int z = -radius; z <= radius; z++){
                    Block b = block.getRelative(x, y, z);
                    b.applyBoneMeal(BlockFace.UP);
                }
            }
        }
    }
    public static void boneMealRadius(Location location, int radius, int amount) {
        Block block = location.getBlock();
        for (int x = -radius; x <= radius; x++){
            for (int y = -radius; y <= radius; y++){
                for (int z = -radius; z <= radius; z++){
                    Block b = block.getRelative(x, y, z);
                    for (int i = 0; i < amount; i++) {
                        b.applyBoneMeal(BlockFace.UP);
                    }
                }
            }
        }
    }

}
