package me.dunescifye.lunaritems.utils;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class BlockUtils {

    public static void boneMealRadius(Location location, int radius) {
        Block block = location.getBlock();
        for (int x = -radius; x <= radius; x++){
            for (int y = -radius; y <= radius; y++){
                for (int z = -radius; z <= radius; z++){
                    Block b = block.getRelative(x, y, z);
                    b.applyBoneMeal(BlockFace.UP);
                    System.out.println("a");
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
