package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import eu.decentsoftware.holograms.api.DHAPI;
import me.clip.placeholderapi.PlaceholderAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.AncienttItemsConfig;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.files.NexusItemsConfig;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
import static me.dunescifye.lunaritems.LunarItems.keyBlocksBroken;
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
                        handleAquaticHoe(drops, p, b, location, AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance, AquaticHoeAxolotlSpawnerChance, AquaticHoeFrogSpawnerChance);
                    case "aquatichoemega" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance, AquaticHoeMegaAxolotlSpawnerChance, AquaticHoeMegaFrogSpawnerChance);
                    case "aquatichoe2" ->
                        handleAquaticHoe(drops, p, b, location, AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance, AquaticHoe2AxolotlSpawnerChance, AquaticHoe2FrogSpawnerChance);
                    case "nexushoe" ->
                        handleNexusHoe(p, NexusHoePyroFarmingXPChance);
                    case "nexushoemega" ->
                        handleNexusHoe(p, NexusHoeMegaPyroFarmingXPChance);
                    case "nexushoeo" ->
                        handleNexusHoe(p, NexusHoeOPyroFarmingXPChance);
                    case "ancientthoe", "ancientthoeo" ->
                        handleAncienttHoe(drops, p, b, location, item, meta, container, AncienttItemsConfig.AncienttHoeParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeFireworkChance, AncienttItemsConfig.AncienttHoeFarmKeyChance, AncienttItemsConfig.AncienttHoeParrotSpawnerChance, AncienttItemsConfig.AncienttHoeInfiniteSeedPouchChance);
                    case "ancientthoemega"->
                        handleAncienttHoe(drops, p, b, location, item, meta, container, AncienttItemsConfig.AncienttHoeMegaParrotSpawnEggChance, AncienttItemsConfig.AncienttHoeMegaFireworkChance, AncienttItemsConfig.AncienttHoeMegaFarmKeyChance, AncienttItemsConfig.AncienttHoeMegaParrotSpawnerChance, AncienttItemsConfig.AncienttHoeMegaInfiniteSeedPouchChance);
                }
            } else {
                //Cancel breaking if plant not fully grown
                e.setCancelled(true);
            }
        }
        //Not a hoe
        else {
            switch (itemID) {
                case "nexuspick" ->
                    handleNexusPick(b, p, container, NexusPickOreBlockChance, NexusPickSquidSpawnerChance);
                case "nexuspicko" ->
                    handleNexusPick(b, p, container, NexusPickOOreBlockChance, NexusPickOSquidSpawnerChance);
                case "nexuspicku" ->
                    handleNexusPick(b, p, container, NexusPickUOreBlockChance, NexusPickUSquidSpawnerChance);
                case "nexuspickmega" ->
                    handleNexusPick(b, p, container, NexusPickMegaOreBlockChance, NexusPickMegaSquidSpawnerChance);
                case "nexusaxe" ->
                    handleNexusAxe(b, p, container, NexusAxeInfinitePouchChance, NexusAxeGlowSquidSpawnerChance);
                case "nexusaxeo" ->
                    handleNexusAxe(b, p, container, NexusAxeOInfinitePouchChance, NexusAxeOGlowSquidSpawnerChance);
                case "nexusaxeu" ->
                    handleNexusAxe(b, p, container, NexusAxeUInfinitePouchChance, NexusAxeUGlowSquidSpawnerChance);
                case "nexusaxemega" ->
                    handleNexusAxe(b, p, container, NexusAxeMegaInfinitePouchChance, NexusAxeMegaGlowSquidSpawnerChance);
                case "nexusshovel" ->
                    handleNexusShovel(b, p, container, NexusShovelSpawnerChance, NexusShovelInfinitePouchChance);
                case "nexusshovelo" ->
                    handleNexusShovel(b, p, container, NexusShovelOSpawnerChance, NexusShovelOInfinitePouchChance);
                case "nexusshovelmega" ->
                    handleNexusShovel(b, p, container, NexusShovelMegaSpawnerChance, NexusShovelMegaInfinitePouchChance);
            }
            if (itemID.contains("ancienttpick"))
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttPickaxeWhitelist, BlockUtils.pickaxeBlacklist);
            else if (itemID.contains("ancienttshovel"))
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttShovelWhitelist);
            else if (itemID.contains("ancienttaxe"))
                BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.ancienttAxeWhitelist, BlockUtils.axeBlacklist);
            //5x5 Mining
            else if (itemID.contains("septembershovel"))
                BlockUtils.
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

    private void handleNexusShovel(Block b, Player p, PersistentDataContainer container, int SpawnerChance, int InfinitePouchChance) {
        if (ThreadLocalRandom.current().nextInt(SpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", NexusShovelSpawnerType).replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + NexusShovelSpawnerMessage));
        }
        if (ThreadLocalRandom.current().nextInt(InfinitePouchChance) == 0) {
            Utils.runConsoleCommands(Config.infinitePouchCommand.replace("%player%", p.getName()));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + NexusShovelInfinitePouchMessage));
        }
        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.shovelWhitelist);
    }

    private void handleNexusPick(Block b, Player p, PersistentDataContainer container, int OreBlockChance, int glowSquidSpawnerChance) {
        if (ThreadLocalRandom.current().nextInt(OreBlockChance) == 0)
            dropOreBlock(b);
        if (ThreadLocalRandom.current().nextInt(glowSquidSpawnerChance) == 0)
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "GLOW_SQUID").replace("%amount%", "1"));
        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.pickaxeWhitelist, BlockUtils.pickaxeBlacklist);
    }

    private void handleNexusAxe(Block b, Player p, PersistentDataContainer container, int InfinitePouchChance, int GlowSquidSpawnerChance) {
        if (ThreadLocalRandom.current().nextInt(InfinitePouchChance) == 0)
            Utils.runConsoleCommands(Config.infinitePouchCommand.replace("%player%", p.getName()));
        if (ThreadLocalRandom.current().nextInt(GlowSquidSpawnerChance) == 0)
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "GLOW_SQUID").replace("%amount%", "1"));
        BlockUtils.breakInFacing(b, (int) (double) container.getOrDefault(LunarItems.keyRadius, PersistentDataType.DOUBLE, 0.0), (int) (double) container.getOrDefault(LunarItems.keyDepth, PersistentDataType.DOUBLE, 0.0), p, BlockUtils.axeWhitelist, BlockUtils.axeBlacklist);
    }

    private void handleAquaticHoe(Collection<ItemStack> drops, Player p, Block block, Location location, int farmKeyChance, int stackOfCropsChance, int axolotlSpawnEggChance, int frogSpawnEggChance, int axolotlSpawnerChance, int frogSpawnerChance) {
        if (ThreadLocalRandom.current().nextInt(farmKeyChance) == 0) {
            Utils.runConsoleCommands("crazycrates give v farm 1 " + p.getName(), "minecraft:sendmessage @a " + prefix + "&a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Farm Key")));
        }
        if (ThreadLocalRandom.current().nextInt(stackOfCropsChance) == 0) {
            drops.add(new ItemStack(block.getType(), 64));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Stack of Crops")));
        }
        if (ThreadLocalRandom.current().nextInt(axolotlSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.AXOLOTL_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "an Axolotl Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(frogSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.FROG_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Frog Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(axolotlSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "AXOLOTL").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "an Axolotl Spawner")));
        }
        if (ThreadLocalRandom.current().nextInt(frogSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "FROG").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Frog Spawner")));
        }

        //Drop drops at location
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(location, drop);
        }

        //Auto replant except for Sugar Cane, doesn't require seed
        if (block.getType() != Material.SUGAR_CANE) {
            Material material = block.getType();
            Bukkit.getScheduler().runTask(getPlugin(), () -> block.setType(material));
        }
    }

    private void handleAncienttHoe(Collection<ItemStack> drops, Player p, Block block, Location location, ItemStack item, ItemMeta meta, PersistentDataContainer container, int parrotSpawnEggChance, int fireworkChance, int farmKeyChance, int parrotSpawnerChance, int infiniteSeedPouchChance) {
        if (ThreadLocalRandom.current().nextInt(farmKeyChance) == 0) {
            Utils.runConsoleCommands("crazycrates give v farm 1 " + p.getName(), "minecraft:sendmessage @a " + prefix + "&a" + p.getName() + " &7has won &a1x Farm Key &7from their aquatic hoe!");
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Farm Key")));
        }
        if (ThreadLocalRandom.current().nextInt(parrotSpawnEggChance) == 0) {
            drops.add(new ItemStack(Material.PARROT_SPAWN_EGG));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Parrot Spawn Egg")));
        }
        if (ThreadLocalRandom.current().nextInt(fireworkChance) == 0) {
            drops.add(new ItemStack(Material.FIREWORK_ROCKET));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "a Firework Rocket")));
        }
        if (ThreadLocalRandom.current().nextInt(parrotSpawnerChance) == 0) {
            Utils.runConsoleCommands(Config.spawnerCommand.replace("%player%", p.getName()).replace("%type%", "PARROT").replace("%amount%", "1"));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Parrot Spawner")));
        }
        if (ThreadLocalRandom.current().nextInt(infiniteSeedPouchChance) == 0) {
            Utils.runConsoleCommands(Config.infinitePouchCommand.replace("%player%", p.getName()).replace("%type%", AncienttItemsConfig.AncienttHoeInfiniteSeedPouchID));
            p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + Config.receiveItemMessage.replace("%item%", "1x Infinite Seed Pouch")));
        }

        //Drop drops at location
        for (ItemStack drop : drops) {
            block.getWorld().dropItemNaturally(location, drop);
        }

        //Auto replant except for Sugar Cane, doesn't require seed
        if (block.getType() != Material.SUGAR_CANE) {
            Material material = block.getType();
            Bukkit.getScheduler().runTask(getPlugin(), () -> block.setType(material));
        }

        //Update blocks broken var
        double blocksBroken = container.get(keyBlocksBroken, PersistentDataType.DOUBLE);
        container.set(keyBlocksBroken, PersistentDataType.DOUBLE, blocksBroken + 1);
        meta.lore(Utils.updateLore(item, String.valueOf((int) blocksBroken), String.valueOf((int) blocksBroken + 1)));
        item.setItemMeta(meta);
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
