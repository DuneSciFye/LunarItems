package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.List;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.utils.Utils.testBlock;

public class FixWorldCommand {

    public static void register() {

        List<List<Predicate<Block>>> blocks = List.of(
            List.of( // Whitelist
                block -> block.getType().equals(Material.GRASS_BLOCK),
                block -> block.getType().equals(Material.DIRT),
                block -> block.getType().equals(Material.SAND),
                block -> block.getType().equals(Material.GRAVEL)
            ),
            List.of(
            )
        );

        new CommandAPICommand("fixworld")
            .withArguments(new IntegerArgument("radius", 0))
            .executesPlayer((p, args) -> {
                Block origin = p.getWorld().getBlockAt(((int) p.getX()), (int) p.getY(), ((int) p.getZ()));
                int radius = args.getUnchecked("radius");

                for (int x = -radius; x < radius; x++) {
                    for (int z = -radius; z < radius; z++) {
                        for (int y = 0; y < 4; y++) {

                            Block b = origin.getRelative(x, -y, z);
                            if (b.getType().equals(Material.AIR) || b.getType().equals(Material.SHORT_GRASS)) {
                                if (testBlock(b.getRelative(BlockFace.DOWN), blocks)) {

                                    // North - South
                                    if (testBlock(b.getRelative(BlockFace.NORTH), blocks)) {
                                        if (testBlock(b.getRelative(BlockFace.SOUTH), blocks) || testBlock(b.getRelative(BlockFace.SOUTH, 2), blocks)) {
                                            b.setType(Material.GRASS_BLOCK);
                                        }
                                    }
                                    // South - North
                                    if (testBlock(b.getRelative(BlockFace.SOUTH), blocks)) {
                                        if (testBlock(b.getRelative(BlockFace.NORTH), blocks) || testBlock(b.getRelative(BlockFace.NORTH, 2), blocks)) {
                                            b.setType(Material.GRASS_BLOCK);
                                        }
                                    }
                                    // East - West
                                    if (testBlock(b.getRelative(BlockFace.EAST), blocks)) {
                                        if (testBlock(b.getRelative(BlockFace.WEST), blocks) || testBlock(b.getRelative(BlockFace.WEST, 2), blocks)) {
                                            b.setType(Material.GRASS_BLOCK);
                                        }
                                    }
                                    // West - East
                                    if (testBlock(b.getRelative(BlockFace.WEST), blocks)) {
                                        if (testBlock(b.getRelative(BlockFace.EAST), blocks) || testBlock(b.getRelative(BlockFace.EAST, 2), blocks)) {
                                            b.setType(Material.GRASS_BLOCK);
                                        }
                                    }

                                }
                            }

                        }
                    }
                }

            })
            .withPermission("lunaritems.fixworld")
            .register();
    }

}
