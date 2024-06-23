package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {
    private static FileConfiguration config;

    public static String prefix, cooldownMessageHours, cooldownMessageMinutes, cooldownMessageSeconds, invalidTargetLocation,
        teleportPadPlaceFirstMessage, teleportPadPlaceSecondMessage, changeVariableMessage, spawnerCommand;
    public static void setup(LunarItems plugin) {
        config = plugin.getConfig();

        prefix = ConfigUtils.setupConfig("Global.Prefix", config, "&b&lSURVIVAL &8&l▶ ");
        cooldownMessageHours = ConfigUtils.setupConfig("Messages.CooldownMessage.Hours", config, "&7You can use this again in &a%hours% Hours, %minutes% Minutes, & %seconds% Seconds&7.");
        cooldownMessageMinutes = ConfigUtils.setupConfig("Messages.CooldownMessage.Minutes", config, "&7You can use this again in &a%minutes% Minutes & %seconds% Seconds&7.");
        cooldownMessageSeconds = ConfigUtils.setupConfig("Messages.CooldownMessage.Seconds", config, "&7You can use this again in &a%seconds% Seconds&7.");
        invalidTargetLocation = ConfigUtils.setupConfig("Messages.Blocks.InvalidTargetLocation", config, "&cTarget location not found!");
        teleportPadPlaceFirstMessage = ConfigUtils.setupConfig("Messages.Blocks.TeleportPad.PlaceFirst", config, "&aPlace down another teleport pad to link them!");
        teleportPadPlaceSecondMessage = ConfigUtils.setupConfig("Messages.Blocks.TeleportPad.PlaceSecond", config, "&aLinked this teleport pad to the one at %x% %y% %z%!");
        changeVariableMessage = ConfigUtils.setupConfig("Messages.ChangeVariableMessage", config, "&aSet %variable% to %content%!", List.of("Message to send when cycling through item settings."));
        spawnerCommand = ConfigUtils.setupConfig("Commands.SpawnerCommand", config, "ss give %player% %type% %amount%", List.of("Command used to give spawners. %player% for player name, %type% for spawner type, %amount% for amount."));


        plugin.saveConfig();
    }


}
