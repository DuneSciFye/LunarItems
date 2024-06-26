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

    public static ItemStack NexusHoe, NexusPick, NexusPickO, NexusPickU, NexusPickMega, NexusAxe, NexusAxeO, NexusAxeU, NexusAxeMega,
        NexusShovel, NexusShovelO, NexusShovelMega;

    public static int NexusHoePyroFarmingXPChance, NexusHoeOPyroFarmingXPChance, NexusHoeMegaPyroFarmingXPChance, NexusPickOreBlockChance,
        NexusPickSquidSpawnerChance, NexusPickOOreBlockChance, NexusPickOSquidSpawnerChance, NexusPickUOreBlockChance, NexusPickUSquidSpawnerChance,
        NexusPickMegaOreBlockChance, NexusPickMegaSquidSpawnerChance, NexusAxeGlowSquidSpawnerChance, NexusAxeInfinitePouchChance,
        NexusAxeOGlowSquidSpawnerChance, NexusAxeOInfinitePouchChance, NexusAxeUGlowSquidSpawnerChance, NexusAxeUInfinitePouchChance,
        NexusAxeMegaGlowSquidSpawnerChance, NexusAxeMegaInfinitePouchChance, NexusShovelSpawnerChance, NexusShovelInfinitePouchChance,
        NexusShovelOSpawnerChance, NexusShovelOInfinitePouchChance, NexusShovelMegaSpawnerChance, NexusShovelMegaInfinitePouchChance;
    public static List<String> NexusPickSpeedRegionBlackList;
    public static String NexusShovelSpawnerMessage, NexusShovelInfinitePouchMessage, NexusShovelSpawnerType;

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

        NexusPick = ConfigUtils.initializeItem("NexusPick", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusPickOreBlockChance = ConfigUtils.setupConfig("NexusPick.OreBlockChance", config, 30, 1, List.of("Chance to obtain ore block of mined ore."));
        NexusPickSquidSpawnerChance = ConfigUtils.setupConfig("NexusPick.SquidSpawnerChance", config, 125000, 1, List.of("Chance to squid spawner."));
        NexusPickO = ConfigUtils.initializeItem("NexusPickO", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusPickOOreBlockChance = ConfigUtils.setupConfig("NexusPickO.OreBlockChance", config, 30, 1);
        NexusPickOSquidSpawnerChance = ConfigUtils.setupConfig("NexusPickO.SquidSpawnerChance", config, 125000, 1);
        NexusPickU = ConfigUtils.initializeItem("NexusPickU", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusPickUOreBlockChance = ConfigUtils.setupConfig("NexusPickU.OreBlockChance", config, 30, 1);
        NexusPickUSquidSpawnerChance = ConfigUtils.setupConfig("NexusPickU.SquidSpawnerChance", config, 125000, 1);
        NexusPickMega = ConfigUtils.initializeItem("NexusPickMega", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusPickMegaOreBlockChance = ConfigUtils.setupConfig("NexusPickMega.OreBlockChance", config, 20, 1);
        NexusPickMegaSquidSpawnerChance = ConfigUtils.setupConfig("NexusPickMega.SquidSpawnerChance", config, 90000, 1);
        NexusPickSpeedRegionBlackList = ConfigUtils.setupConfig("NexusPick.SpeedRegionBlacklist", config, List.of("spawn", "pvp", "pvpnop", "slayer", "pvpsafe"), List.of("Regions that speed isn't allowed in."));

        NexusAxe = ConfigUtils.initializeItem("NexusAxe", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusAxeGlowSquidSpawnerChance = ConfigUtils.setupConfig("NexusAxe.GlowSquidSpawnerChance", config, 10000, 1, List.of("Chance to find glow squid spawner"));
        NexusAxeInfinitePouchChance = ConfigUtils.setupConfig("NexusAxe.InfinitePouchChance", config, 15000, 1, List.of("Chance to find infinite pouch"));
        NexusAxeU = ConfigUtils.initializeItem("NexusAxeU", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusAxeUGlowSquidSpawnerChance = ConfigUtils.setupConfig("NexusAxeU.GlowSquidSpawnerChance", config, 10000, 1);
        NexusAxeUInfinitePouchChance = ConfigUtils.setupConfig("NexusAxeU.InfinitePouchChance", config, 15000, 1);
        NexusAxeO = ConfigUtils.initializeItem("NexusAxeO", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusAxeOGlowSquidSpawnerChance = ConfigUtils.setupConfig("NexusAxeO.GlowSquidSpawnerChance", config, 10000, 1);
        NexusAxeOInfinitePouchChance = ConfigUtils.setupConfig("NexusAxeO.InfinitePouchChance", config, 15000, 1);
        NexusAxeMega = ConfigUtils.initializeItem("NexusAxeMega", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusAxeMegaGlowSquidSpawnerChance = ConfigUtils.setupConfig("NexusAxeMega.GlowSquidSpawnerChance", config, 10000, 1);
        NexusAxeMegaInfinitePouchChance = ConfigUtils.setupConfig("NexusAxeMega.InfinitePouchChance", config, 15000, 1);

        NexusShovel = ConfigUtils.initializeItem("NexusShovel", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusShovelSpawnerChance = ConfigUtils.setupConfig("NexusShovel.SpawnerChance", config, 83000, 1);
        NexusShovelInfinitePouchChance = ConfigUtils.setupConfig("NexusShovel.InfinitePouchChance", config, 250000, 1);
        NexusShovelO = ConfigUtils.initializeItem("NexusShovelO", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusShovelOSpawnerChance = ConfigUtils.setupConfig("NexusShovelO.SpawnerChance", config, 83000, 1);
        NexusShovelOInfinitePouchChance = ConfigUtils.setupConfig("NexusShovelO.InfinitePouchChance", config, 250000, 1);
        NexusShovelMega = ConfigUtils.initializeItem("NexusShovelMega", config, LunarItems.keyRadius, LunarItems.keyDepth, LunarItems.keyLoreRadius);
        NexusShovelMegaSpawnerChance = ConfigUtils.setupConfig("NexusShovelMega.SpawnerChance", config, 83000, 1);
        NexusShovelMegaInfinitePouchChance = ConfigUtils.setupConfig("NexusShovelMega.InfinitePouchChance", config, 250000, 1);
        NexusShovelInfinitePouchMessage = ConfigUtils.setupConfig("NexusShovel.InfinitePouchMessage", config, "&7You received an infinite block pouch.");
        NexusShovelSpawnerMessage = ConfigUtils.setupConfig("NexusShovel.SpawnerMessage", config, "&7You received a Glow Squid Spawner.");
        NexusShovelSpawnerType = ConfigUtils.setupConfig("NexusShovel.SpawnerType", config, "GLOW_SQUID");

        config.options().copyDefaults(true);

        nexusItems.save();

    }
}
