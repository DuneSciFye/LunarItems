package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.logging.Logger;

public class ConfigUtils {

    private File file;
    private FileConfiguration config;

    public ConfigUtils(Plugin plugin, String path){
        this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
    }

    public ConfigUtils(String path){
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean save(){
        try {
            this.config.save(this.file);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(){
        return this.file;
    }

    public FileConfiguration getConfig(){
        return this.config;
    }


    public static int setupConfig(String path, FileConfiguration config, int defaultValue, int minValue) {
        Logger logger = Bukkit.getLogger();
        if (!config.isSet(path)) {
            config.addDefault(path, defaultValue);
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

    public static String setupConfig(String path, FileConfiguration config, String defaultValue) {
        if (!config.isSet(path)) {
            config.addDefault(path, defaultValue);
            return defaultValue;
        }
        return config.getString(path);
    }

}
