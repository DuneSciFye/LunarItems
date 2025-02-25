package me.dunescifye.lunaritems.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Predicate;

import static org.bukkit.Bukkit.getServer;

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
            block -> block.getType().equals(Material.NETHER_QUARTZ_ORE)
        ),
        List.of()
    );

    public static List<List<Predicate<Block>>> pickaxePredicates = List.of(
        List.of( // Whitelist
            block -> Tag.MINEABLE_PICKAXE.isTagged(block.getType())
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
            block -> block.getType().equals(Material.BARREL),
            block -> block.getType().equals(Material.CHEST),
            block -> block.getType().equals(Material.TRAPPED_CHEST),
            block -> Tag.ALL_SIGNS.isTagged(block.getType())
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
            block -> block.getType().equals(Material.LAVA)
        ),
        List.of( // Blacklist
            block -> block.getType().equals(Material.BARREL),
            block -> block.getType().equals(Material.CHEST),
            block -> block.getType().equals(Material.TRAPPED_CHEST),
            block -> Tag.ALL_SIGNS.isTagged(block.getType())
        )
    );

    //Breaks blocks in direction player is facing. Updates block b to air. Returns drops.
    public static Collection<ItemStack> breakInFacing(Block origin, int radius, int depth, Player p, List<List<Predicate<Block>>> predicates) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = new ArrayList<>();
        for (Block b : Utils.getBlocksInFacing(origin, radius, depth, p)) {
            PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
            if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
            if (b.equals(origin)) {
                drops.addAll(b.getDrops(heldItem));
                continue;
            }
            if (Utils.testBlock(b, predicates) && FUtils.isInClaimOrWilderness(p, b.getLocation())) {
                drops.addAll(b.getDrops(heldItem));
                b.setType(Material.AIR);
            }
        }
        return drops;
    }


    //Breaks blocks in radius. Updates block b to air.
    public static Collection<ItemStack> breakInRadius(Block center, int radius, Player p, List<List<Predicate<Block>>> predicates) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = new ArrayList<>();
        for (Block b : Utils.getBlocksInRadius(center, radius)) {
            PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
            if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
            if (b.equals(center)) {
                drops.addAll(b.getDrops(heldItem));
                continue;
            }
            if (Utils.testBlock(b, predicates) && FUtils.isInClaimOrWilderness(p, b.getLocation())) {
                drops.addAll(b.getDrops(heldItem));
                b.setType(Material.AIR);
            }
        }
        return drops;
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


    public static void dropAllItemStacks(World world, Location location, Collection<ItemStack> itemStacks) {
        for (ItemStack item : mergeSimilarItemStacks(itemStacks)) world.dropItemNaturally(location, item);
    }

    private static CoreProtectAPI getCoreProtect() {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            System.out.println("core protect plugin not found");
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
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

    public static void boneMealRadius(Block center, int radius) {
        for (Block b : Utils.getBlocksInRadius(center, radius))
            b.applyBoneMeal(BlockFace.UP);
    }

}
