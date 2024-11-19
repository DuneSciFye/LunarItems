package me.dunescifye.lunaritems.utils;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.perms.PermissibleActions;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.dunescifye.lunaritems.LunarItems;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;

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

    private static CoreProtectAPI getCoreProtect() {
        return coreProtectAPI;
    }

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    //Drop item at player
    public static void dropItems(Location location, ItemStack... items) {
        for (ItemStack item : items) {
            if (item.getAmount() > 64) {
                while (item.getAmount() > 0) {
                    Item drop = location.getWorld().dropItemNaturally(location, item);
                    item.setAmount(item.getAmount() - 64);
                    drop.setPickupDelay(0);
                }
            } else {
                Item drop = location.getWorld().dropItemNaturally(location, item);
                drop.setPickupDelay(0);
            }
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
        List<Component> loreList = item.lore();

        TextReplacementConfig config = TextReplacementConfig.builder()
            .matchLiteral(matcher)
            .replacement(replacement)
            .build();

        if (loreList != null)
            loreList.replaceAll(component -> component.replaceText(config));

        return loreList;
    }

    public static boolean isInRegion(Player p, List<String> regions) {
        for (ProtectedRegion rg : WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(p.getLocation()))) {
            if (regions.contains(rg.getId().toLowerCase())) return true;
        }
        return false;
    }



    public static boolean testBlock(Block b, List<Predicate<Block>>[] predicates) {
        for (Predicate<Block> whitelist : predicates[0])
            if (whitelist.test(b)) {
                for (Predicate<Block> blacklist : predicates[1])
                    if (blacklist.test(b)) return false;
                return true;
            }
        return false;
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

}
