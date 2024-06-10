package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class Config {
    private static FileConfiguration config;
    private static Logger logger;

    public static int AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance,
            AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance,
    AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance;
    public static String prefix;
    public static void setup(LunarItems plugin) {
        config = plugin.getConfig();
        logger = Bukkit.getLogger();

        AquaticHoeFarmKeyChance = setupConfig("AquaticHoe.Chances.FarmKey", 1000000, 1);
        AquaticHoeStackOfCropsChance = setupConfig("AquaticHoe.Chances.StackOfCrops", 2000, 1);
        AquaticHoeAxolotlSpawnEggChance = setupConfig("AquaticHoe.Chances.AxolotlSpawnEgg", 5000, 1);
        AquaticHoeFrogSpawnEggChance = setupConfig("AquaticHoe.Chances.FrogSpawnEgg", 5000, 1);
        AquaticHoeMegaFarmKeyChance = setupConfig("AquaticHoeMega.Chances.FarmKey", 1000000, 1);
        AquaticHoeMegaStackOfCropsChance = setupConfig("AquaticHoeMega.Chances.StackOfCrops", 2000, 1);
        AquaticHoeMegaAxolotlSpawnEggChance = setupConfig("AquaticHoeMega.Chances.AxolotlSpawnEgg", 5000, 1);
        AquaticHoeMegaFrogSpawnEggChance = setupConfig("AquaticHoeMega.Chances.FrogSpawnEgg", 5000, 1);
        AquaticHoe2FarmKeyChance = setupConfig("AquaticHoe2.Chances.FarmKey", 1000000, 1);
        AquaticHoe2StackOfCropsChance = setupConfig("AquaticHoe2.Chances.StackOfCrops", 2000, 1);
        AquaticHoe2AxolotlSpawnEggChance = setupConfig("AquaticHoe2.Chances.AxolotlSpawnEgg", 5000, 1);
        AquaticHoe2FrogSpawnEggChance = setupConfig("AquaticHoe2.Chances.FrogSpawnEgg", 5000, 1);
        prefix = setupConfig("Global.Prefix", "&b&lCHILLSMP");

        plugin.saveDefaultConfig();
    }

    private static int setupConfig(String path, int defaultValue, int minValue) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }

        String valueStr = config.getString(path);

        if (!valueStr.matches("-?\\d+(\\.\\d+)?")) {
            logger.warning("[CustomItems] " + path + " is not a valid number. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        int value = Integer.parseInt(valueStr);

        if (value < minValue) {
            logger.warning("[CustomItems] " + path + " is not a valid number. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        return value;
    }

    private static String setupConfig(String path, String defaultValue) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }
        return config.getString(path);
    }

}
