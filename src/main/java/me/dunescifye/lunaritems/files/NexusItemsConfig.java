package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;
import static me.dunescifye.lunaritems.utils.ConfigUtils.setupConfig;

public class NexusItemsConfig {

    public static ItemStack NexusHoe, NexusPick;

    public static int NexusHoePyroFarmingXPChance, NexusHoeOPyroFarmingXPChance, NexusHoeMegaPyroFarmingXPChance, NexusPickOreBlockChance,
        NexusPickSquidSpawnerChance;

    public static void setup() {
        ConfigUtils nexusItems = new ConfigUtils(getPlugin(), "items/NexusItems.yml");
        FileConfiguration config = nexusItems.getConfig();

        if (!config.isSet("NexusHoe")){
            config.addDefault("NexusHoe.name", "&8[#b2e300&lϟ#b2e300&lN#ffe606&lE#ffe606&lX#b2e300&lU#b2e300&lS #b2e300&lH#ffe606&lO#ffe606&lE&lϟ&8]");
            List<String> lore = Arrays.asList(
                "&7Unbreakable",
                "&7&l&m----------------",
                "&e⭐&fGives miniscule amounts of #33ff85pyro farming XP&7.",
                "",
                "&e⭐&fShift Right click to #33ff85bonemeal &fin a #33ff8510x10&f.",
                "&7&l&m----------------"
            );
            config.addDefault("NexusHoe.lore", lore);
            config.addDefault("NexusHoe.customModelData", 10039);
            config.addDefault("NexusHoe.material", "NETHERITE_HOE");
            config.addDefault("NexusHoe.unbreakable", true);
        }

        NexusHoe = ConfigUtils.initializeItem("NexusHoe", config, LunarItems.keyUses);
        NexusHoePyroFarmingXPChance = setupConfig("NexusHoe.Chances.PyroFarmingXPChance", config, 4000, 1);
        NexusHoeOPyroFarmingXPChance = setupConfig("NexusHoeO.Chances.PyroFarmingXPChance", config, 4000, 1);
        NexusHoeMegaPyroFarmingXPChance = setupConfig("NexusHoeMega.Chances.PyroFarmingXPChance", config, 4000, 1);

        NexusPick = ConfigUtils.initializeItem("NexusPick", config);
        NexusPickOreBlockChance = ConfigUtils.setupConfig("NexusPick.OreBlockChance", config, 30, 1, List.of("Chance to obtain ore block of mined ore."));
        NexusPickSquidSpawnerChance = ConfigUtils.setupConfig("NexusPick.SquidSpawnerChance", config, 125000, 1, List.of("Chance to squid spawner   ."));

        config.options().copyDefaults(true);

        nexusItems.save();

    }
}
