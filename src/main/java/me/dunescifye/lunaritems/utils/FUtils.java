package me.dunescifye.lunaritems.utils;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.LunarItems;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static com.massivecraft.factions.listeners.FactionsBlockListener.playerCanBuildDestroyBlock;
import static me.dunescifye.lunaritems.utils.BlockUtils.*;
import static me.dunescifye.lunaritems.utils.Utils.getBlocksInFacing;
import static me.dunescifye.lunaritems.utils.Utils.testBlock;

public class FUtils {
    //Breaks blocks in direction player is facing. Updates block b to air.
    public static void breakInFacingDoubleOres(Block center, int radius, int depth, Player p, List<List<Predicate<Block>>> predicates, int exp) {
        Collection<ItemStack> drops = new ArrayList<>();
        ItemStack heldItem = p.getInventory().getItemInMainHand();
        for (Block b : getBlocksInFacing(center, radius, depth, p)) {
            // Testing custom block
            PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
            if (blockContainer.has(LunarItems.keyEIID, PersistentDataType.STRING))
                continue;
            if (testBlock(b, predicates) && isInClaimOrWilderness(p, b.getLocation())) {
                if (testBlock(b, ores) && !heldItem.containsEnchantment(Enchantment.SILK_TOUCH))
                    drops.addAll(b.getDrops(heldItem));
                drops.addAll(b.getDrops(heldItem));
                p.giveExp(exp);
                b.setType(Material.AIR);
            }
        }

        dropAllItemStacks(center.getWorld(), center.getLocation(), drops);
    }
    public static boolean isInClaimOrWilderness(final Player player, final Location location) {
        if (LunarItems.griefPreventionEnabled) {
            final Claim claim = GriefPrevention.instance.dataStore.getClaimAt(location, true, null);
            return claim == null || claim.getOwnerID().equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Build);
        } else if (LunarItems.factionsUUIDEnabled) {
            return playerCanBuildDestroyBlock(player, location, "destroy", true);
        }
        return true;
    }
}
