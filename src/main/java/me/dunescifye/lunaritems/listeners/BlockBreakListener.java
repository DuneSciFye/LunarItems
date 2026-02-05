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
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
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
import org.bukkit.inventory.meta.BlockStateMeta;
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
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
            BlockState blockState = e.getBlockState();
            if (itemID != null) {
                if (blockState.getBlockData() instanceof Ageable ageable && ageable.getAge() == ageable.getMaximumAge()) {
                    // Auto Pickup crops
                    if (itemID.contains("soulhoe") || itemID.contains("demonslimehoe"))
                        items.removeIf(drop -> inv.addItem(drop.getItemStack()).isEmpty());
                    else if (itemID.contains("halloweenhoe")) {
                        for (Item drop : items) {
                            ItemStack itemStack = drop.getItemStack();
                            NamespacedKey key =
                              new NamespacedKey("score", "score-" + itemStack.getType().toString().toLowerCase());
                            Double amount = pdc.get(key, PersistentDataType.DOUBLE);
                            if (amount != null) {
                                if (Objects.equals(pdc.get(key2x, PersistentDataType.STRING), "Enabled"))
                                    pdc.set(key, PersistentDataType.DOUBLE,
                                      amount + itemStack.getAmount() + itemStack.getAmount());
                                else
                                    pdc.set(key, PersistentDataType.DOUBLE, amount + itemStack.getAmount());
                                drop.remove();
                                item.setItemMeta(meta);
                            }
                        }
                    }
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

        for (ItemStack item : p.getInventory().getContents()) {
            if (item == null || !item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String itemID = pdc.get(keyEIID, PersistentDataType.STRING);
            if (itemID != null && itemID.contains("creakingscribe")) {
                Double blocksBroken = pdc.get(keyBlocks_Broken, PersistentDataType.DOUBLE);
                if (blocksBroken == null) continue;
                pdc.set(keyBlocks_Broken, PersistentDataType.DOUBLE, (blocksBroken + 1));
                meta.lore(updateLore(item, "心" + blocksBroken.intValue(), "心" + (blocksBroken.intValue() + 1)));
                item.setItemMeta(meta);
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
                if (container.has(autoSellKey) && container.has(keyMoney, PersistentDataType.DOUBLE)) {
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
                if (container.has(autoReplantKey)) {
                    replant(b);
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
                  itemID.contains("autumnhoe") ||
                  itemID.contains("discoveryhoe2") ||
                  itemID.contains("halloweenhoe")) {
                    replant(b);
                }
                else if (itemID.contains("amberlighthoe")) {
                    String offhandItemID = p.getInventory().getItemInOffHand().getPersistentDataContainer().get(keyEIID,
                      PersistentDataType.STRING);
                    if (offhandItemID != null && offhandItemID.contains("amberlightfertilizer")) {
                        replant(b, ThreadLocalRandom.current().nextInt(0, 3));
                    }
                    else replant(b);
                }
                else if (itemID.contains("seraphimhoe")) {
                    replant(b);
                    p.setFoodLevel(p.getFoodLevel() + 1);
                    Double blocksBroken = container.get(keyBlocksBroken, PersistentDataType.DOUBLE);
                    Double points = container.get(keyPoints, PersistentDataType.DOUBLE);
                    if (blocksBroken != null && points != null) {
                        blocksBroken++;
                        if (blocksBroken >= 1000) {
                            blocksBroken = 0.0;
                            points++;
                            container.set(keyPoints, PersistentDataType.DOUBLE, points);
                        }
                        container.set(keyBlocksBroken, PersistentDataType.DOUBLE, blocksBroken);
                        item.setItemMeta(meta);
                    }
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
        Collection<ItemStack> drops = new ArrayList<>();

        if (container.has(LunarItems.keyRadius, PersistentDataType.DOUBLE)) {
            int radius = (int) (double) container.getOrDefault(keyRadius, PersistentDataType.DOUBLE, 0.0);
            // BreakInFacing
            if (container.has(LunarItems.keyDepth, PersistentDataType.DOUBLE)) {
                int depth = (int) (double) container.getOrDefault(keyDepth, PersistentDataType.DOUBLE, 0.0);
                // Custom drop
                String customDrop = container.get(LunarItems.keyDrop, PersistentDataType.STRING);

                // Increment block break statistic
                p.incrementStatistic(Statistic.MINE_BLOCK, b.getType());

                if (itemID.contains("jollyaxe")) {
                    drops = breakInFacing(b, radius, depth, p, axePredicates);
                    // Custom Drops
                    if (customDrop != null && !customDrop.isEmpty()) {
                        Material log = Material.getMaterial(customDrop + "_LOG");
                        Material strippedLog = Material.getMaterial("STRIPPED_" + customDrop + "_LOG");
                        Material wood = Material.getMaterial(customDrop + "_WOOD");
                        Material strippedWood = Material.getMaterial("STRIPPED_" + customDrop + "_WOOD");

                        if (log != null && strippedLog != null && wood != null && strippedWood != null) {
                            drops = drops.stream().map(drop -> {
                                String dropString = drop.getType().toString();
                                if (dropString.contains("LOG"))
                                    return new ItemStack(dropString.contains("STRIPPED_") ? strippedLog : log,
                                      drop.getAmount());
                                else if (dropString.contains("WOOD"))
                                    return new ItemStack(dropString.contains("STRIPPED_") ? strippedWood : wood,
                                      drop.getAmount());
                              return drop;
                            }).collect(Collectors.toList());
                        }
                    }
                    // Add to Inventory
                    Inventory inv = p.getInventory();
                    drops.removeIf(drop -> {
                        String dropString = drop.getType().toString();
                        if (dropString.contains("LOG") || dropString.contains("WOOD"))
                            return inv.addItem(drop).isEmpty();
                        return false;
                    });
                } else if (itemID.contains("amberlightpick")) {
                    drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                    // Put items in shulker
                    if (Objects.equals(container.get(keyShulker, PersistentDataType.STRING), "Enabled")) {
                        ItemStack offhandItem = p.getInventory().getItemInOffHand();
                        if (offhandItem.getItemMeta() instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                            HashMap<Integer, ItemStack> leftovers =
                              shulkerBox.getInventory().addItem(drops.toArray(new ItemStack[0]));
                            drops.clear();
                            drops.addAll(leftovers.values());
                            blockStateMeta.setBlockState(shulkerBox);
                            offhandItem.setItemMeta(blockStateMeta);
                        }
                    }
                    // Custom Drops
                    if (customDrop != null && !customDrop.isEmpty()) {
                        drops = drops.stream()
                          .map(drop -> {
                              Material current = drop.getType();
                              Material mat = Material.getMaterial(customDrop + current);
                              if (mat == null) mat = Material.getMaterial(current + customDrop);
                              if (mat != null) {
                                  return new ItemStack(mat, drop.getAmount());
                              }
                              return drop;
                          })
                          .toList();
                    }
                } else if (itemID.contains("amberlightaxem")) {
                    drops = breakInFacing(b, radius, depth, p, axePredicates);
                    // Custom Drops
                    if (customDrop != null && !customDrop.isEmpty()) {
                        drops = drops.stream()
                          .map(drop -> {
                              Material mat = null;
                              String current = drop.getType().toString();
                              if (customDrop.equals("STRIPPED")) mat = Material.getMaterial("STRIPPED_" + current);
                              else if (current.startsWith("OAK_")) mat = Material.getMaterial("OAK_" + customDrop);
                              else if (current.startsWith("BIRCH_")) mat = Material.getMaterial("BIRCH_" + customDrop);
                              else if (current.startsWith("DARK_OAK_")) mat = Material.getMaterial("DARK_OAK_" + customDrop);
                              else if (current.startsWith("JUNGLE_")) mat = Material.getMaterial("JUNGLE_" + customDrop);
                              else if (current.startsWith("SPRUCE_")) mat = Material.getMaterial("SPRUCE_" + customDrop);
                              else if (current.startsWith("ACACIA_")) mat = Material.getMaterial("ACACIA_" + customDrop);
                              else if (current.startsWith("MANGROVE_")) mat = Material.getMaterial("MANGROVE_" + customDrop);
                              else if (current.startsWith("CHERRY_")) mat = Material.getMaterial("CHERRY_" + customDrop);
                              else if (current.startsWith("PALE_OAK_")) mat = Material.getMaterial("PALE_OAK_" + customDrop);
                              if (mat != null) {
                                  return new ItemStack(mat, drop.getAmount() * 2);
                              }
                              return drop;
                          })
                          .toList();
                    }
                } else if (itemID.contains("amberlightaxe")) {
                    drops = breakInFacing(b, radius, depth, p, axePredicates);
                    // Custom Drops
                    if (customDrop != null && !customDrop.isEmpty()) {
                        drops = drops.stream()
                          .map(drop -> {
                              Material mat = null;
                              String current = drop.getType().toString();
                              if (customDrop.equals("STRIPPED")) mat = Material.getMaterial("STRIPPED_" + current);
                              else if (current.startsWith("OAK_")) mat = Material.getMaterial("OAK_" + customDrop);
                              else if (current.startsWith("BIRCH_")) mat = Material.getMaterial("BIRCH_" + customDrop);
                              else if (current.startsWith("DARK_OAK_")) mat = Material.getMaterial("DARK_OAK_" + customDrop);
                              else if (current.startsWith("JUNGLE_")) mat = Material.getMaterial("JUNGLE_" + customDrop);
                              else if (current.startsWith("SPRUCE_")) mat = Material.getMaterial("SPRUCE_" + customDrop);
                              else if (current.startsWith("ACACIA_")) mat = Material.getMaterial("ACACIA_" + customDrop);
                              else if (current.startsWith("MANGROVE_")) mat = Material.getMaterial("MANGROVE_" + customDrop);
                              else if (current.startsWith("CHERRY_")) mat = Material.getMaterial("CHERRY_" + customDrop);
                              else if (current.startsWith("PALE_OAK_")) mat = Material.getMaterial("PALE_OAK_" + customDrop);
                              if (mat != null) {
                                  return new ItemStack(mat, drop.getAmount());
                              }
                              return drop;
                          })
                          .toList();
                    }
                }
                else if (itemID.contains("aetheraxe") && testBlock(b, axePredicates)) {
                    e.setCancelled(true);
                    // Will do 3x3x3 if axe is being thrown, otherwise use item's radius and depth
                    drops = p.hasMetadata("ignoreBlockBreak") ? breakInFacing(b, 0, 1, p, axePredicates) : breakInFacing(b, radius, depth, p, axePredicates);
                    if (b.getBlockData() instanceof Door door && door.getHalf() == Bisected.Half.TOP) drops.add(new ItemStack(door.getMaterial()));
                    Material sapling = Material.getMaterial(customDrop);
                    if (sapling != null) {
                        drops = drops.stream()
                            .map(drop -> drop.getType().toString().contains("_SAPLING") ? new ItemStack(sapling, drop.getAmount()) : drop)
                            .collect(Collectors.toList());
                    }
                    Inventory inv = p.getInventory();
                    drops.removeIf(drop -> inv.addItem(drop).isEmpty());
                }
                else if (customDrop != null && !customDrop.isEmpty()) {
                    if (itemID.contains("catsageaxe") && testBlock(b, axePredicates)) {
                        e.setCancelled(true);
                        Material mat = Material.getMaterial(customDrop);
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
                        if (mat != null)
                            drops = drops.stream()
                              .map(drop -> Tag.LOGS.isTagged(drop.getType()) ? new ItemStack(mat, drop.getAmount()) : drop)
                              .toList();
                    } else if (itemID.contains("aquaticaxe") && testBlock(b, axePredicates)) {
                        e.setCancelled(true);
                        //Change log drops
                        String material = b.getType().toString().toUpperCase();
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
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
                    } else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                        e.setCancelled(true);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        drops = breakInFacing(b, radius, depth, p, shovelPredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                    } else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                        e.setCancelled(true);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                    } else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                        e.setCancelled(true);
                        Material mat = Material.getMaterial(customDrop.toUpperCase());
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        if (mat != null) drops = drops.stream()
                          .map(drop -> new ItemStack(mat, drop.getAmount()))
                          .toList();
                    }
                }
                // No custom drop
                // Shovels
                else if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                     if (itemID.contains("amberlightshovel")) {
                        drops = breakInFacing(b, radius, depth, p, shovelPredicates);
                        // Put items in shulker
                        if (Objects.equals(container.get(keyShulker, PersistentDataType.STRING), "Enabled")) {
                            ItemStack offhandItem = p.getInventory().getItemInOffHand();
                            if (offhandItem.getItemMeta() instanceof BlockStateMeta blockStateMeta && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                                HashMap<Integer, ItemStack> leftovers =
                                  shulkerBox.getInventory().addItem(drops.toArray(new ItemStack[0]));
                                drops.clear();
                                drops.addAll(leftovers.values());
                                blockStateMeta.setBlockState(shulkerBox);
                                offhandItem.setItemMeta(blockStateMeta);
                            }
                        }
                    }
                    else if (itemID.contains("aethershovel")) {
                        drops =  breakInFacing(b, radius, depth, p, axePredicates);
                          p.getInventory().addItem(breakInFacing(b, radius, depth, p, shovelPredicates).toArray(new ItemStack[0])).values();
                    }
                    else if (itemID.contains("seraphimshovel")) {
                        drops = breakInFacing(b, radius, depth, p, shovelPredicates);

                        String storedBlock = container.get(keyStoredBlock, PersistentDataType.STRING);
                        Double amount = container.get(keyAmount, PersistentDataType.DOUBLE);
                        if (storedBlock != null && amount != null && !drops.isEmpty()) {
                            Material mat = Material.getMaterial(storedBlock);
                            if (mat != null) {
                                for (Iterator<ItemStack> it = drops.iterator(); it.hasNext(); ) {
                                    ItemStack drop = it.next();
                                    if (drop.getType() == (mat)) {
                                        amount += drop.getAmount();
                                        it.remove();
                                    }
                                }
                            }
                            container.set(keyAmount, PersistentDataType.DOUBLE, amount);
                            item.setItemMeta(meta);
                        }

                        // Replace sand with sandstone and clay with bricks
                         drops = drops.stream().map(
                             drop -> switch (drop.getType()) {
                                 case RED_SAND -> drop.withType(Material.RED_SANDSTONE);
                                 case CLAY_BALL -> drop.withType(Material.BRICK);
                                 case CLAY -> drop.withType(Material.BRICKS);
                                 case SAND -> drop.withType(Material.SANDSTONE);
                                 default -> drop;
                             })
                           .collect(Collectors.toList());

                    }
                    else if (itemID.contains("ancienttshovel"))
                        drops = breakInFacing(b, radius, depth, p, ancientShovelPredicates);
                    else if (itemID.contains("creakingshovel")) {
                        drops = breakInFacing(b, radius, depth, p, ancientShovelPredicates);
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
                    }
                    else
                        drops = breakInFacing(b, radius, depth, p, shovelPredicates);
                }
                // Pickaxes
                else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                    e.setCancelled(true);
                    if (itemID.contains("nightmarepick")) {
                        drops = new ArrayList<>();
                        if (b.getType().toString().contains("_ORE"))
                            veinMineOres25ChanceDouble(b, drops, b.getType(), p, item);
                        PlayerInventory inv = p.getInventory();
                        drops.removeIf(drop -> inv.addItem(drop).isEmpty());
                        drops.addAll(breakInFacing(b, radius, depth, p, pickaxePredicates));
                    }
                    else if (itemID.contains("seraphimpick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);

                        String storedBlock = container.get(keyStoredBlock, PersistentDataType.STRING);
                        Double amount = container.get(keyAmount, PersistentDataType.DOUBLE);
                        if (storedBlock != null && amount != null && !drops.isEmpty()) {
                            Material mat = Material.getMaterial(storedBlock);
                            if (mat != null) {
                                for (Iterator<ItemStack> it = drops.iterator(); it.hasNext(); ) {
                                    ItemStack drop = it.next();
                                    if (drop.getType() == (mat)) {
                                        amount += drop.getAmount();
                                        it.remove();
                                    }
                                }
                            }
                            container.set(keyAmount, PersistentDataType.DOUBLE, amount);
                            item.setItemMeta(meta);
                        }
                    }
                    else if (itemID.contains("abysspickm")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
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
                    }
                    else if (itemID.contains("abysspick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
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
                    }
                    else if (itemID.contains("jollypick")) {
                        if (oreBlocks.contains(b.getType())) breakInVein(b, drops, b.getType(), p, 0);
                        drops.addAll(breakInFacing(b, radius, depth, p, pickaxePredicates));
                    }
                    else if (itemID.contains("sunpick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
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
                    }
                    else if (itemID.contains("creakingpick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        if (("Enabled").equals(container.get(keyVoid, PersistentDataType.STRING))) {
                            drops.removeIf(drop ->
                              !oreDrops.contains(drop.getType()) && !oreBlocks.contains(drop.getType()));
                        }
                    }
                    else if (itemID.contains("nightspirepick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                        Collection<ItemStack> ores = new ArrayList<>();
                        boolean smeltingEnabled = container.getOrDefault(keySmelting, PersistentDataType.STRING, "Disabled").equals("Enabled");

                        drops.removeIf(drop -> {
                            if (smeltedOres.containsKey(drop.getType())) {
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
                    }
                    else if (itemID.contains("soulpick"))
                        FUtils.breakInFacingDoubleOres(b, radius, depth, p, pickaxePredicates, e.getExpToDrop());
                    else if (itemID.contains("ancienttpick"))
                        drops = BlockUtils.breakInFacing(b, radius, depth, p, ancientPickPredicates);
                    else if (itemID.contains("catsagepick")) {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
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
                    } else {
                        drops = breakInFacing(b, radius, depth, p, pickaxePredicates);
                    }
                }
                // Axes
                else if (item.getType().equals(Material.NETHERITE_AXE)) {
                    // Early return if block doesn't match axe predicates - prevents item deletion bug with mushrooms
                    if (!testBlock(b, axePredicates)) return;
                    e.setCancelled(true);
                    if (itemID.contains("ancienttaxe"))
                        drops = BlockUtils.breakInFacing(b, radius, depth, p, ancientAxePredicates);
                    else if (itemID.contains("sunaxe")) {
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
                        drops.removeIf(drop -> {
                            Material dropMat = drop.getType();
                            if (logMap.containsKey(dropMat)) {
                                runConsoleCommands("ei console-modification modification variable " + p.getName() + " -1 " +
                                  " " + logMap.get(dropMat) + " 1");
                                return true;
                            }
                            return false;
                        });
                    }
                    else if (itemID.contains("demonslimeaxe")) {
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
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
                    }
                    else
                        drops = breakInFacing(b, radius, depth, p, axePredicates);
                }
            }

            // BreakInRadius
            else {
                if (item.getType().equals(Material.NETHERITE_SHOVEL) && testBlock(b, shovelPredicates)) {
                    e.setCancelled(true);
                    drops = breakInRadius(b, radius, p, shovelPredicates);
                }
                else if (item.getType().equals(Material.NETHERITE_PICKAXE) && testBlock(b, pickaxePredicates)) {
                    e.setCancelled(true);
                    drops = breakInRadius(b, radius, p, pickaxePredicates);
                }
                else if (item.getType().equals(Material.NETHERITE_AXE) && testBlock(b, axePredicates)) {
                    e.setCancelled(true);
                    drops = breakInRadius(b, radius, p, axePredicates);
                }
            }
        }

        // Do functions based on keys
        // Auto smelt everything
        if (container.has(autoSmeltKey)) {
            drops = drops.stream().map(
              drop -> new ItemStack(smeltMaterial(drop.getType()), drop.getAmount())
            ).collect(Collectors.toList());
        }
        // Remove a specific drop material
        String removeDrop = container.get(removeDropKey, PersistentDataType.STRING);
        if (removeDrop != null) {
            Material mat = Material.getMaterial(removeDrop.toUpperCase());
            if (mat != null) {
                drops.removeIf(drop -> drop.getType().equals(mat));
            }
        }
        // Replace all drops with this drop
        String replaceDrop = container.get(replaceDropKey, PersistentDataType.STRING);
        if (replaceDrop != null) {
            Material mat = Material.getMaterial(replaceDrop.toUpperCase());
            if (mat != null) {
                drops = drops.stream().map(
                  drop -> drop.withType(mat))
                  .collect(Collectors.toList());
            }
        }
        // Ore Storage
        String oreStorage = container.get(oreStorageKey, PersistentDataType.STRING);
        if ("Enabled".equals(oreStorage)) {
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
        }
        // Auto Ore Pickup
        String autoPickupOres = container.get(autoPickupOresKey, PersistentDataType.STRING);
        if ("Enabled".equals(autoPickupOres)) {
            Inventory inv = p.getInventory();
            drops.removeIf(drop -> inv.addItem(drop).isEmpty());
        }

        // Auto Strip Wood
        String autoStripWood = container.get(keyAutoStripWood, PersistentDataType.STRING);
        if ("Enabled".equals(autoStripWood)) {
            drops = drops.stream().map(
                drop -> stripWood.containsKey(drop.getType()) ? drop.withType(stripWood.get(drop.getType())) : drop)
              .collect(Collectors.toList());
        }

        // Convert one material to another
        String materialFrom = container.get(keyMaterialFrom, PersistentDataType.STRING);
        String materialTo = container.get(keyMaterialTo, PersistentDataType.STRING);
        if (materialFrom != null && materialTo != null) {
            Material matFrom = Material.getMaterial(materialFrom.toUpperCase());
            Material matTo = Material.getMaterial(materialTo.toUpperCase());
            if (matFrom != null && matTo != null) {
                drops = drops.stream().map(
                    drop -> matFrom.equals(drop.getType()) ? drop.withType(matTo) : drop)
                  .collect(Collectors.toList());
            }
        }

        // Duplicate Ore Drops
        Double chance = container.get(keyDuplicateOres, PersistentDataType.DOUBLE);
        if (chance != null && chance != 0) {
            drops = drops.stream().map(
                drop -> {
                    if (rawOres.containsValue(drop.getType()) && ThreadLocalRandom.current().nextDouble() <= chance) {
                        return drop.asQuantity(drop.getAmount() * 2);
                    }
                    return drop;
                })
              .collect(Collectors.toList());
        }

        items = dropAllItemStacks(world, loc, drops);

        // Call block drop event on custom dropped items
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
