package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import eu.decentsoftware.holograms.api.DHAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;
import static me.dunescifye.lunaritems.files.AquaticItemsConfig.*;
import static me.dunescifye.lunaritems.files.Config.prefix;
import static me.dunescifye.lunaritems.files.NexusItemsConfig.*;

public class BlockBreakListener implements Listener {

    public void blockBreakHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e) {
        if (e.isCancelled()) return;

        Player p = e.getPlayer();
        Block b = e.getBlock();
        ItemStack item = p.getInventory().getItemInMainHand();

        if (!item.hasItemMeta()) return;
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;
        //Hoes
        if (b.getBlockData() instanceof Ageable ageable) {
            if (ageable.getAge() == ageable.getMaximumAge() || b.getType() == Material.SUGAR_CANE) {
                Collection<ItemStack> drops = b.getDrops(item);
                Location location = b.getLocation();
                switch (Objects.requireNonNull(itemID)) {
                    case "aquatichoe" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance);
                    case "aquatichoemega" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance);
                    case "aquatichoe2" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance);
                    case "nexushoe" ->
                        handleNexusHoe(p, NexusHoePyroFarmingXPChance);
                    case "nexushoemega" ->
                        handleNexusHoe(p, NexusHoeMegaPyroFarmingXPChance);
                    case "nexushoeo" ->
                        handleNexusHoe(p, NexusHoeOPyroFarmingXPChance);
                }
            } else {
                //Cancel breaking if plant not fully grown
                e.setCancelled(true);
            }
        }
        //Not a hoe
        else {
            switch (itemID) {
                case "nexuspick" -> {
                    if (ThreadLocalRandom.current().nextInt(NexusPickOreBlockChance) == 0)
                        dropOreBlock(b);
                    if (ThreadLocalRandom.current().nextInt(NexusPickSquidSpawnerChance) == 0)
                        Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "SQUID").replace("%amount%", "1"));
                    BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 1.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist);
                }
            }
        }
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(b, LunarItems.getPlugin());
        if (blockContainer.has(LunarItems.keyID, PersistentDataType.STRING)) {
            String blockID = blockContainer.get(LunarItems.keyID, PersistentDataType.STRING);
            switch (Objects.requireNonNull(blockID)) {
                case "teleport_pad" -> {
                    //Drop custom item
                    Location loc = b.getLocation();
                    e.setDropItems(false);
                    b.getWorld().dropItemNaturally(loc, BlocksConfig.teleport_pad);

                    //Remove hologram
                    String hologramID = blockContainer.get(LunarItems.keyUUID, PersistentDataType.STRING);
                    if (hologramID != null)
                        DHAPI.removeHologram(hologramID);

                    //Make linked teleport pad not work
                    Location targetLocation = blockContainer.get(LunarItems.keyLocation, DataType.LOCATION);
                    if (targetLocation != null) {
                        Block targetBlock = b.getWorld().getBlockAt(targetLocation);
                        PersistentDataContainer targetBlockContainer = new CustomBlockData(targetBlock, LunarItems.getPlugin());
                        if (Objects.equals(targetBlockContainer.get(LunarItems.keyID, PersistentDataType.STRING), "teleport_pad"))
                            targetBlockContainer.remove(LunarItems.keyLocation);
                    }
                }
            }
        }
    }

    private void handleAquaticHoe(Collection<ItemStack> drops, Player player, Block block, Location location, int farmKeyChance, int stackOfCropsChance, int axolotlSpawnEggChance, int frogSpawnEggChance) {
        if (ThreadLocalRandom.current().nextInt(farmKeyChance) == 0) {
            Utils.runConsoleCommands("crazycrates give v farm 1 " + player.getName(), "minecraft:sendmessage @a " + prefix + "&a" + player.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
        }
        if (ThreadLocalRandom.current().nextInt(stackOfCropsChance) == 0) {
            drops.add(new ItemStack(block.getType(), 64));
        }
        if (ThreadLocalRandom.current().nextInt(axolotlSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
        }
        if (ThreadLocalRandom.current().nextInt(frogSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
        }

        //Drop drops at location
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(location, drop);
        }

        //Auto replant except for Sugar Cane
        if (block.getType() != Material.SUGAR_CANE) {
            Material material = block.getType();
            Bukkit.getScheduler().runTask(getPlugin(), () -> block.setType(material));
        }
    }

    private void handleNexusHoe(Player player, int xpChance) {
        if (ThreadLocalRandom.current().nextInt(xpChance) == 0) {
            Utils.runConsoleCommands("pyrofarming addxp " + ThreadLocalRandom.current().nextInt(1, 6) + " " + player.getName());
        }
    }

    private void dropOreBlock(Block b) {
        for (Predicate<Block> predicate : BlockUtils.regularOres) {
            if (predicate.test(b)) {
                String material = b.getType().toString();
                Utils.dropItems(b.getLocation(), new ItemStack(Material.getMaterial(material.substring(0, material.length() - 3) + "BLOCK")));
                return;
            }
        }
        for (Predicate<Block> predicate : BlockUtils.deepslateOres) {
            if (predicate.test(b)) {
                String material = b.getType().toString();
                Utils.dropItems(b.getLocation(), new ItemStack(Material.getMaterial(material.substring(10, material.length() - 3) + "BLOCK")));
                return;
            }
        }
    }

}
