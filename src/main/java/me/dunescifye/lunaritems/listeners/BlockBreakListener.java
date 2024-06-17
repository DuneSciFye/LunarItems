package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
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

        Player player = e.getPlayer();
        Block block = e.getBlock();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(LunarItems.keyItemID, PersistentDataType.STRING)) {
                String itemID = container.get(LunarItems.keyItemID, PersistentDataType.STRING);
                if (block.getBlockData() instanceof Ageable ageable) {
                    if (ageable.getAge() == ageable.getMaximumAge() || block.getType() == Material.SUGAR_CANE) {
                        Collection<ItemStack> drops = block.getDrops(item);
                        Location location = block.getLocation();

                        switch (Objects.requireNonNull(itemID)) {
                            case "aquatichoe":
                                handleAquaticHoe(drops, player, block, location, AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance);
                                break;
                            case "aquatichoemega":
                                handleAquaticHoe(drops, player, block, location, AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance);
                                break;
                            case "aquatichoe2":
                                handleAquaticHoe(drops, player, block, location, AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance);
                                break;
                            case "nexushoe":
                                handleNexusHoe(player, NexusHoePyroFarmingXPChance);
                                break;
                            case "nexushoemega":
                                handleNexusHoe(player, NexusHoeMegaPyroFarmingXPChance);
                                break;
                            case "nexushoeo":
                                handleNexusHoe(player, NexusHoeOPyroFarmingXPChance);
                                break;
                        }
                    } else {
                        //Cancel breaking if plant not fully grown
                        e.setCancelled(true);
                    }
                }
            }
        }
        //Custom Blocks
        PersistentDataContainer blockContainer = new CustomBlockData(block, LunarItems.getPlugin());
        if (blockContainer.has(LunarItems.keyID, PersistentDataType.STRING)) {
            String blockID = blockContainer.get(LunarItems.keyID, PersistentDataType.STRING);
            switch (Objects.requireNonNull(blockID)) {
                case "teleport_pad":
                    //Drop custom item
                    Location loc = block.getLocation();
                    e.setDropItems(false);
                    block.getWorld().dropItemNaturally(loc, BlocksConfig.teleport_pad);

                    //Make linked teleport pad not work
                    Location targetLocation = blockContainer.get(LunarItems.keyLocation, DataType.LOCATION);
                    if (targetLocation != null) {
                        Block targetBlock = block.getWorld().getBlockAt(targetLocation);
                        PersistentDataContainer targetBlockContainer = new CustomBlockData(targetBlock, LunarItems.getPlugin());
                        if (Objects.equals(targetBlockContainer.get(LunarItems.keyID, PersistentDataType.STRING), "teleport_pad"))
                            targetBlockContainer.remove(LunarItems.keyLocation);
                    }

                    break;
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

}
