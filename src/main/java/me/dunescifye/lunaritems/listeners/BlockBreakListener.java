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
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static me.dunescifye.lunaritems.LunarItems.*;
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

    @EventHandler
    public void onBlockDropItems(BlockDropItemEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        Inventory inv = p.getInventory();
        List<Item> items = e.getItems();

        if (item.hasItemMeta()) {
            PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
            String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID != null) {
                if (e.getBlockState().getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
                    // Auto Pickup crops
                    if (itemID.contains("soulhoe") || itemID.contains("demonslimehoe"))
                        items.removeIf(drop -> inv.addItem(drop.getItemStack()).isEmpty());
                }
            }
        }

        ItemStack offhandItem = p.getInventory().getItemInOffHand();
        if (offhandItem.hasItemMeta()) {
            PersistentDataContainer pdc = offhandItem.getItemMeta().getPersistentDataContainer();
            String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID != null) {
                if (itemID.contains("creakingoregen")) {
                    for (Item drop : items) {
                        Material smeltedMat = smeltedOres.get(drop.getItemStack().getType());
                        if (smeltedMat != null) drop.setItemStack(drop.getItemStack().withType(smeltedMat));
                    }
                }
            }
        }

    }

    // Monitor priority so it ignores when EI cancels
    @EventHandler (priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        // Return if EI item has desactiveDrop
        if (!e.isDropItems()) return;

        Player p = e.getPlayer();
        Block b = e.getBlock();
        // Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        String blockID = blockContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (blockID != null) {
            switch (blockID) {
                case "teleport_pad" -> {
                    // Drop custom item
                    Location loc = b.getLocation();
                    e.setDropItems(false);
                    b.getWorld().dropItemNaturally(loc, BlocksConfig.teleport_pad);

                    // Remove hologram
                    if (LunarItems.decentHologramsEnabled) {
                        String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
                        if (hologramID != null)
                            DHAPI.removeHologram(hologramID);
                    }

                    // Make linked teleport pad not work
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
                    // Drop custom item
                    Location loc = b.getLocation();
                    e.setDropItems(false);
                    b.getWorld().dropItemNaturally(loc, BlocksConfig.elevator);

                    // Remove hologram
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

        World world = b.getWorld();
        Location loc = b.getLocation();

        // Hoes
        if (item.getType() == Material.NETHERITE_HOE && b.getType().toString().contains("_STEM")) {
            e.setCancelled(true);
            return;
        }
        if (b.getBlockData() instanceof Ageable ageable && b.getType() != Material.SUGAR_CANE && b.getType() != Material.CACTUS && item.getType() == Material.NETHERITE_HOE) {
            if (ageable.getAge() == ageable.getMaximumAge()) {
                Collection<ItemStack> drops = b.getDrops(item);
                switch (Objects.requireNonNull(itemID)) {
                    case "nexushoe" ->
                      handleNexusHoe(p, NexusHoePyroFarmingXPChance);
                    case "nexushoemega" ->
                      handleNexusHoe(p, NexusHoeMegaPyroFarmingXPChance);
                    case "nexushoeo" ->
                      handleNexusHoe(p, NexusHoeOPyroFarmingXPChance);
                    case "ancientthoe", "ancientthoeo" ->
                      handleAncienttHoe(drops, p, b, loc, item, meta, container, AncienttItemsConfig.AncienttHoeParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeFireworkChance, AncienttItemsConfig.AncienttHoeFarmKeyChance, AncienttItemsConfig.AncienttHoeParrotSpawnerChance, AncienttItemsConfig.AncienttHoeInfiniteSeedPouchChance);
                    case "ancientthoemega"->
                      handleAncienttHoe(drops, p, b, loc, item, meta, container, AncienttItemsConfig.AncienttHoeMegaParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeMegaFireworkChance, AncienttItemsConfig.AncienttHoeMegaFarmKeyChance, AncienttItemsConfig.AncienttHoeMegaParrotSpawnerChance, AncienttItemsConfig.AncienttHoeMegaInfiniteSeedPouchChance);
                }
                // Auto Sell
                if (("Enabled").equals(container.get(keyAutoSell, PersistentDataType.STRING))) {
                    e.setDropItems(false);
                    BigDecimal totalPrice = BigDecimal.valueOf(container.get(keyMoney, PersistentDataType.DOUBLE));
                    Double price = container.get(new NamespacedKey("score", "score-" + b.getType().toString().toLowerCase()), PersistentDataType.DOUBLE);
                    if (price != null) {
                        totalPrice = totalPrice.add(BigDecimal.valueOf(price));
                        String formattedTotal = totalPrice.setScale(4, RoundingMode.HALF_UP).toPlainString();
                        runConsoleCommands("ei console-modification set variable " + p.getName() + " -1 " +
                          "money " + formattedTotal);
                    }
                }
                // Auto Replant
                if (itemID.contains("celestialhoe") ||
                  itemID.contains("demonslimehoe") ||
                  itemID.contains("soulhoem") ||
                  itemID.contains("catsagehoe") ||
                  itemID.contains("twistedhoe") ||
                  itemID.contains("nightmarehoe") ||
                  itemID.contains("aquatichoe") ||
                  itemID.contains("creakinghoe") ||
                  itemID.contains("abysshoem") ||
                  itemID.contains("discoveryhoe2")) {
                    replant(b);
                }
                // 65% chance to auto replant for regular soul hoe and soul hoeo
                else if (itemID.contains("soulhoe")) {
                    if (ThreadLocalRandom.current().nextInt(100) <= 65) {
                        replant(b);
                    }
                }
                // Double Drops
                if (itemID.contains("catsagehoe") || itemID.contains("twistedhoe"))
                    dropAllItemStacks(world, loc, drops);
                // Aether Hoe
                if (itemID.contains("aetherhoem")) {
                    if (ThreadLocalRandom.current().nextInt(100) <= 15) {
                        dropAllItemStacks(world, loc, drops);
                        dropAllItemStacks(world, loc, drops);
                    }
                    replant(b, 1);
                }
                else if (itemID.contains("nightspirehoe")) {
                    replant(b, 2);
                }
                else if (itemID.contains("aetherhoe")) {
                    if (ThreadLocalRandom.current().nextInt(10) == 0) {
                        dropAllItemStacks(world, loc, drops);
                        dropAllItemStacks(world, loc, drops);
                    }
                    replant(b, 1);
                }
                // Auto replant two stages higher
                else if (itemID.contains("angelichoem") || itemID.contains("krampushoem")) {
                    replant(b, 2);
                }
                // Auto replant two stages higher
                else if (itemID.contains("sunhoem")) {
                    replant(b, 2);
                }
                // Auto replant two stages higher from inv
                else if (itemID.contains("sunhoe")) {
                    replantFromInv(p, b, 2);
                }
                // Auto replant 1 stage higher
                else if (itemID.contains("angelichoe") || itemID.contains("krampushoe") || itemID.contains("sakurahoe")) {
                    replant(b, 1);
                }
                else if (itemID.contains("sakurahoem")) {
                    Double cropsFarmed = container.get(keyCropsFarmed, PersistentDataType.DOUBLE);
                    if (cropsFarmed != null) {
                        container.set(keyCropsFarmed, PersistentDataType.DOUBLE, cropsFarmed + 1);
                        item.setItemMeta(meta);
                    }
                }
            }
            else e.setCancelled(true);
            return;
        }

        //Not a hoe

        // Radius pickaxes
        if (radiusMiningDisabledWorlds.contains(p.getWorld().getName())) return;

        List<Item> items = null;

        if (container.has(LunarItems.keyRadius, PersistentDataType.DOUBLE)) {
            int radius = (int) (double) container.getOrDefault(keyRadius, PersistentDataType.DOUBLE, 0.0);
            // BreakInFacing
            if (container.has(LunarItems.keyDepth, PersistentDataType.DOUBLE)) {
                int depth = (int) (double) container.getOrDefault(keyDepth, PersistentDataType.DOUBLE, 0.0);
                // Custom drop
                String customDrop = container.get(LunarItems.keyDrop, PersistentDataType.STRING);
                if (itemID.contains("aetheraxe") && testBlock(b, axePredicates)) {
                    e.setDropItems(false);
                    // Will do 3x3x3 if axe is being thrown, otherwise use item's radius and depth
                    Collection<ItemStack> drops = p.hasMetadata("ignoreBlockBreak") ? breakInFacing(b, 0, 1, p, axePredicates) : breakInFacing(b, radius, depth, p, axePredicates);
                    if (b.getBlockData() instanceof Door door && door.getHalf() == Bisected.Half.TOP) drops.add(new ItemStack(door.getMaterial()));
                    Material sapling = Material.getMaterial(customDrop);
                    Collection<ItemStack> saplings = new ArrayList<>();
                    if (sapling != null) {
                        drops.removeIf(drop -> {
                            if (drop.getType().toString().contains("_SAPLING")) {
                                saplings.add(new ItemStack(sapling, drop.getAmount()));
                                return true;
                            }
                            return false;
                        });
                    }
                    drops.addAll(saplings);
                    Inventory inv = p.getInventory();
                    drops.removeIf(drop -> inv.addItem(drop).isEmpty());
                    items = dropAllItemStacks(world, loc, drops);
                }
                if (customDrop != null && !customDrop.isEmpty()) {
                    if (itemID.contains("catsageaxe") && testBlock(b, axePredicates)) {
                        e.setDropItems(false);
                        Material mat = Material.getMaterial(customDrop);
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates);
                        if (mat != null)
                            drops = drops.stream()
                              .map(drop -> Tag.LOGS.isTagged(drop.getType()) ? new ItemStack(mat, drop.getAmount()) : drop)
                              .toList();
                        items = dropAllItemStacks(world, loc, drops);
                    } else if (itemID.contains("aquaticaxe") && testBlock(b, axePredicates)) {
                        e.setDropItems(false);
                        //Change log drops
                        String material = b.getType().toString().toUpperCase();
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates);
                        Collection<ItemStack> newDrops = new ArrayList<>();
                        Material newMaterial = customDrop.equalsIgnoreCase("STRIPPED") ? Material.getMaterial("STRIPPED_" + material) : Material.getMaterial(material.substring(0, material.length() - 3) + customDrop.toUpperCase());
                        drops.removeIf(drop -> {
                            if (drop.getType().toString().contains("LOG") && newMaterial != null) {
                                newDrops.add(new ItemStack(newMaterial, drop.getAmount()));
                                return true;
                            }
                            return false;
                        });
                        drops.addAll(newDrops);
                        items = dropAllItemStacks(world, loc, drops);
                    } else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                        e.setDropItems(false);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, shovelPredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                        items = dropAllItemStacks(world, loc, drops);
                    } else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                        e.setDropItems(false);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                        items = dropAllItemStacks(world, loc, drops);
                    } else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                        e.setDropItems(false);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                        items = dropAllItemStacks(world, loc, drops);
                    }
                }
                // No custom drop
                // Shovels
                else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                    e.setDropItems(false);
                    if (itemID.contains("aethershovel")) {
                        items = dropAllItemStacks(world, loc, p.getInventory().addItem(breakInFacing(b, radius, depth, p, shovelPredicates).toArray(new ItemStack[0])).values());
                    }
                    else if (itemID.contains("ancienttshovel"))
                        items = dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, ancientShovelPredicates));
                    else if (itemID.contains("creakingshovel")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, ancientShovelPredicates);
                        // Auto Sell
                        if (("Enabled").equals(container.get(keyAutoSell, PersistentDataType.STRING))) {
                            BigDecimal totalPrice = BigDecimal.valueOf(container.get(keyMoney, PersistentDataType.DOUBLE));

                            Iterator<ItemStack> iterator = drops.iterator();
                            while (iterator.hasNext()) {
                                ItemStack drop = iterator.next();
                                Double price = container.get(new NamespacedKey("score", "score-" + drop.getType().toString().toLowerCase()), PersistentDataType.DOUBLE);
                                if (price != null) {
                                    totalPrice = totalPrice.add(BigDecimal.valueOf(price));
                                    iterator.remove();
                                }
                            }
                            String formattedTotal = totalPrice.setScale(4, RoundingMode.HALF_UP).toPlainString();
                            runConsoleCommands("ei console-modification set variable " + p.getName() + " -1 " +
                              "money " + formattedTotal);
                        }
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else
                        items = dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, shovelPredicates));
                }
                // Pickaxes
                else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                    e.setDropItems(false);
                    if (itemID.contains("nightmarepick")) {
                        Collection<ItemStack> drops = new ArrayList<>();
                        if (b.getType().toString().contains("_ORE"))
                            veinMineOres25ChanceDouble(b, drops, b.getType(), p, item);
                        PlayerInventory inv = p.getInventory();
                        drops.removeIf(drop -> inv.addItem(drop).isEmpty());
                        drops.addAll(breakInFacing(b, radius, depth, p, pickaxePredicates));
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("abysspickm")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        Collection<ItemStack> quartz = new ArrayList<>();
                        if (("Enabled").equals(container.get(keyGraniteMode, PersistentDataType.STRING))) {
                            drops.removeIf(drop -> {
                                if (drop.getType().equals(Material.GRANITE)) {
                                    quartz.add(new ItemStack(Material.QUARTZ_BLOCK, drop.getAmount() * 3));
                                    return true;
                                } return false;
                            });
                        }
                        if (("Enabled").equals(container.get(keyDioriteMode, PersistentDataType.STRING))) {
                            drops.removeIf(drop -> {
                                if (drop.getType().equals(Material.DIORITE)) {
                                    quartz.add(new ItemStack(Material.QUARTZ_BLOCK, drop.getAmount() * 2));
                                    return true;
                                } return false;
                            });
                        }
                        drops.addAll(quartz);
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("abysspick")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        Collection<ItemStack> quartz = new ArrayList<>();
                        if (("Enabled").equals(container.get(keyGraniteMode, PersistentDataType.STRING))) {
                            drops.removeIf(drop -> {
                                if (drop.getType().equals(Material.GRANITE)) {
                                    quartz.add(new ItemStack(Material.QUARTZ_BLOCK, drop.getAmount() * 2));
                                    return true;
                                } return false;
                            });
                        }
                        if (("Enabled").equals(container.get(keyDioriteMode, PersistentDataType.STRING))) {
                            drops.removeIf(drop -> {
                                if (drop.getType().equals(Material.DIORITE)) {
                                    quartz.add(drop.withType(Material.QUARTZ_BLOCK));
                                    return true;
                                } return false;
                            });
                        }
                        drops.addAll(quartz);
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("sunpick")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        drops.removeIf(drop -> {
                            Material dropMat = drop.getType();
                            if (smeltedOres.containsKey(dropMat)) {
                                Material smeltedMat = smeltedOres.get(dropMat);
                                runConsoleCommands("ei console-modification modification variable " + p.getName() + " -1 " +
                                  " " + smeltedMat.toString() + " " + 1);
                                return true;
                            }
                            return false;
                        });
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("creakingpick")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        if (("Enabled").equals(container.get(keyVoid, PersistentDataType.STRING))) {
                            drops.removeIf(drop ->
                                !oreDrops.contains(drop.getType()) && !oreBlocks.contains(drop.getType()));
                        }
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("nightspirepick")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        Collection<ItemStack> ores = new ArrayList<>();
                        boolean smeltingEnabled = container.getOrDefault(keySmelting, PersistentDataType.STRING, "Disabled").equals("Enabled");

                        drops.removeIf(drop -> {
                            if (oreDrops.contains(drop.getType())) {
                                if (ThreadLocalRandom.current().nextInt(4) == 0) {
                                    drop.setAmount(drop.getAmount() * 2);
                                    e.setExpToDrop(e.getExpToDrop() + 2);
                                }
                                if (smeltingEnabled) {
                                    ores.add(new ItemStack(smeltedOres.get(drop.getType()), drop.getAmount()));
                                    return true;
                                }
                            }
                            return false;
                        });
                        drops.addAll(ores);
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("soulpick"))
                        FUtils.breakInFacingDoubleOres(b, radius, depth, p, pickaxePredicates, e.getExpToDrop());
                    else if (itemID.contains("ancienttpick"))
                        items = dropAllItemStacks(world, loc, BlockUtils.breakInFacing(b, radius, depth, p, ancientPickPredicates));
                    else if (itemID.contains("catsagepick")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        List<ItemStack> ores = new ArrayList<>();
                        List<ItemStack> glazedDrops = new ArrayList<>();
                        boolean terracotta = container.getOrDefault(keyTerracotta, PersistentDataType.STRING, "Disabled").equals("Enabled");
                        boolean smeltingEnabled = container.getOrDefault(keySmelting, PersistentDataType.STRING, "Disabled").equals("Enabled");

                        // Partition drops into smelted ores, glazed terracotta, and remaining drops
                        drops.removeIf(drop -> {
                            Material dropType = drop.getType();

                            // Collect smelted ores to add to inventory
                            if (smeltedOres.containsKey(dropType)) {
                                if (smeltingEnabled) ores.add(new ItemStack(smeltedOres.get(dropType), drop.getAmount()));
                                else ores.add(drop);
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
                        items = dropAllItemStacks(world, loc, drops);
                    } else {
                        items = dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, pickaxePredicates));
                    }
                }
                // Axes
                else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                    e.setDropItems(false);
                    if (itemID.contains("ancienttaxe"))
                        items = dropAllItemStacks(world, loc, BlockUtils.breakInFacing(b, radius, depth, p, ancientAxePredicates));
                    else if (itemID.contains("sunaxe")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates);
                        drops.removeIf(drop -> {
                            Material dropMat = drop.getType();
                            if (logMap.containsKey(dropMat)) {
                                runConsoleCommands("ei console-modification modification variable " + p.getName() + " -1 " +
                                  " " + logMap.get(dropMat) + " 1");
                                return true;
                            }
                            return false;
                        });
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else if (itemID.contains("demonslimeaxe")) {
                        Collection<ItemStack> drops = breakInFacing(b, radius, depth, p, axePredicates);
                        if ("true".equals(container.get(keyCoal, PersistentDataType.STRING))) {
                            Collection<ItemStack> coal = new ArrayList<>();
                            drops.removeIf(drop -> {
                                if (drop.getType().toString().contains("_LOG")) {
                                    coal.add(new ItemStack(Material.COAL, drop.getAmount()));
                                    return true;
                                }
                                return false;
                            });
                            drops.addAll(coal);
                        }
                        items = dropAllItemStacks(world, loc, drops);
                    }
                    else
                        items = dropAllItemStacks(world, loc, breakInFacing(b, radius, depth, p, axePredicates));
                }
            }

            // BreakInRadius
            else {
                if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                    e.setDropItems(false);
                    items = dropAllItemStacks(world, loc, breakInRadius(b, radius, p, shovelPredicates));
                }
                else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                    e.setDropItems(false);
                    items = dropAllItemStacks(world, loc, breakInRadius(b, radius, p, pickaxePredicates));
                }
                else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                    e.setDropItems(false);
                    items = dropAllItemStacks(world, loc, breakInRadius(b, radius, p, axePredicates));
                }
            }
        }

        // Call block drop event on custom dropped items
        if (items == null) return;

        BlockDropItemEvent event = new BlockDropItemEvent(b, b.getState(), p, items);
        Bukkit.getServer().getPluginManager().callEvent(event);

        // Remove drops if event is cancelled
        if (event.isCancelled()) for (Item itemDrop : items) itemDrop.remove();


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
                    if (ThreadLocalRandom.current().nextInt(4) == 0) drops.addAll(b.getDrops(item));
                    b.setType(Material.AIR);
                    this.veinMineOres25ChanceDouble(b, drops, material, player, item);
                }
            }
        }
    }

}
