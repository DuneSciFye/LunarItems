package me.dunescifye.lunaritems.utils;

import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Predicate;

import static org.bukkit.Bukkit.getServer;

public class BlockUtils {

    public static List<Predicate<Block>> ores = List.of(
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
    );

    public static List<Predicate<Block>> regularOres = List.of(
        block -> block.getType().equals(Material.GOLD_ORE),
        block -> block.getType().equals(Material.IRON_ORE),
        block -> block.getType().equals(Material.COAL_ORE),
        block -> block.getType().equals(Material.COPPER_ORE),
        block -> block.getType().equals(Material.REDSTONE_ORE),
        block -> block.getType().equals(Material.LAPIS_ORE),
        block -> block.getType().equals(Material.DIAMOND_ORE),
        block -> block.getType().equals(Material.EMERALD_ORE)
    );
    public static List<Predicate<Block>> deepslateOres = List.of(
        block -> block.getType().equals(Material.DEEPSLATE_GOLD_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_IRON_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_COAL_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_COPPER_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_REDSTONE_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_LAPIS_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_DIAMOND_ORE),
        block -> block.getType().equals(Material.DEEPSLATE_EMERALD_ORE)
    );

    public static List<Predicate<Block>> pickaxeWhitelist = List.of(
        block -> Tag.MINEABLE_PICKAXE.isTagged(block.getType())
    );
    public static List<Predicate<Block>> pickaxeBlacklist = List.of(
        block -> block.getType().equals(Material.SPAWNER),
        block -> block.getType().equals(Material.GILDED_BLACKSTONE),
        block -> block.getType().equals(Material.DROPPER),
        block -> block.getType().equals(Material.DISPENSER),
        block -> block.getType().equals(Material.HOPPER),
        block -> block.getType().equals(Material.FURNACE),
        block -> block.getType().equals(Material.BLAST_FURNACE),
        block -> block.getType().equals(Material.SMOKER),
        block -> Tag.SHULKER_BOXES.isTagged(block.getType())
    );


    public static boolean isInsideClaim(final Player player, final Location blockLocation) {
        final DataStore dataStore = GriefPrevention.instance.dataStore;
        return dataStore.getClaimAt(blockLocation, false, dataStore.getPlayerData(player.getUniqueId()).lastClaim) != null;
    }
    public static boolean isWilderness(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) == null;
    }

    public static void breakInFacing(Block b, double radius, double depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        Collection<ItemStack> drops = new ArrayList<>();

        for (int x = (int) -radius; x <= radius; x++) {
            for (int y = (int) -radius; y <= radius; y++) {
                block: for (int z = (int) -radius; z <= radius; z++) {
                    Block relative = b.getRelative(x, y, z);
                    //Testing whitelist
                    for (Predicate<Block> whitelisted : whitelist) {
                        if (whitelisted.test(relative)) {
                            //Testing blacklist
                            for (Predicate<Block> blacklisted : blacklist) {
                                if (!blacklisted.test(relative)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                        relative.setType(Material.AIR);
                                        break block;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (ItemStack item : mergeSimilarItemStacks(drops)){
            b.getWorld().dropItemNaturally(b.getLocation(), item);
        }
    }

    public static Collection<ItemStack> mergeSimilarItemStacks(Collection<ItemStack> itemStacks) {
        Map<Material, ItemStack> mergedStacksMap = new HashMap<>();

        for (ItemStack stack : itemStacks) {
            Material material = stack.getType();
            ItemStack existing = mergedStacksMap.get(material);
            if (existing == null) {
                mergedStacksMap.put(material, stack.clone());
            } else {
                existing.setAmount(existing.getAmount() + stack.getAmount());
            }
        }
        return mergedStacksMap.values();
    }

    public static boolean isNaturallyGenerated(Block block) {
        List<String[]> lookup = getCoreProtect().blockLookup(block, 2147483647);
        if (lookup != null && !lookup.isEmpty()) {
            CoreProtectAPI.ParseResult parseResult = getCoreProtect().parseResult(lookup.get(0));
            return parseResult.getPlayer().startsWith("#") || parseResult.getActionId() != 1 || parseResult.isRolledBack();
        }
        return true;
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

    public static void boneMealRadius(Location location, int radius) {
        Block block = location.getBlock();
        for (int x = -radius; x <= radius; x++){
            for (int y = -radius; y <= radius; y++){
                for (int z = -radius; z <= radius; z++){
                    Block b = block.getRelative(x, y, z);
                    b.applyBoneMeal(BlockFace.UP);
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
