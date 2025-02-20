package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import eu.decentsoftware.holograms.api.DHAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.AncienttItemsConfig;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.FUtils;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static me.dunescifye.lunaritems.LunarItems.*;
import static me.dunescifye.lunaritems.files.AquaticItemsConfig.*;
import static me.dunescifye.lunaritems.files.Config.prefix;
import static me.dunescifye.lunaritems.files.Config.radiusMiningDisabledWorlds;
import static me.dunescifye.lunaritems.files.NexusItemsConfig.*;
import static me.dunescifye.lunaritems.utils.BlockUtils.*;
import static me.dunescifye.lunaritems.utils.FUtils.isInClaimOrWilderness;
import static me.dunescifye.lunaritems.utils.Utils.*;

public class BlockBreakListener implements Listener {

    public void blockBreakHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onBlockDrop(ItemSpawnEvent e) {
        Block b = e.getLocation().getBlock();
        if (e.getEntity().getItemStack().getType() != b.getType() && b.getType() != Material.AIR) return;
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        String blockID = blockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (blockID == null) return;
        blockContainer.remove(LunarItems.keyEIID);
        e.setCancelled(true);
        switch (blockID) {
            case "teleport_pad" -> b.getWorld().dropItemNaturally(b.getLocation(), BlocksConfig.teleport_pad);
            case "elevator" -> b.getWorld().dropItemNaturally(b.getLocation(), BlocksConfig.elevator);
        }
        //Remove hologram
        if (LunarItems.decentHologramsEnabled) {
            String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
            if (hologramID != null)
                DHAPI.removeHologram(hologramID);
        }
    }

    // Monitor priority so it ignores when EI cancels
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Block b = e.getBlock();
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        String blockID = blockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (blockID != null) {
            switch (blockID) {
                case "teleport_pad" -> {
                    //Drop custom item
                    Location loc = b.getLocation();
                    e.setDropItems(false);
                    b.getWorld().dropItemNaturally(loc, BlocksConfig.teleport_pad);

                    //Remove hologram
                    if (LunarItems.decentHologramsEnabled) {
                        String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
                        if (hologramID != null)
                            DHAPI.removeHologram(hologramID);
                    }

                    //Make linked teleport pad not work
                    Location targetLocation = blockContainer.get(LunarItems.keyLocation, DataType.LOCATION);
                    if (targetLocation != null) {
                        Block targetBlock = b.getWorld().getBlockAt(targetLocation);
                        PersistentDataContainer targetBlockContainer = new CustomBlockData(targetBlock, LunarItems.getPlugin());
                        if (Objects.equals(targetBlockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING), "teleport_pad"))
                            targetBlockContainer.remove(LunarItems.keyLocation);
                    }
                    b.setType(Material.AIR);
                }
                case
                    "elevator" -> {
                    //Drop custom item
                    Location loc = b.getLocation();
                    e.setDropItems(false);
                    b.getWorld().dropItemNaturally(loc, BlocksConfig.elevator);

                    //Remove hologram
                    if (LunarItems.decentHologramsEnabled) {
                        String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
                        if (hologramID != null)
                            DHAPI.removeHologram(hologramID);
                    }

                    b.setType(Material.AIR);
                }
            }
        }

        ItemStack item = p.getInventory().getItemInMainHand();

        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;

        //Hoes
        if (b.getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge() || b.getType() == Material.SUGAR_CANE) {
                Collection<ItemStack> drops = b.getDrops(item);
                Location location = b.getLocation();
                switch (Objects.requireNonNull(itemID)) {
                    case "aquatichoe" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance, AquaticHoeAxolotlSpawnerChance, AquaticHoeFrogSpawnerChance);
                    case "aquatichoemega" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance, AquaticHoeMegaAxolotlSpawnerChance, AquaticHoeMegaFrogSpawnerChance);
                    case "aquatichoe2" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance, AquaticHoe2AxolotlSpawnerChance, AquaticHoe2FrogSpawnerChance);
                    case "nexushoe" ->
                        handleNexusHoe(p, NexusHoePyroFarmingXPChance);
                    case "nexushoemega" ->
                        handleNexusHoe(p, NexusHoeMegaPyroFarmingXPChance);
                    case "nexushoeo" ->
                        handleNexusHoe(p, NexusHoeOPyroFarmingXPChance);
                    case "ancientthoe", "ancientthoeo" ->
                        handleAncienttHoe(drops, p, b, location, item, meta, container, AncienttItemsConfig.AncienttHoeParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeFireworkChance, AncienttItemsConfig.AncienttHoeFarmKeyChance, AncienttItemsConfig.AncienttHoeParrotSpawnerChance, AncienttItemsConfig.AncienttHoeInfiniteSeedPouchChance);
                    case "ancientthoemega"->
                        handleAncienttHoe(drops, p, b, location, item, meta, container, AncienttItemsConfig.AncienttHoeMegaParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeMegaFireworkChance, AncienttItemsConfig.AncienttHoeMegaFarmKeyChance, AncienttItemsConfig.AncienttHoeMegaParrotSpawnerChance, AncienttItemsConfig.AncienttHoeMegaInfiniteSeedPouchChance);
                    case "twistedhoe", "twistedhoe2", "twistedhoemega" -> {
                        dropAllItemStacks(location.getWorld(), location, drops);

                        //Auto replant except for Sugar Cane, doesn't require seed
                        if (b.getType() != Material.SUGAR_CANE) {
                            Material material = b.getType();
                            Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(material));
                        }
                    }
                }
            }
        }
        //Not a hoe
        else {
            if (radiusMiningDisabledWorlds.contains(p.getWorld().getName())) return;

            if (itemID.contains("nightmarepick") && testBlock(b, ores)) {
                Collection<ItemStack> drops = new ArrayList<>();

                veinMineOres25ChanceDouble(b, drops, b.getType(), p, item);

                PlayerInventory inv = p.getInventory();
                for (ItemStack drop : mergeSimilarItemStacks(drops)) {
                    if (inv.firstEmpty() == -1) {
                        dropAllItemStacks(b.getWorld(), b.getLocation(), drops);
                        break;
                    }
                    else {
                        inv.addItem(drop);
                        drops.remove(drop);
                    }
                }
            }

            World world = b.getWorld();
            Location loc = b.getLocation();

            if (container.has(LunarItems.keyRadius, PersistentDataType.DOUBLE)) {
                e.setDropItems(false);
                int radius = (int) (double) container.getOrDefault(keyRadius, PersistentDataType.DOUBLE, 0.0);
                //BreakInFacing
                if (container.has(LunarItems.keyDepth, PersistentDataType.DOUBLE)) {
                    int depth = (int) (double) container.getOrDefault(keyDepth, PersistentDataType.DOUBLE, 0.0);
                    //Custom drop
                    String customDrop = container.get(LunarItems.keyDrop, PersistentDataType.STRING);
                    if (customDrop != null && !customDrop.isEmpty()) {
                        e.setDropItems(false);
                        if (itemID.contains("aetheraxe") && testBlock(b, axePredicates)) {
                            // Will do 3x3x3 if axe is being thrown, otherwise use item's radius and depth
                            Collection<ItemStack> drops = p.hasMetadata("ignoreBlockBreak") ? breakInFacing(b, 0, 1, p, axePredicates) : breakInFacing(b, radius, depth, p, axePredicates);
                            if (Objects.equals(customDrop, "")) { // No custom Sapling Drops
                                drops = p.getInventory().addItem(drops.toArray(new ItemStack[0])).values();
                            } else {
                                Material sapling = Material.getMaterial(customDrop);
                                if (sapling == null) return;
                                drops = drops.stream()
                                    .map(drop -> drop.getType().toString().contains("_SAPLING") ? new ItemStack(sapling, drop.getAmount()) : drop)
                                    .collect(Collectors.toList());
                            }
                            dropAllItemStacks(world, loc, drops);
                        } else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                            //Change log drops
                            String material = b.getType().toString();
                            Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates).stream()
                                .map(drop -> {
                                    Material newMaterial = customDrop.equalsIgnoreCase("STRIPPED") ? Material.getMaterial("STRIPPED_" + material) : Material.getMaterial(material.substring(0, material.length() - 3) + drop);
                                    return (drop.getType().toString().contains("LOG") && newMaterial != null) ? new ItemStack(newMaterial, drop.getAmount()) : drop;
                                })
                                .collect(Collectors.toList());
                            //Break non logs
                            drops.addAll(breakInFacing(b, radius, depth, p, axePredicates));
                            dropAllItemStacks(world, loc, drops);
                        } else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                            Material mat = Material.getMaterial(customDrop);
                            if (mat == null) return;
                            dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, shovelPredicates).stream()
                                .map(drop -> new ItemStack(mat, drop.getAmount()))
                                .collect(Collectors.toList()));
                        } else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                            Material mat = Material.getMaterial(customDrop);
                            if (mat == null) return;
                            dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, pickaxePredicates).stream()
                                .map(drop -> new ItemStack(mat, drop.getAmount()))
                                .collect(Collectors.toList()));
                        }
                    }
                    //No custom drop
                    else if (itemID.contains("aethershovel") && testBlock(b, shovelPredicates)) {
                        dropAllItemStacks(world, loc, p.getInventory().addItem(breakInFacing(b, radius, depth, p, shovelPredicates).toArray(new ItemStack[0])).values());
                    } else if (itemID.contains("soulpick") && testBlock(b, pickaxePredicates)) {
                        FUtils.breakInFacingDoubleOres(b, radius, depth, p, pickaxePredicates, e.getExpToDrop());
                    }
                    else if (itemID.contains("ancienttpick") && testBlock(b, ancientPickPredicates))
                        dropAllItemStacks(world, loc, BlockUtils.breakInFacing(b, radius, depth, p, ancientPickPredicates));
                    else if (itemID.contains("ancienttshovel") && testBlock(b, ancientShovelPredicates))
                        dropAllItemStacks(world, loc, BlockUtils.breakInFacing(b, radius, depth, p, ancientShovelPredicates));
                    else if (itemID.contains("ancienttaxe") && testBlock(b, ancientAxePredicates))
                        dropAllItemStacks(world, loc, BlockUtils.breakInFacing(b, radius, depth, p, ancientAxePredicates));
                    else if (itemID.contains("catsagepick") && testBlock(b, pickaxePredicates)) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        List<ItemStack> ores = new ArrayList<>();
                        List<ItemStack> glazedDrops = new ArrayList<>();
                        boolean terracotta = container.getOrDefault(keyTerracotta, PersistentDataType.STRING, "Disabled").equals("Enabled");

                        // Partition drops into smelted ores, glazed terracotta, and remaining drops
                        drops.removeIf(drop -> {
                            Material dropType = drop.getType();

                            // Collect smelted ores to add to inventory
                            if (smeltedOres.containsKey(dropType)) {
                                ores.add(new ItemStack(smeltedOres.get(dropType), drop.getAmount()));
                                return true; // Remove from drops
                            }

                            // Collect glazed terracotta to be dropped normally
                            if (terracotta && glazeTerracotta.containsKey(dropType)) {
                                glazedDrops.add(new ItemStack(glazeTerracotta.get(dropType), drop.getAmount()));
                                return true; // Remove original terracotta from drops
                            }

                            return false; // Keep other items in drops
                        });

                        // Add smelted ores to the player's inventory, drop the remaining items
                        drops.addAll(p.getInventory().addItem(ores.toArray(new ItemStack[0])).values());
                        // Add the glazed terracotta to the remaining drops
                        drops.addAll(glazedDrops);
                        dropAllItemStacks(world, loc, drops);

                    }
                    else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates))
                        dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, axePredicates));
                    else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates))
                        dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, pickaxePredicates));
                    else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates))
                        dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, shovelPredicates));
                }
                // BreakInRadius
                else {
                    if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                        dropAllItemStacks(world, loc, breakInRadius(b, radius, p, shovelPredicates));
                    }
                    else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates))
                        dropAllItemStacks(world, loc, breakInRadius(b, radius, p, pickaxePredicates));
                    else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates))
                        dropAllItemStacks(world, loc, breakInRadius(b, radius, p, axePredicates));
                }
            }
        }

    }

    private void handleAquaticHoe(Collection<ItemStack> drops, Player p, Block block, Location location, int farmKeyChance, int stackOfCropsChance, int axolotlSpawnEggChance, int frogSpawnEggChance, int axolotlSpawnerChance, int frogSpawnerChance) {
        if (ThreadLocalRandom.current().nextInt(farmKeyChance) == 0) {
            Utils.runConsoleCommands("crazycrates give v farm 1 " + p.getName(), "minecraft:sendmessage @a " + prefix + "&a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Farm Key")));
        }
        if (ThreadLocalRandom.current().nextInt(stackOfCropsChance) == 0) {
            drops.add(new ItemStack(block.getType(), 64));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Stack of Crops")));
        }
        if (ThreadLocalRandom.current().nextInt(axolotlSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "an Axolotl Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(frogSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Frog Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(axolotlSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "AXOLOTL").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "an Axolotl Spawner")));
        }
        if (ThreadLocalRandom.current().nextInt(frogSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "FROG").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Frog Spawner")));
        }

        //Drop drops at location
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(location, drop);
        }

        //Auto replant except for Sugar Cane, doesn't require seed
        if (block.getType() != Material.SUGAR_CANE) {
            Material material = block.getType();
            Bukkit.getScheduler().runTask(getPlugin(), () -> block.setType(material));
        }
    }

    private void handleAncienttHoe(Collection<ItemStack> drops, Player p, Block block, Location location, ItemStack item, ItemMeta meta, PersistentDataContainer container, int parrotSpawnEggChance, int fireworkChance, int farmKeyChance, int parrotSpawnerChance, int infiniteSeedPouchChance) {
        if (ThreadLocalRandom.current().nextInt(farmKeyChance) == 0) {
            Utils.runConsoleCommands("crazycrates give v farm 1 " + p.getName(), "minecraft:sendmessage @a " + prefix + "&a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Farm Key")));
        }
        if (ThreadLocalRandom.current().nextInt(parrotSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.PARROT_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Parrot Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(fireworkChance) == 0) {
            drops.add(new ItemStack(Material.FIREWORK_ROCKET));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Firework Rocket")));
        }
        if (ThreadLocalRandom.current().nextInt(parrotSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "PARROT").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Parrot Spawner")));
        }
        if (ThreadLocalRandom.current().nextInt(infiniteSeedPouchChance) == 0) {
            Utils.runConsoleCommands(Config.infinitePouchCommand.replace("%player%", p.getName()).replace("%type%", AncienttItemsConfig.AncienttHoeInfiniteSeedPouchID));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Infinite Seed Pouch")));
        }

        //Drop drops at location
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(location, drop);
        }

        //Auto replant except for Sugar Cane, doesn't require seed
        if (block.getType() != Material.SUGAR_CANE) {
            Material material = block.getType();
            /*
            if (block.getBlockData() instanceof Directional directional) {
                BlockFace face = directional.getFacing();
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    block.setType(material);
                    BlockData newData = block.getBlockData();
                    ((Directional) newData).setFacing(face);
                    block.setBlockData(newData);
                });
            } else {

             */
                Bukkit.getScheduler().runTask(getPlugin(), () -> block.setType(material));
            //}
        }
    }

    private void handleNexusHoe(Player player, int xpChance) {
        if (ThreadLocalRandom.current().nextInt(xpChance) == 0) {
            Utils.runConsoleCommands("pyrofarming addxp " + ThreadLocalRandom.current().nextInt(1, 6) + " " + player.getName());
        }
    }

    private void veinMineOres25ChanceDouble(Block center, Collection<ItemStack> drops, Material material, Player player, ItemStack item) {
        for (Block b : getBlocksInRadius(center, 1)) {
            if (b.getType().equals(material)) {
                //Testing claim
                if (isInClaimOrWilderness(player, b.getLocation())) {
                    drops.addAll(b.getDrops(item));
                    if (Utils.isNaturallyGenerated(b) && ThreadLocalRandom.current().nextInt(4) == 0) drops.addAll(b.getDrops(item));
                    b.setType(Material.AIR);
                    this.veinMineOres25ChanceDouble(b, drops, material, player, item);
                }
            }
        }
    }

}
