package me.dunescifye.lunaritems.utils;

import com.jeff_media.customblockdata.CustomBlockData;
//import com.massivecraft.factions.Board;
//import com.massivecraft.factions.FLocation;
//import com.massivecraft.factions.FPlayers;
//import com.massivecraft.factions.Faction;
//import com.massivecraft.factions.perms.PermissibleActions;
import me.dunescifye.lunaritems.LunarItems;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.utils.BlockUtils.*;
import static me.dunescifye.lunaritems.utils.Utils.getBlocksInFacing;

public class FUtils {
    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInFacingDoubleOres(Block center, int radius, int depth, Player p, List<Predicate<Block>> whitelist, List<Predicate<Block>> blacklist, int exp) {
        Collection<ItemStack> drops = new ArrayList<>();
        ItemStack heldItem = p.getInventory().getItemInMainHand();
        for (Block b : getBlocksInFacing(center, radius, depth, p)) {
            //Testing custom block
            PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
            if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING)) {
                continue;
            }
            //Testing whitelist
            for (Predicate<Block> whitelisted : whitelist) {
                if (whitelisted.test(b)) {
                    //Testing blacklist
                    if (notInBlacklist(b, blacklist)) {
                        //Testing claim
                        if (isInClaimOrWilderness(p, b.getLocation())) {
                            if (inWhitelist(b, ores) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH)) {
                                drops.addAll(b.getDrops(heldItem));
                            }
                            drops.addAll(b.getDrops(heldItem));
                            p.giveExp(exp);
                            b.setType(Material.AIR);
                            break;
                        }
                    }
                }
            }
        }

        dropAllItemStacks(center.getWorld(), center.getLocation(), drops);
    }
    public static boolean isInClaimOrWilderness(final Player player, final Location location) {
        if (LunarItems.griefPreventionEnabled) {
            final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
            return claim == null || claim.getOwnerID().equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Build);
        }/* else if (LunarItems.factionsUUIDEnabled) {
            FLocation fLocation = new FLocation(location);
            Faction faction = Board.getInstance().getFactionAt(fLocation);
            return faction.isWilderness() || faction.hasAccess(FPlayers.getInstance().getByPlayer(player), PermissibleActions.DESTROY, fLocation);
        }
        */
        return true;
    }
}
