package me.dunescifye.lunaritems.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.DataStore;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
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
        block -> block.getType().equals(Material.NETHER_QUARTZ_ORE)
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
    public static List<Predicate<Block>> ancienttPickaxeWhitelist = List.of(
        block -> Tag.MINEABLE_PICKAXE.isTagged(block.getType()),
        block -> block.getType().equals(Material.WATER),
        block -> block.getType().equals(Material.LAVA)
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
    public static List<Predicate<Block>> axeWhitelist = List.of(
        block -> Tag.MINEABLE_AXE.isTagged(block.getType()),
        block -> Tag.LEAVES.isTagged(block.getType())
    );
    public static List<Predicate<Block>> ancienttAxeWhitelist = List.of(
        block -> Tag.MINEABLE_AXE.isTagged(block.getType()),
        block -> Tag.LEAVES.isTagged(block.getType()),
        block -> block.getType().equals(Material.WATER),
        block -> block.getType().equals(Material.LAVA)
    );
    public static List<Predicate<Block>> axeBlacklist = List.of(
        block -> block.getType().equals(Material.BARREL),
        block -> block.getType().equals(Material.CHEST),
        block -> block.getType().equals(Material.TRAPPED_CHEST),
        block -> Tag.ALL_SIGNS.isTagged(block.getType())
    );
    public static List<Predicate<Block>> shovelWhitelist = List.of(
        block -> Tag.MINEABLE_SHOVEL.isTagged(block.getType())
    );
    public static List<Predicate<Block>> ancienttShovelWhitelist = List.of(
        block -> Tag.MINEABLE_SHOVEL.isTagged(block.getType()),
        block -> block.getType().equals(Material.WATER),
        block -> block.getType().equals(Material.LAVA)
    );

    public static boolean isInsideClaim(final Player player, final Location location) {
        final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        if (claim == null) return false;
        return claim.getOwnerID().equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Build);
    }

    public static boolean isInsideClaimOrWilderness(final Player player, final Location location) {
        final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
        return claim == null || claim.getOwnerID().equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Build);
    }
    public static boolean isWilderness(Location location) {
        return GriefPrevention.instance.dataStore.getClaimAt(location, true, null) == null;
    }

    public static boolean notInBlacklist(Block b, List<Predicate<Block>> blacklist) {
        for (Predicate<Block> blacklisted : blacklist) {
            if (blacklisted.test(b)) {
                return false;
            }
        }
        return true;
    }

    public static boolean inWhitelist(Block b, List<Predicate<Block>> whitelist) {
        for (Predicate<Block> predicate : whitelist) {
            if (predicate.test(b)) {
                return true;
            }
        }
        return false;
    }

    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInFacing(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                            continue;
                        }
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                            continue;
                        }
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }
    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInFacingDoubleOres(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    block: for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    if (isInsideClaimOrWilderness(p, relative.getLocation())) {
                                        if (inWhitelist(relative, ores) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
                                            drops.addAll(relative.getDrops(heldItem));
                                        }
                                        if (relative.equals(b)) {
                                            drops.addAll(relative.getDrops(heldItem));
                                            continue block;
                                        }
                                        drops.addAll(relative.getDrops(heldItem));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    block: for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    if (inWhitelist(relative, ores) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                    }
                                    if (relative.equals(b)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                        continue block;
                                    }
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }
    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInFacingAutoPickup(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        PlayerInventory inv = p.getInventory();
        for (ItemStack item : mergeSimilarItemStacks(drops)){
            if (inv.firstEmpty() == -1) {
                dropAllItemStacks(drops, b.getWorld(), b.getLocation());
                return;
            }
            else {
                inv.addItem(item);
                drops.remove(item);
            }
        }
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Only whitelist. Replaces all drops with material.
    public static void breakInFacing(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, Material material) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        if (relative.equals(b)) {
                            drops.add(new ItemStack(material));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing claim
                                Location relativeLocation = relative.getLocation();
                                if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                    //Testing custom block
                                    PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                                    if (!blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                                        drops.add(new ItemStack(material));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        if (relative.equals(b)) {
                            drops.add(new ItemStack(material));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing custom block
                                PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                                if (!blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                                    drops.add(new ItemStack(material));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Only whitelist. Replaces all drops with material.
    public static void breakInFacing(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist, Material material) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        if (relative.equals(b)) {
                            drops.add(new ItemStack(material));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative) && notInBlacklist(relative, blacklist)) {
                                //Testing claim
                                Location relativeLocation = relative.getLocation();
                                if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                    //Testing custom block
                                    PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                                    if (!blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                                        drops.add(new ItemStack(material));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        if (relative.equals(b)) {
                            drops.add(new ItemStack(material));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing custom block
                                PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                                if (!blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                                    drops.add(new ItemStack(material));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Replaces drop A with drop B
    public static void breakInFacing(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist, String dropFromContains, Material dropTo) {
        if (b == null) return;
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        //Ignore actual block broken
                        if (relative.equals(b)) {
                            for (ItemStack drop : relative.getDrops(heldItem)) {
                                if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                else drops.add(drop);
                            }
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        for (ItemStack drop : relative.getDrops(heldItem)) {
                                            if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                            else drops.add(drop);
                                        }
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        //Ignore actual block broken
                        if (relative.equals(b)) {
                            for (ItemStack drop : relative.getDrops(heldItem)) {
                                if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                else drops.add(drop);
                            }
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Replace drops
                                    for (ItemStack drop : relative.getDrops(heldItem)) {
                                        if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                        else drops.add(drop);
                                    }
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }
    //Breaks blocks in direction player is facing. Updates block b to air. Replaces drop A with drop B
    public static void breakInFacingAutoPickup(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist, String dropFromContains, Material dropTo) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        //Ignore actual block broken
                        if (relative.equals(b)) {
                            for (ItemStack drop : relative.getDrops(heldItem)) {
                                if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                else drops.add(drop);
                            }
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        for (ItemStack drop : relative.getDrops(heldItem)) {
                                            if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                            else drops.add(drop);
                                        }
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        //Ignore actual block broken
                        if (relative.equals(b)) {
                            for (ItemStack drop : relative.getDrops(heldItem)) {
                                if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                else drops.add(drop);
                            }
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Replace drops
                                    for (ItemStack drop : relative.getDrops(heldItem)) {
                                        if (drop.getType().toString().contains(dropFromContains)) drops.add(new ItemStack(dropTo, drop.getAmount()));
                                        else drops.add(drop);
                                    }
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        PlayerInventory inv = p.getInventory();
        for (ItemStack item : mergeSimilarItemStacks(drops)){
            if (inv.firstEmpty() == -1) {
                dropAllItemStacks(drops, b.getWorld(), b.getLocation());
                return;
            }
            else {
                inv.addItem(item);
                drops.remove(item);
            }
        }
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Only whitelist.
    public static void breakInFacing(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing claim
                                Location relativeLocation = relative.getLocation();
                                if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                drops.addAll(relative.getDrops(heldItem));
                                relative.setType(Material.AIR);
                                break;
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }
    //Breaks blocks in direction player is facing. Updates block b to air. Only whitelist. AutoPickup
    public static void breakInFacingAutoPickup(Block b, int radius, int depth, Player p, List<Predicate<Block>> whitelist) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing claim
                                Location relativeLocation = relative.getLocation();
                                if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) {
                            drops.addAll(relative.getDrops(heldItem));
                            continue;
                        }
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                drops.addAll(relative.getDrops(heldItem));
                                relative.setType(Material.AIR);
                                break;
                            }
                        }
                    }
                }
            }
        }

        PlayerInventory inv = p.getInventory();
        for (ItemStack item : mergeSimilarItemStacks(drops)){
            if (inv.firstEmpty() == -1) {
                dropAllItemStacks(drops, b.getWorld(), b.getLocation());
                return;
            }
            else {
                inv.addItem(item);
                drops.remove(item);
            }
        }
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Only blocks that contains arg A. Appends part B to front of block as drop.
    public static void breakInFacingAddFront(Block b, int radius, int depth, Player p, String contains, String append) {
        depth = depth < 1 ? 1 : depth -1;
        double pitch = p.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (p.getFacing()) {
                case NORTH -> {
                    zStart = -depth;
                    zEnd = 0;
                }
                case SOUTH -> {
                    zStart = 0;
                    zEnd = depth;
                }
                case WEST -> {
                    xStart = -depth;
                    xEnd = 0;
                }
                case EAST -> {
                    xStart = 0;
                    xEnd = depth;
                }
            }
        }
        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        String material = relative.getType().toString();
                        if (relative.equals(b)) {
                            b.getDrops().clear();
                            drops.add(new ItemStack(Material.getMaterial(append + material)));
                            continue;
                        }
                        //Testing whitelist
                        if (material.contains(contains)) {
                            //Testing claim
                            Location relativeLocation = relative.getLocation();
                            if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                drops.add(new ItemStack(Material.getMaterial(append + material)));
                                relative.setType(Material.AIR);
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = xStart; x <= xEnd; x++) {
                for (int y = yStart; y <= yEnd; y++) {
                    for (int z = zStart; z <= zEnd; z++) {
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        //Testing whitelist
                        String material = relative.getType().toString();
                        if (material.contains(contains)) {
                            drops.add(new ItemStack(Material.getMaterial(append + material)));
                            relative.setType(Material.AIR);
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }
    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInRadius(Block b, int radius, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = -radius; x <= radius; x++){
                for (int y = -radius; y <= radius; y++){
                    for (int z = -radius; z <= radius; z++){
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) continue;
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    //Testing claim
                                    Location relativeLocation = relative.getLocation();
                                    if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                        drops.addAll(relative.getDrops(heldItem));
                                        relative.setType(Material.AIR);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = -radius; x <= radius; x++){
                for (int y = -radius; y <= radius; y++){
                    for (int z = -radius; z <= radius; z++){
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) continue;
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing blacklist
                                if (notInBlacklist(relative, blacklist)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
    }

    //Breaks blocks in direction player is facing. Updates block b to air. Only whitelist.
    public static void breakInRadius(Block b, int radius, Player p, List<Predicate<Block>> whitelist) {
        ItemStack heldItem = p.getInventory().getItemInMainHand();

        //If GriefPrevention enabled
        Collection<ItemStack> drops = new ArrayList<>();
        if (LunarItems.griefPreventionEnabled) {
            for (int x = -radius; x <= radius; x++){
                for (int y = -radius; y <= radius; y++){
                    for (int z = -radius; z <= radius; z++){
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) continue;
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                //Testing claim
                                Location relativeLocation = relative.getLocation();
                                if (isInsideClaim(p, relativeLocation) || isWilderness(relativeLocation)) {
                                    drops.addAll(relative.getDrops(heldItem));
                                    relative.setType(Material.AIR);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            for (int x = -radius; x <= radius; x++){
                for (int y = -radius; y <= radius; y++){
                    for (int z = -radius; z <= radius; z++){
                        Block relative = b.getRelative(x, y, z);
                        //Testing custom block
                        PersistentDataContainer blockContainer = new CustomBlockData(relative, LunarItems.getPlugin());
                        if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) continue;
                        if (relative.equals(b)) continue;
                        //Testing whitelist
                        for (Predicate<Block> whitelisted : whitelist) {
                            if (whitelisted.test(relative)) {
                                drops.addAll(relative.getDrops(heldItem));
                                relative.setType(Material.AIR);
                                break;
                            }
                        }
                    }
                }
            }
        }

        dropAllItemStacks(drops, b.getWorld(), b.getLocation());
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


    public static void dropAllItemStacks(Collection<ItemStack> itemStacks, World world, Location location) {
        for (ItemStack item : mergeSimilarItemStacks(itemStacks)) {
            int amount = item.getAmount();
            while (amount > 64) {
                item.setAmount(64);
                world.dropItemNaturally(location, item);
                amount -= 64;
            }
            item.setAmount(amount);
            world.dropItemNaturally(location, item);
        }
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
