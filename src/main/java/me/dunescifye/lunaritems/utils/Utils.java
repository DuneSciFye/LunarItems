package me.dunescifye.lunaritems.utils;

import me.dunescifye.lunaritems.LunarItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.utils.ConfigUtils.isInteger;

public class Utils {

    public static final NamespacedKey autoSmeltKey = new NamespacedKey("lunaritems", "autosmelt");
    public static final NamespacedKey removeDropKey = new NamespacedKey("lunaritems", "removedrop");
    public static final NamespacedKey replaceDropKey = new NamespacedKey("lunaritems", "replacedrop");
    public static final NamespacedKey oreStorageKey = new NamespacedKey("score", "score-orestorage");
    public static final NamespacedKey autoPickupOresKey = new NamespacedKey("score", "score-autopickupores");
    public static final NamespacedKey autoReplantKey = new NamespacedKey("lunaritems", "autoreplant");
    public static final NamespacedKey autoSellKey = new NamespacedKey("lunaritems", "autosell");

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
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

        ItemStack item = player.getInventory().getItemInOffHand();
        if (item.hasItemMeta()) {
            String itemID = item.getItemMeta().getPersistentDataContainer().get(LunarItems.keyEIID,
              PersistentDataType.STRING);
            if (itemID != null && itemID.contains("sunbuilder")) {
                for (int x = xStart; x <= xEnd; x++) {
                    for (int y = yStart; y <= yEnd; y++) {
                        for (int z = zStart; z <= zEnd; z++) {
                            Block b = origin.getRelative(x, y, z);
                            if (b.getY() >= player.getY()) blocks.add(b);
                        }
                    }
                }
                return blocks;
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
      Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP),
      Map.entry(Material.NETHERITE_SCRAP, Material.NETHERITE_SCRAP),
      Map.entry(Material.NETHER_QUARTZ_ORE, Material.QUARTZ),
      Map.entry(Material.NETHER_GOLD_ORE, Material.GOLD_NUGGET),
      Map.entry(Material.QUARTZ, Material.QUARTZ)
    );
    public static final Map<Material, Material> rawOres = Map.ofEntries(
      Map.entry(Material.COAL_ORE, Material.COAL),
      Map.entry(Material.DEEPSLATE_COAL_ORE, Material.COAL),
      Map.entry(Material.RAW_COPPER, Material.COPPER_INGOT),
      Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
      Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
      Map.entry(Material.DIAMOND_ORE, Material.DIAMOND),
      Map.entry(Material.DEEPSLATE_DIAMOND_ORE, Material.DIAMOND),
      Map.entry(Material.EMERALD_ORE, Material.EMERALD),
      Map.entry(Material.DEEPSLATE_EMERALD_ORE, Material.EMERALD),
      Map.entry(Material.RAW_GOLD, Material.GOLD_INGOT),
      Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
      Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
      Map.entry(Material.RAW_IRON, Material.IRON_INGOT),
      Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
      Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
      Map.entry(Material.LAPIS_ORE, Material.LAPIS_LAZULI),
      Map.entry(Material.DEEPSLATE_LAPIS_ORE, Material.LAPIS_LAZULI),
      Map.entry(Material.REDSTONE_ORE, Material.REDSTONE),
      Map.entry(Material.DEEPSLATE_REDSTONE_ORE, Material.REDSTONE),
      Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP),
      Map.entry(Material.NETHER_QUARTZ_ORE, Material.QUARTZ),
      Map.entry(Material.NETHER_GOLD_ORE, Material.GOLD_ORE)
    );
    public static final Map<Material, Material> smeltedFoods = Map.ofEntries(
      Map.entry(Material.POTATO, Material.BAKED_POTATO),
      Map.entry(Material.BEEF, Material.COOKED_BEEF),
      Map.entry(Material.PORKCHOP, Material.COOKED_PORKCHOP),
      Map.entry(Material.MUTTON, Material.COOKED_MUTTON),
      Map.entry(Material.CHICKEN, Material.COOKED_CHICKEN),
      Map.entry(Material.RABBIT, Material.COOKED_RABBIT),
      Map.entry(Material.COD, Material.COOKED_COD),
      Map.entry(Material.SALMON, Material.COOKED_SALMON)
    );
    public static final Map<Material, Material> stripWood = Map.ofEntries(
      Map.entry(Material.OAK_LOG, Material.STRIPPED_OAK_LOG),
      Map.entry(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD),
      Map.entry(Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG),
      Map.entry(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD),
      Map.entry(Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG),
      Map.entry(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD),
      Map.entry(Material.JUNGLE_LOG, Material.STRIPPED_JUNGLE_LOG),
      Map.entry(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD),
      Map.entry(Material.ACACIA_LOG, Material.STRIPPED_ACACIA_LOG),
      Map.entry(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD),
      Map.entry(Material.DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG),
      Map.entry(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD),
      Map.entry(Material.MANGROVE_LOG, Material.STRIPPED_MANGROVE_LOG),
      Map.entry(Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_WOOD),
      Map.entry(Material.CHERRY_LOG, Material.STRIPPED_CHERRY_LOG),
      Map.entry(Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_WOOD),
      Map.entry(Material.CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM),
      Map.entry(Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE),
      Map.entry(Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM),
      Map.entry(Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE),
      Map.entry(Material.BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK),
      Map.entry(Material.PALE_OAK_LOG, Material.STRIPPED_PALE_OAK_LOG),
      Map.entry(Material.PALE_OAK_WOOD, Material.STRIPPED_PALE_OAK_WOOD)
      );

    public static final Map<Material, String> logMap = Map.ofEntries(
      Map.entry(Material.OAK_LOG, "oak"),
      Map.entry(Material.STRIPPED_OAK_LOG, "oak"),
      Map.entry(Material.BIRCH_LOG, "birch"),
      Map.entry(Material.STRIPPED_BIRCH_LOG, "birch"),
      Map.entry(Material.DARK_OAK_LOG, "dark_oak"),
      Map.entry(Material.STRIPPED_DARK_OAK_LOG, "dark_oak"),
      Map.entry(Material.SPRUCE_LOG, "spruce"),
      Map.entry(Material.STRIPPED_SPRUCE_LOG, "spruce"),
      Map.entry(Material.JUNGLE_LOG, "jungle"),
      Map.entry(Material.STRIPPED_JUNGLE_LOG, "jungle"),
      Map.entry(Material.ACACIA_LOG, "acacia"),
      Map.entry(Material.STRIPPED_ACACIA_LOG, "acacia"),
      Map.entry(Material.PALE_OAK_LOG, "pale_oak"),
      Map.entry(Material.STRIPPED_PALE_OAK_LOG, "pale_oak"),
      Map.entry(Material.MANGROVE_LOG, "mangrove"),
      Map.entry(Material.STRIPPED_MANGROVE_LOG, "mangrove"),
      Map.entry(Material.CHERRY_LOG, "cherry"),
      Map.entry(Material.STRIPPED_CHERRY_LOG, "cherry")
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


    public static Material smeltMaterial(Material mat) {
        Iterator<Recipe> iter = Bukkit.recipeIterator();
        while (iter.hasNext()) {
            Recipe recipe = iter.next();
            if (!(recipe instanceof FurnaceRecipe furnaceRecipe)) continue;
            if (furnaceRecipe.getInput().getType() != mat) continue;
            return recipe.getResult().getType();
        }
        return mat;
    }

    public static Component translateMessage(String message) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

}
