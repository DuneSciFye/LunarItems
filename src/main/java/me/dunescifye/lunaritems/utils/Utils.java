package me.dunescifye.lunaritems.utils;

import me.dunescifye.lunaritems.LunarItems;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

import static me.dunescifye.lunaritems.utils.ConfigUtils.isInteger;
import static org.bukkit.Bukkit.getServer;

public class Utils {

    private static final CoreProtectAPI coreProtectAPI;

    static {
        Plugin plugin = getServer().getPluginManager().getPlugin("CoreProtect");
        Logger logger = LunarItems.getPlugin().getLogger();

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            logger.warning("core protect plugin not found");
            coreProtectAPI = null;
        } else {
            // Check that the API is enabled
            CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
            if (!CoreProtect.isEnabled()) {
                logger.warning("core protect api is not enabled");
                coreProtectAPI = null;
            } else {
                // Check that a compatible version of the API is loaded
                if (CoreProtect.APIVersion() < 10) {
                    logger.warning("core protect api version is not supported");
                    coreProtectAPI = null;
                } else {
                    coreProtectAPI = CoreProtect;
                }
            }
        }
    }

    public static CoreProtectAPI getCoreProtect() {
        return coreProtectAPI;
    }

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    public static boolean isNaturallyGenerated(Block block) {
        List<String[]> lookup = coreProtectAPI.queueLookup(block);
        if (lookup == null || lookup.isEmpty()) {
            lookup = coreProtectAPI.blockLookup(block, 2147483647);
        } else {
            return false;
        }
        if (lookup != null && !lookup.isEmpty()) {
            CoreProtectAPI.ParseResult parseResult = coreProtectAPI.parseResult(lookup.getFirst());
            return parseResult.getPlayer().startsWith("#") || parseResult.isRolledBack();
        }
        return true;
    }


    //Drop item with owner
    public static void dropItems(Location location, UUID uuid, ItemStack... items) {
        for (ItemStack item : items) {
            if (item.getAmount() > 64) {
                while (item.getAmount() > 0) {
                    Item drop = location.getWorld().dropItemNaturally(location, item);
                    item.setAmount(item.getAmount() - 64);
                    drop.setPickupDelay(0);
                    drop.setOwner(uuid);
                }
            } else {
                Item drop = location.getWorld().dropItemNaturally(location, item);
                drop.setPickupDelay(0);
                drop.setOwner(uuid);
            }
        }
    }


    public static List<Component> updateLore(ItemStack item, String matcher, String replacement){
        ItemMeta meta = item.getItemMeta();
        List<Component> loreList = meta.lore();
        List<Component> newLore = new ArrayList<>();

        if (loreList != null) {
            matcher = matcher.replace("§", "&");
            replacement = replacement.replace("§", "&");

            for (Component component : loreList) {
                newLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(LegacyComponentSerializer.legacyAmpersand().serialize(component).replace(matcher, replacement)).decoration(TextDecoration.ITALIC, false));
            }
        }

        return newLore;
    }

    public static boolean testBlock(Block b, List<List<Predicate<Block>>> predicates) {
        if (predicates == null) return true; //Used for fully empty lists
        List<Predicate<Block>> whitelistPredicates = predicates.getFirst();
        List<Predicate<Block>> blacklistPredicates = predicates.getLast();

        // If whitelist is empty, only check blacklist
        if (whitelistPredicates.isEmpty())
            return blacklistPredicates.stream().noneMatch(predicate -> predicate.test(b));

        // Check whitelist and ensure no blacklist match if any whitelist condition is met
        return whitelistPredicates.stream().anyMatch(predicate -> predicate.test(b)) &&
            blacklistPredicates.stream().noneMatch(predicate -> predicate.test(b));
    }

    public static Set<Block> getBlocksInRadius(Block origin, final int radius) {
        Set<Block> blocks = new HashSet<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(origin.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Set<Block> getBlocksInFacing(Block origin, final int radius, int depth, final Player player) {
        Set<Block> blocks = new HashSet<>();
        depth = depth > 0 ? depth - 1 : depth;

        double pitch = player.getLocation().getPitch();
        int xStart = -radius, yStart = -radius, zStart = -radius, xEnd = radius, yEnd = radius, zEnd = radius;
        if (pitch < -45) {
            yStart = 0;
            yEnd = depth;
        } else if (pitch > 45) {
            yStart = -depth;
            yEnd = 0;
        } else {
            switch (player.getFacing()) {
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

        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y <= yEnd; y++) {
                for (int z = zStart; z <= zEnd; z++) {
                    blocks.add(origin.getRelative(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static String[] getItemSlots() {
        return new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "main", "mainhand", "off", "offhand", "cursor"};
    }
    public static ItemStack getInvItem(Player player, String input) {
        PlayerInventory inv = player.getInventory();
        if (isInteger(input)) {
            int slot = Integer.parseInt(input);
            return slot == -1 ? inv.getItemInMainHand() : inv.getItem(slot);
        } else {
            switch (input) {
                case "main", "mainhand" -> {
                    return inv.getItemInMainHand();
                }
                case "offhand", "off" -> {
                    return inv.getItemInOffHand();
                }
                case "cursor" -> {
                    return player.getItemOnCursor();
                }
            }
        }

        return null;
    }

    public static final Map<Material, Material> smeltedOres = Map.ofEntries(
        Map.entry(Material.COAL, Material.COAL),
        Map.entry(Material.COAL_ORE, Material.COAL),
        Map.entry(Material.DEEPSLATE_COAL_ORE, Material.COAL),
        Map.entry(Material.RAW_COPPER, Material.COPPER_INGOT),
        Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
        Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
        Map.entry(Material.DIAMOND, Material.DIAMOND),
        Map.entry(Material.DIAMOND_ORE, Material.DIAMOND),
        Map.entry(Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND),
        Map.entry(Material.EMERALD, Material.EMERALD),
        Map.entry(Material.EMERALD_ORE, Material.EMERALD),
        Map.entry(Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD),
        Map.entry(Material.RAW_GOLD, Material.GOLD_INGOT),
        Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
        Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
        Map.entry(Material.RAW_IRON, Material.IRON_INGOT),
        Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
        Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
        Map.entry(Material.LAPIS_LAZULI, Material.LAPIS_LAZULI),
        Map.entry(Material.LAPIS_ORE, Material.LAPIS_LAZULI),
        Map.entry(Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_LAZULI),
        Map.entry(Material.REDSTONE, Material.REDSTONE),
        Map.entry(Material.REDSTONE_ORE, Material.REDSTONE),
        Map.entry(Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE),
        Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP)
    );

    public static final Map<Material, Material> glazeTerracotta = Map.ofEntries(
        Map.entry(Material.WHITE_TERRACOTTA, Material.WHITE_GLAZED_TERRACOTTA),
        Map.entry(Material.LIGHT_GRAY_TERRACOTTA, Material.LIGHT_GRAY_GLAZED_TERRACOTTA),
        Map.entry(Material.GRAY_TERRACOTTA, Material.GRAY_GLAZED_TERRACOTTA),
        Map.entry(Material.BLACK_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA),
        Map.entry(Material.BROWN_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA),
        Map.entry(Material.RED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA),
        Map.entry(Material.ORANGE_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA),
        Map.entry(Material.YELLOW_TERRACOTTA, Material.YELLOW_GLAZED_TERRACOTTA),
        Map.entry(Material.LIME_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA),
        Map.entry(Material.GREEN_TERRACOTTA, Material.GREEN_GLAZED_TERRACOTTA),
        Map.entry(Material.CYAN_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA),
        Map.entry(Material.LIGHT_BLUE_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA),
        Map.entry(Material.BLUE_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA),
        Map.entry(Material.PURPLE_TERRACOTTA, Material.PURPLE_GLAZED_TERRACOTTA),
        Map.entry(Material.MAGENTA_TERRACOTTA, Material.MAGENTA_GLAZED_TERRACOTTA),
        Map.entry(Material.PINK_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA)
    );

}
