package me.dunescifye.lunaritems.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Slab;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;
import static me.dunescifye.lunaritems.utils.FUtils.isInClaimOrWilderness;
import static me.dunescifye.lunaritems.utils.Utils.getBlocksInRadius;

public class BlockUtils {

    public static List<List<Predicate<Block>>> ores = List.of(
        List.of(
            block -> block.getType().equals(Material.GOLD_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_GOLD_ORE),
            block -> block.getType().equals(Material.IRON_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_IRON_ORE),
            block -> block.getType().equals(Material.COAL_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_COAL_ORE),
            block -> block.getType().equals(Material.COPPER_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_COPPER_ORE),
            block -> block.getType().equals(Material.REDSTONE_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_REDSTONE_ORE),
            block -> block.getType().equals(Material.LAPIS_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_LAPIS_ORE),
            block -> block.getType().equals(Material.DIAMOND_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_DIAMOND_ORE),
            block -> block.getType().equals(Material.EMERALD_ORE),
            block -> block.getType().equals(Material.DEEPSLATE_EMERALD_ORE),
            block -> block.getType().equals(Material.NETHER_GOLD_ORE),
            block -> block.getType().equals(Material.NETHER_QUARTZ_ORE),
            block -> block.getType().equals(Material.ANCIENT_DEBRIS)
        ),
        List.of()
    );

    public static List<Material> oreDrops = List.of(
      Material.RAW_GOLD,
      Material.RAW_IRON,
      Material.COAL,
      Material.RAW_COPPER,
      Material.REDSTONE,
      Material.LAPIS_LAZULI,
      Material.DIAMOND,
      Material.EMERALD,
      Material.NETHERITE_SCRAP,
      Material.GOLD_NUGGET,
      Material.QUARTZ
    );

    public static List<Material> oreBlocks = List.of(
      Material.GOLD_ORE,
      Material.DEEPSLATE_GOLD_ORE,
      Material.IRON_ORE,
      Material.DEEPSLATE_IRON_ORE,
      Material.COAL_ORE,
      Material.DEEPSLATE_COAL_ORE,
      Material.COPPER_ORE,
      Material.DEEPSLATE_COPPER_ORE,
      Material.REDSTONE_ORE,
      Material.DEEPSLATE_REDSTONE_ORE,
      Material.LAPIS_ORE,
      Material.DEEPSLATE_LAPIS_ORE,
      Material.DIAMOND_ORE,
      Material.DEEPSLATE_DIAMOND_ORE,
      Material.EMERALD_ORE,
      Material.DEEPSLATE_EMERALD_ORE,
      Material.ANCIENT_DEBRIS,
      Material.GOLD_BLOCK,
      Material.RAW_GOLD_BLOCK,
      Material.IRON_BLOCK,
      Material.RAW_IRON_BLOCK,
      Material.COAL_BLOCK,
      Material.COPPER_BLOCK,
      Material.RAW_COPPER_BLOCK,
      Material.REDSTONE_BLOCK,
      Material.LAPIS_BLOCK,
      Material.DIAMOND_BLOCK,
      Material.EMERALD_BLOCK,
      Material.NETHERITE_BLOCK,
      Material.NETHER_GOLD_ORE,
      Material.NETHER_QUARTZ_ORE
    );

    public static List<List<Predicate<Block>>> pickaxePredicates = List.of(
      List.of( // Whitelist
        block -> Tag.MINEABLE_PICKAXE.isTagged(block.getType())
      ),
      List.of( // Blacklist
        block -> block.getType().equals(Material.SPAWNER),
        block -> block.getType().equals(Material.GILDED_BLACKSTONE),
        block -> block instanceof Container,
        block -> block.getType().equals(Material.DROPPER),
        block -> block.getType().equals(Material.DISPENSER),
        block -> block.getType().equals(Material.HOPPER),
        block -> block.getType().equals(Material.FURNACE),
        block -> block.getType().equals(Material.BLAST_FURNACE),
        block -> block.getType().equals(Material.SMOKER),
        block -> Tag.SHULKER_BOXES.isTagged(block.getType())
      )
    );

    public static List<List<Predicate<Block>>> shovelPredicates = List.of(
        List.of( // Whitelist
            block -> Tag.MINEABLE_SHOVEL.isTagged(block.getType())
        ),
        List.of( // Blacklist
        )
    );

    public static List<List<Predicate<Block>>> axePredicates = List.of(
        List.of( // Whitelist
            block -> Tag.MINEABLE_AXE.isTagged(block.getType()),
          block -> Tag.LEAVES.isTagged(block.getType())
        ),
        List.of( // Blacklist
            block -> block instanceof Container,
            block -> block.getType().equals(Material.BARREL),
            block -> block.getType().equals(Material.CHEST),
            block -> block.getType().equals(Material.TRAPPED_CHEST),
            block -> Tag.ALL_SIGNS.isTagged(block.getType()),
          block -> block.getType().equals(Material.MUSHROOM_STEM),
          block -> block.getType().equals(Material.BROWN_MUSHROOM_BLOCK),
          block -> block.getType().equals(Material.RED_MUSHROOM_BLOCK)
        )
    );

    public static List<List<Predicate<Block>>> ancientPickPredicates = List.of(
        List.of( // Whitelist
            block -> Tag.MINEABLE_PICKAXE.isTagged(block.getType()),
            block -> block.getType().equals(Material.WATER),
            block -> block.getType().equals(Material.LAVA)
        ),
        List.of( // Blacklist
            block -> block.getType().equals(Material.SPAWNER),
            block -> block.getType().equals(Material.GILDED_BLACKSTONE),
            block -> block.getType().equals(Material.DROPPER),
            block -> block.getType().equals(Material.DISPENSER),
            block -> block.getType().equals(Material.HOPPER),
            block -> block.getType().equals(Material.FURNACE),
            block -> block.getType().equals(Material.BLAST_FURNACE),
            block -> block.getType().equals(Material.SMOKER),
            block -> Tag.SHULKER_BOXES.isTagged(block.getType())
        )
    );
    public static List<List<Predicate<Block>>> ancientShovelPredicates = List.of(
      List.of( // Whitelist
        block -> Tag.MINEABLE_SHOVEL.isTagged(block.getType()),
        block -> block.getType().equals(Material.WATER),
        block -> block.getType().equals(Material.LAVA)
      ),
      List.of( // Blacklist
      )
    );


    public static List<List<Predicate<Block>>> ancientAxePredicates = List.of(
        List.of( // Whitelist
            block -> Tag.MINEABLE_AXE.isTagged(block.getType()),
            block -> Tag.LEAVES.isTagged(block.getType()),
            block -> block.getType().equals(Material.WATER),
            block -> block.getType().equals(Material.LAVA),
          block -> block.getType().equals(Material.MUSHROOM_STEM),
          block -> block.getType().equals(Material.BROWN_MUSHROOM_BLOCK),
          block -> block.getType().equals(Material.RED_MUSHROOM_BLOCK)
        ),
        List.of( // Blacklist
            block -> block.getType().equals(Material.BARREL),
            block -> block.getType().equals(Material.CHEST),
            block -> block.getType().equals(Material.TRAPPED_CHEST),
            block -> Tag.ALL_SIGNS.isTagged(block.getType())
        )
    );

    //Breaks blocks in direction player is facing. Updates block b to air. Returns drops.
    public static Collection<ItemStack> breakInFacing(Block center, int radius, int depth, Player p, List<List<Predicate<Block>>> predicates) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();
        Collection<ItemStack> drops = new ArrayList<>();

        for (Block b : Utils.getBlocksInFacing(center, radius, depth, p)) {

            // Skip if block fails custom predicate check or isn't in a valid area
            if (!Utils.testBlock(b, predicates) || !FUtils.isInClaimOrWilderness(p, b.getLocation())) {
                continue;
            }

            // Skip if it's a custom block
            PersistentDataContainer container = new CustomBlockData(b, LunarItems.getPlugin());
            if (container.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                continue;
            }

            // Handle center block separately for plugin compatibility
            if (b.equals(center)) {
                BlockData data = b.getBlockData();
                if (data instanceof Door door && door.getHalf() == Bisected.Half.TOP) {
                    Block halfBlock = b.getRelative(BlockFace.DOWN);

                    drops.add(new ItemStack(halfBlock.getType()));
                    halfBlock.setType(Material.AIR);
                }
                else if (p.isSneaking() && data instanceof Slab slab && slab.getType() == Slab.Type.DOUBLE) {
                    continue; // Skip breaking double slab while sneaking to support Purpur slab breaking
                }

            }
            drops.addAll(b.getDrops(heldItem));
            b.setType(Material.AIR, true);
        }

        return drops;
    }


    // Breaks blocks in radius. Updates block b to air.
    public static Collection<ItemStack> breakInRadius(Block center, int radius, Player p, List<List<Predicate<Block>>> predicates) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = new ArrayList<>();
        for (Block b : Utils.getBlocksInRadius(center, radius)) {
            PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
            if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
            if (Utils.testBlock(b, predicates) && FUtils.isInClaimOrWilderness(p, b.getLocation())) {
                if (b.getBlockData() instanceof Door door)
                    if (door.getHalf() == Bisected.Half.TOP) b.getRelative(BlockFace.DOWN).setType(Material.AIR);
                    else b.getRelative(BlockFace.UP).setType(Material.AIR);
                drops.addAll(b.getDrops(heldItem));
                b.setType(Material.AIR, true);
            }
        }
        return drops;
    }

    public static void breakInVein(Block center, Collection<ItemStack> drops, Material material, Player p) {
        ItemStack item = p.getInventory().getItemInMainHand();
        for (Block b : getBlocksInRadius(center, 1)) {
            if (b.getType().equals(material)) {
                // Testing claim
                if (isInClaimOrWilderness(p, b.getLocation())) {
                    drops.addAll(b.getDrops(item));
                    b.setType(Material.AIR);
                    breakInVein(b, drops, material, p);
                }
            }
        }
    }

    /**
     * Combines Similar ItemStacks into one ItemStack, Each Combined Stack Won't go Over Max Stack Size
     * @author DuneSciFye
     * @param itemStacks Collection of ItemStacks
     * @return Collection of Combined ItemStacks
     */
    public static Collection<ItemStack> mergeSimilarItemStacks(Collection<ItemStack> itemStacks) {
        Map<ItemStack, Integer> mergedStacksMap = new HashMap<>(); //ItemStack with Stack Size of 1-Used to Compare, Item's Stack Size
        Collection<ItemStack> finalItems = new ArrayList<>(); //Items over Max Stack Size here

        for (ItemStack stack : itemStacks) {
            ItemStack oneStack = stack.asQuantity(1);
            int stackSize = stack.getAmount();
            Integer currentStackSize = mergedStacksMap.remove(oneStack);
            if (currentStackSize != null) {
                int maxSize = stack.getMaxStackSize();
                stackSize += currentStackSize;
                while (stackSize > maxSize) {
                    finalItems.add(stack.asQuantity(maxSize));
                    stackSize-=maxSize;
                }
            }
            if (stackSize > 0) mergedStacksMap.put(oneStack, stackSize);
        }
        for (ItemStack stack : mergedStacksMap.keySet()) { //Leftover items
            finalItems.add(stack.asQuantity(mergedStacksMap.get(stack)));
        }
        return finalItems;
    }


    public static List<Item> dropAllItemStacks(World world, Location location, Collection<ItemStack> itemStacks) {
        List<Item> items = new ArrayList<>();

        for (ItemStack item : mergeSimilarItemStacks(itemStacks)) {
            items.add(world.dropItemNaturally(location, item));
        }

        return items;
    }

    public static void boneMealRadius(Block center, int radius) {
        for (Block b : Utils.getBlocksInRadius(center, radius))
            b.applyBoneMeal(BlockFace.UP);
    }

    public static void replant(Block b) {
        Material mat = b.getType();
        if (b.getBlockData() instanceof Directional directional) {
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                b.setType(mat);
                BlockData blockData = b.getBlockData();
                ((Directional) blockData).setFacing(directional.getFacing());
                b.setBlockData(blockData);
            });
        } else Bukkit.getScheduler().runTask(getPlugin(), () -> b.setType(mat));
    }

    public static void replant(Block b, int newAge) {
        Material mat = b.getType();
        if (b.getBlockData() instanceof Directional directional) {
            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                b.setType(mat);
                BlockData blockData = b.getBlockData();
                ((Directional) blockData).setFacing(directional.getFacing());
                ((Ageable) blockData).setAge(newAge);
                b.setBlockData(blockData);
            });
        } else Bukkit.getScheduler().runTask(getPlugin(), () -> {
            b.setType(mat);
            BlockData blockData = b.getBlockData();
            ((Ageable) blockData).setAge(newAge);
            b.setBlockData(blockData);
        });
    }

    public static Map<Material, Material> cropToSeed = Map.ofEntries(
        Map.entry(Material.COCOA, Material.COCOA_BEANS),
        Map.entry(Material.WHEAT, Material.WHEAT_SEEDS),
        Map.entry(Material.POTATOES, Material.POTATO),
        Map.entry(Material.CARROTS, Material.CARROT),
        Map.entry(Material.BEETROOTS, Material.BEETROOT_SEEDS),
        Map.entry(Material.MELON_STEM, Material.MELON_SEEDS),
        Map.entry(Material.PUMPKIN_STEM, Material.PUMPKIN_SEEDS),
        Map.entry(Material.NETHER_WART, Material.NETHER_WART)
    );

    public static void replantFromInv(Player p, Block b, int newAge) {
        Material mat = b.getType();
        if (!cropToSeed.containsKey(mat)) return;
        ItemStack toRemove = new ItemStack(cropToSeed.get(mat));
        if (p.getInventory().removeItem(toRemove).isEmpty()) {
            if (b.getBlockData() instanceof Directional directional) {
                Bukkit.getScheduler().runTask(getPlugin(), () -> {
                    b.setType(mat);
                    BlockData blockData = b.getBlockData();
                    ((Directional) blockData).setFacing(directional.getFacing());
                    ((Ageable) blockData).setAge(newAge);
                    b.setBlockData(blockData);
                });
            } else Bukkit.getScheduler().runTask(getPlugin(), () -> {
                b.setType(mat);
                BlockData blockData = b.getBlockData();
                ((Ageable) blockData).setAge(newAge);
                b.setBlockData(blockData);
            });
        }
    }

}
