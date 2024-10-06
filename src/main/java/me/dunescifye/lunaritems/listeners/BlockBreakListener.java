package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import eu.decentsoftware.holograms.api.DHAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.AncienttItemsConfig;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.LunarItems.*;
import static me.dunescifye.lunaritems.files.AquaticItemsConfig.*;
import static me.dunescifye.lunaritems.files.Config.prefix;
import static me.dunescifye.lunaritems.files.Config.radiusMiningDisabledWorlds;
import static me.dunescifye.lunaritems.files.NexusItemsConfig.*;

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
    }

/*
    @EventHandler
    public void onBlockDrop(ItemSpawnEvent e) {
        Block b = e.getLocation().getBlock();
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
            if (!e.getEntity().getItemStack().hasItemMeta()) {
                e.setCancelled(true);
            } else {
                blockContainer.remove(LunarItems.keyEIID);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockPhysicsEvent e) {
        Block b = e.getBlock();
        if (e.getChangedType() != Material.AIR) return;
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        String blockID = blockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (blockID == null) return;

        switch (blockID) {
            case "teleport_pad" -> {
                //Drop custom item
                Location loc = b.getLocation();
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
            }
            case "elevator" -> {
                //Drop custom item
                Location loc = b.getLocation();
                b.getWorld().dropItemNaturally(loc, BlocksConfig.elevator);

                //Remove hologram
                if (LunarItems.decentHologramsEnabled) {
                    String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
                    if (hologramID != null)
                        DHAPI.removeHologram(hologramID);
                }
            }
        }
    }

 */


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
                        BlockUtils.dropAllItemStacks(drops, location.getWorld(), location);

                        //Auto replant except for Sugar Cane, doesn't require seed
                        if (b.getType() != Material.SUGAR_CANE) {
                            Material material = b.getType();
                            Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(material));
                        }
                    }
                }
            } else {
                //Cancel breaking if plant not fully grown
                e.setCancelled(true);
            }
        }
        //Not a hoe
        else {
            if (radiusMiningDisabledWorlds.contains(p.getWorld().getName())) return;
            //Ancient Tools
            if (BlockUtils.inWhitelist(b, BlockUtils.ancienttPickaxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.pickaxeBlacklist) && itemID.contains("ancienttpick")) {
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttPickaxeWhitelist, BlockUtils.pickaxeBlacklist);
            }
            else if (BlockUtils.inWhitelist(b, BlockUtils.ancienttShovelWhitelist) && itemID.contains("ancienttshovel")) {
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttShovelWhitelist);
            }
            else if (BlockUtils.inWhitelist(b, BlockUtils.ancienttAxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.axeBlacklist) && itemID.contains("ancienttaxe")) {
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttAxeWhitelist, BlockUtils.axeBlacklist);
            }
            //Aether Axe
            else if (itemID.contains("aetheraxe") && BlockUtils.inWhitelist(b, BlockUtils.axeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.axeBlacklist)) {
                String drop = container.get(LunarItems.keyDrop, PersistentDataType.STRING);
                if (p.hasMetadata("ignoreBlockBreak")) {
                    if (Objects.equals(drop, "")) {
                        e.setDropItems(false);
                        BlockUtils.breakInFacingAutoPickup(b, 0, 1, p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
                    } else {
                        if (drop != null) {
                            e.setDropItems(false);
                            BlockUtils.breakInFacingAutoPickup(b, 0, 1, p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist, "_SAPLING", Material.getMaterial(drop));
                        }
                    }
                    return;
                }

                if (Objects.equals(drop, "")) {
                    e.setDropItems(false);
                    BlockUtils.breakInFacingAutoPickup(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 1.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
                } else {
                    if (drop != null) {
                        e.setDropItems(false);
                        BlockUtils.breakInFacingAutoPickup(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 1.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist, "_SAPLING", Material.getMaterial(drop));
                    }
                }
            } else if (itemID.contains("aethershovel") && BlockUtils.inWhitelist(b, BlockUtils.shovelWhitelist)) {
                e.setDropItems(false);
                BlockUtils.breakInFacingAutoPickup(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.shovelWhitelist);
            } else if (itemID.contains("soulpick") && BlockUtils.inWhitelist(b, BlockUtils.pickaxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.pickaxeBlacklist)) {
                BlockUtils.breakInFacingDoubleOres(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist);
            }

            else if (container.has(LunarItems.keyRadius, PersistentDataType.DOUBLE)) {
                //BreakInFacing
                if (container.has(LunarItems.keyDepth, PersistentDataType.DOUBLE)) {
                    //Custom drop
                    String drop = container.get(LunarItems.keyDrop, PersistentDataType.STRING);
                    if (drop != null && !drop.isEmpty()) {
                        drop = drop.toUpperCase();
                        if (BlockUtils.inWhitelist(b, BlockUtils.axeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.axeBlacklist) && item.getType().equals(Material.NETHERITE_AXE)) {
                            //Change log drops
                            e.setDropItems(false);
                            String material = b.getType().toString();
                            if (drop.equals("STRIPPED")) {
                                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist, "_LOG", Material.getMaterial(drop + "_" + material));
                            } else {
                                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist, "_LOG", Material.getMaterial(material.substring(0, material.length() - 3) + drop));
                            }
                            //Break non logs
                            BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
                        } else if (BlockUtils.inWhitelist(b, BlockUtils.shovelWhitelist) && item.getType().equals(Material.NETHERITE_SHOVEL)) {
                            //Change shovel drops
                            e.setDropItems(false);
                            BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.shovelWhitelist, Material.getMaterial(drop));
                        } else if (BlockUtils.inWhitelist(b, BlockUtils.pickaxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.pickaxeBlacklist) &&  item.getType().equals(Material.NETHERITE_PICKAXE)) {
                            e.setDropItems(false); //Change pickaxe drops
                            BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist, Material.getMaterial(drop));
                        }
                    }
                    //No custom drop
                    else if (BlockUtils.inWhitelist(b, BlockUtils.axeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.axeBlacklist) && item.getType().equals(Material.NETHERITE_AXE)) {
                        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
                    }
                    else if (BlockUtils.inWhitelist(b, BlockUtils.pickaxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.pickaxeBlacklist)&& item.getType().equals(Material.NETHERITE_PICKAXE)) {
                        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist);
                    }
                    else if (BlockUtils.inWhitelist(b, BlockUtils.shovelWhitelist) && item.getType().equals(Material.NETHERITE_SHOVEL)) {
                        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.shovelWhitelist);
                    }
                } else {
                    //BreakInRadius
                    if (BlockUtils.inWhitelist(b, BlockUtils.shovelWhitelist) && item.getType().equals(Material.NETHERITE_SHOVEL)) {
                        BlockUtils.breakInRadius(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.shovelWhitelist);
                    }
                    else if (BlockUtils.inWhitelist(b, BlockUtils.pickaxeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.pickaxeBlacklist) && item.getType().equals(Material.NETHERITE_PICKAXE))
                        BlockUtils.breakInRadius(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist);
                    else if (BlockUtils.inWhitelist(b, BlockUtils.axeWhitelist) && BlockUtils.notInBlacklist(b, BlockUtils.axeBlacklist) && item.getType().equals(Material.NETHERITE_AXE))
                        BlockUtils.breakInRadius(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
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

    private void dropOreBlock(Block b) {
        for (Predicate<Block> predicate : BlockUtils.regularOres) {
            if (predicate.test(b)) {
                String material = b.getType().toString();
                Utils.dropItems(b.getLocation(), new ItemStack(Material.getMaterial(material.substring(0, material.length() - 3) + "BLOCK")));
                return;
            }
        }
        for (Predicate<Block> predicate : BlockUtils.deepslateOres) {
            if (predicate.test(b)) {
                String material = b.getType().toString();
                Utils.dropItems(b.getLocation(), new ItemStack(Material.getMaterial(material.substring(10, material.length() - 3) + "BLOCK")));
                return;
            }
        }
    }

}
