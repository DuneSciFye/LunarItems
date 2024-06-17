package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;
import static me.dunescifye.lunaritems.utils.ConfigUtils.setupConfig;

public class AquaticItemsConfig {

    public static ItemStack AquaticHoe;

    public static int AquaticHoeFarmKeyChance, AquaticHoeStackOfCropsChance, AquaticHoeAxolotlSpawnEggChance, AquaticHoeFrogSpawnEggChance,
        AquaticHoeMegaFarmKeyChance, AquaticHoeMegaStackOfCropsChance, AquaticHoeMegaAxolotlSpawnEggChance, AquaticHoeMegaFrogSpawnEggChance,
        AquaticHoe2FarmKeyChance, AquaticHoe2StackOfCropsChance, AquaticHoe2AxolotlSpawnEggChance, AquaticHoe2FrogSpawnEggChance;

    public static void setup() {
        ConfigUtils aquaticItems = new ConfigUtils(getPlugin(), "items/AquaticItems.yml");
        FileConfiguration config = aquaticItems.getConfig();

        if (!config.isSet("AquaticHoe")){
            config.addDefault("AquaticHoe.name", "&8[#11998e&lA#15a38c&lQ#1aac8a&lU#1eb688&lA#22bf86&lT#27c985&lI#2bd283&lC #2fdc81&lH#34e57f&lO#38ef7d&lE&8]");
            List<String> lore = Arrays.asList(
                "&7Unbreakable",
                "&7&l&m----------------",
                "&e⭐&fAuto replants crops, no seed required.",
                "&e⭐&fChance to give a stack of crop you harvest.",
                "&e⭐&fChance to find Axolotl / Frog Eggs.",
                "&e⭐&fChance to find Axolotl / Frog Spawners.",
                "&e⭐&fShift Right Click to place water.",
                "&7&l&m----------------",
                "&7Owner &8> &cNo owner.",
                "",
                "&7Run &e/modify owner &7to",
                "&7set and unset owner."
            );
            config.addDefault("AquaticHoe.lore", lore);
            config.addDefault("AquaticHoe.customModelData", 10038);
            config.addDefault("AquaticHoe.material", "NETHERITE_HOE");
            config.addDefault("AquaticHoe.unbreakable", true);
        }

        AquaticHoe = ConfigUtils.initializeItem("AquaticHoe", aquaticItems, LunarItems.keyUses);

        AquaticHoeFarmKeyChance = setupConfig("AquaticHoe.Chances.FarmKey", config, 1000000, 1);
        AquaticHoeStackOfCropsChance = setupConfig("AquaticHoe.Chances.StackOfCrops", config, 2000, 1);
        AquaticHoeAxolotlSpawnEggChance = setupConfig("AquaticHoe.Chances.AxolotlSpawnEgg", config, 5000, 1);
        AquaticHoeFrogSpawnEggChance = setupConfig("AquaticHoe.Chances.FrogSpawnEgg", config, 5000, 1);
        AquaticHoeMegaFarmKeyChance = setupConfig("AquaticHoeMega.Chances.FarmKey", config, 1000000, 1);
        AquaticHoeMegaStackOfCropsChance = setupConfig("AquaticHoeMega.Chances.StackOfCrops", config, 2000, 1);
        AquaticHoeMegaAxolotlSpawnEggChance = setupConfig("AquaticHoeMega.Chances.AxolotlSpawnEgg", config, 5000, 1);
        AquaticHoeMegaFrogSpawnEggChance = setupConfig("AquaticHoeMega.Chances.FrogSpawnEgg", config, 5000, 1);
        AquaticHoe2FarmKeyChance = setupConfig("AquaticHoe2.Chances.FarmKey", config, 1000000, 1);
        AquaticHoe2StackOfCropsChance = setupConfig("AquaticHoe2.Chances.StackOfCrops", config, 2000, 1);
        AquaticHoe2AxolotlSpawnEggChance = setupConfig("AquaticHoe2.Chances.AxolotlSpawnEgg", config, 5000, 1);
        AquaticHoe2FrogSpawnEggChance = setupConfig("AquaticHoe2.Chances.FrogSpawnEgg", config, 5000, 1);

        config.options().copyDefaults(true);

        aquaticItems.save();

    }


}
