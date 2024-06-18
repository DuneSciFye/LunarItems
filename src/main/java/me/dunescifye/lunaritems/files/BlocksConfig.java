package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;

public class BlocksConfig {

    public static ItemStack teleport_pad;

    public static void setup() {
        ConfigUtils blocksConfig = new ConfigUtils(getPlugin(), "items/blocks.yml");
        FileConfiguration config = blocksConfig.getConfig();

        if (!config.isSet("teleport_pad")){
            config.addDefault("teleport_pad.name", "&8[#ff032d&lTELEPORT PAD&8]");
            List<String> lore = Arrays.asList(
                "&7&l&m----------------",
                "&fPlace this teleport pad to fast travel between",
                "&ftwo points on the server. It comes with an A and a B",
                "&7&l&m----------------"
            );
            config.addDefault("teleport_pad.lore", lore);
            config.addDefault("teleport_pad.material", "OAK_PRESSURE_PLATE");
            List<String> hologram = Arrays.asList(
                "#ff032d&lTeleport Pad",
                "&f➥ Shift to teleport!"
            );
            config.addDefault("teleport_pad.hologram", hologram);
        }

        teleport_pad = ConfigUtils.initializeItem("teleport_pad", blocksConfig);

        config.options().copyDefaults(true);
        blocksConfig.save();

    }

}
