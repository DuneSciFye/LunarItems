package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;
import static me.dunescifye.lunaritems.files.AquaticItemsConfig.*;
import static me.dunescifye.lunaritems.files.Config.*;
import static me.dunescifye.lunaritems.files.NexusItemsConfig.*;
import static me.dunescifye.lunaritems.utils.Utils.keyItemID;
import static me.dunescifye.lunaritems.utils.Utils.runConsoleCommands;

public class BlockBreakListener implements Listener {

    public void blockBreakHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        Player p = e.getPlayer();
        Block b = e.getBlock();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(keyItemID, PersistentDataType.STRING)) {
                String itemID = container.get(keyItemID, PersistentDataType.STRING);
                if (itemID.equals("aquatichoe")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            Collection<ItemStack> drops = b.getDrops(item);
                            Location location = b.getLocation();

                            if (ThreadLocalRandom.current().nextInt(AquaticHoeFarmKeyChance) == 0) {
                                runConsoleCommands("crazycrates give v farm 1 " + p.getName(),
                                        "minecraft:sendmessage @a " + prefix + "&a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeStackOfCropsChance) == 0) {
                                drops.add(new ItemStack(b.getType(), 64));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeAxolotlSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeFrogSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
                            }

                            for (ItemStack drop : drops) {
                                b.getWorld().dropItemNaturally(location, drop);
                            }
                            Material material = b.getType();
                            Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(material));
                        } else {
                            e.setCancelled(true);
                        }
                    }
                } else if (itemID.equals("aquatichoemega")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            Location location = b.getLocation();
                            Collection<ItemStack> drops = b.getDrops(item);

                            if (ThreadLocalRandom.current().nextInt(AquaticHoeMegaFarmKeyChance) == 0) {
                                runConsoleCommands("crazycrates give v farm 1 " + p.getName(),
                                        "minecraft:sendmessage @a " + prefix + " &8&l▶ &a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeMegaStackOfCropsChance) == 0) {
                                drops.add(new ItemStack(b.getType(), 64));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeMegaAxolotlSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoeMegaFrogSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
                            }

                            for (ItemStack drop : drops) {
                                b.getWorld().dropItemNaturally(location, drop);
                            }

                            Material material = b.getType();
                            Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(material));
                        } else {
                            e.setCancelled(true);
                        }
                    }
                } else if (itemID.equals("aquatichoe2")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            Location location = b.getLocation();
                            Collection<ItemStack> drops = b.getDrops(item);

                            if (ThreadLocalRandom.current().nextInt(AquaticHoe2FarmKeyChance) == 0) {
                                runConsoleCommands("crazycrates give v farm 1 " + p.getName(),
                                        "minecraft:sendmessage @a " + prefix + " &8&l▶ &a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoe2StackOfCropsChance) == 0) {
                                drops.add(new ItemStack(b.getType(), 64));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoe2AxolotlSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
                            }
                            if (ThreadLocalRandom.current().nextInt(AquaticHoe2FrogSpawnEggChance) == 0) {
                                drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
                            }

                            for (ItemStack drop : drops) {
                                b.getWorld().dropItemNaturally(location, drop);
                            }

                            Material material = b.getType();
                            Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(material));
                        } else {
                            e.setCancelled(true);
                        }
                    }
                } else if (itemID.equals("nexushoe")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            if (ThreadLocalRandom.current().nextInt(NexusHoePyroFarmingXPChance) == 0) {
                                runConsoleCommands("pyrofarming addxp " + p.getName() + ThreadLocalRandom.current().nextInt(1, 6));
                            }
                        }
                    }
                } else if (itemID.equals("nexushoemega")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            if (ThreadLocalRandom.current().nextInt(NexusHoeMegaPyroFarmingXPChance) == 0) {
                                runConsoleCommands("pyrofarming addxp " + p.getName() + ThreadLocalRandom.current().nextInt(1, 6));
                            }
                        }
                    }
                } else if (itemID.equals("nexushoeo")) {
                    if (b.getBlockData() instanceof Ageable ageable) {
                        if (ageable.getAge() == ageable.getMaximumAge()) {
                            if (ThreadLocalRandom.current().nextInt(NexusHoeOPyroFarmingXPChance) == 0) {
                                runConsoleCommands("pyrofarming addxp " + p.getName() + ThreadLocalRandom.current().nextInt(1, 6));
                            }
                        }
                    }
                }
            }
        }
    }

}
