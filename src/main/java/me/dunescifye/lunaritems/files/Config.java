package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.dunescifye.lunaritems.utils.ConfigUtils.setupConfig;

public class Config {
    private static FileConfiguration config;

    public static Map<String, ItemStack> allItems = new HashMap<>();

    public static String prefix, cooldownMessageHours, cooldownMessageMinutes, cooldownMessageSeconds;
    public static void setup(LunarItems plugin) {
        config = plugin.getConfig();

        prefix = setupConfig("Global.Prefix", config, "&b&lCHILLSMP &8&l▶ ");
        cooldownMessageHours = setupConfig("Messages.CooldownMessage.Hours", config, "&7You can use this again in &a%hours% Hours, %minutes% Minutes, & %seconds% Seconds&7.");
        cooldownMessageMinutes = setupConfig("Messages.CooldownMessage.Minutes", config, "&7You can use this again in &a%minutes% Minutes & %seconds% Seconds&7.");
        cooldownMessageSeconds = setupConfig("Messages.CooldownMessage.Seconds", config, "&7You can use this again in &a%seconds% Seconds&7.");

        plugin.saveConfig();
    }


}
