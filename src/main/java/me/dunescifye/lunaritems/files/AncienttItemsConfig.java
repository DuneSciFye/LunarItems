package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;

public class AncienttItemsConfig {

    public static int AncienttHoeParrotSpawnEggChance, AncienttHoeFireworkChance, AncienttHoeFarmKeyChance, AncienttHoeParrotSpawnerChance, AncienttHoeInfiniteSeedPouchChance,
        AncienttHoeMegaParrotSpawnEggChance, AncienttHoeMegaFireworkChance, AncienttHoeMegaFarmKeyChance, AncienttHoeMegaParrotSpawnerChance, AncienttHoeMegaInfiniteSeedPouchChance;
    public static String AncienttHoeInfiniteSeedPouchID;

    public static void setup() {
        ConfigUtils ancienttItems = new ConfigUtils(getPlugin(), "items/AncienttItems.yml");
        FileConfiguration config = ancienttItems.getConfig();

        AncienttHoeParrotSpawnEggChance = ConfigUtils.setupConfig("AncienttHoe.ParrotSpawnEggChance", config, 8000, 1);
        AncienttHoeFireworkChance = ConfigUtils.setupConfig("AncienttHoe.FireworkChance", config, 3000, 1);
        AncienttHoeFarmKeyChance = ConfigUtils.setupConfig("AncienttHoe.FarmKeyChance", config, 50000, 1);
        AncienttHoeInfiniteSeedPouchChance = ConfigUtils.setupConfig("AncienttHoe.InfiniteSeedPouchSeed", config, 50000, 1);
        AncienttHoeParrotSpawnerChance = ConfigUtils.setupConfig("AncienttHoe.ParrotSpawnerChance", config, 30000, 1);
        AncienttHoeMegaParrotSpawnEggChance = ConfigUtils.setupConfig("AncienttHoeMega.ParrotSpawnEggChance", config, 6000, 1);
        AncienttHoeMegaFireworkChance = ConfigUtils.setupConfig("AncienttHoeMega.FireworkChance", config, 2000, 1);
        AncienttHoeMegaFarmKeyChance = ConfigUtils.setupConfig("AncienttHoeMega.FarmKeyChance", config, 40000, 1);
        AncienttHoeMegaParrotSpawnerChance = ConfigUtils.setupConfig("AncienttHoeMega.ParrotSpawnerChance", config, 40000, 1);
        AncienttHoeMegaInfiniteSeedPouchChance = ConfigUtils.setupConfig("AncienttHoeMega.InfiniteSeedPouchSeed", config, 25000, 1);
        AncienttHoeInfiniteSeedPouchID = ConfigUtils.setupConfig("AncienttHoe.InfiniteSeedPouchID", config, "inffarm");

        ancienttItems.save();
    }

}
