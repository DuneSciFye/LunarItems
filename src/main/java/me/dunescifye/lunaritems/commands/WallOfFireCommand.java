package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.EntitySelectorArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WallOfFireCommand {

    public static void register() {
        new CommandAPICommand("walloffire")
            .withArguments(new EntitySelectorArgument.OnePlayer("Player"))
            .withArguments(new IntegerArgument("Time", 0))
            .executes((sender, args) -> {
                Player p = (Player) args.get("Player");
                Location eyeLocation = p.getEyeLocation();
                Location targetLocation;
                BlockIterator iterator = new BlockIterator(p, 20);

                maxDist: {
                    while (iterator.hasNext()) {
                        Location next = iterator.next().getLocation();
                        targetLocation = next;
                        Collection<Entity> nearbyEntities = targetLocation.getNearbyEntities(1, 1,1);
                        nearbyEntities.remove(p);
                        if (next.getBlock().getType() != Material.AIR || !nearbyEntities.isEmpty()) {
                            break maxDist;
                        }
                    }
                    targetLocation = eyeLocation.add(eyeLocation.getDirection().multiply(20));
                }


                // Place fire blocks to create a wall
                int height = 3;  // height of the wall
                int width = 5;   // width of the wall

                List<Block> blocks = new ArrayList<>();

                for (int y = 0; y < height; y++) {
                    if (p.getFacing() == BlockFace.NORTH || p.getFacing() == BlockFace.SOUTH) {
                        for (int x = -width / 2; x <= width / 2; x++) {
                            Location fireLocation = targetLocation.clone().add(x, y, 0);
                            Block block = fireLocation.getBlock();
                            if (block.getType() == Material.AIR) {
                                blocks.add(block);
                            }
                        }
                    } else {
                        for (int z = -width / 2; z <= width / 2; z++) {
                            Location fireLocation = targetLocation.clone().add(0, y, z);
                            Block block = fireLocation.getBlock();
                            if (block.getType() == Material.AIR) {
                                blocks.add(block);
                            }
                        }
                    }
                }

                for (Block block : blocks) {
                    block.setType(Material.FIRE, false);
                }

                Bukkit.getScheduler().runTaskLater(LunarItems.getPlugin(), () -> {
                    for (Block block : blocks) {
                        block.setType(Material.AIR);
                    }
                }, (int) args.get("Time"));

            })
            .withPermission("lunaritems.command.walloffire")
            .register();
    }

}
