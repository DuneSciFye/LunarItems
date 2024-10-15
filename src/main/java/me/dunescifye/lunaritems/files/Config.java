package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class Config {

    public static String prefix, cooldownMessageHours, cooldownMessageMinutes, cooldownMessageSeconds, invalidTargetLocation,
        teleportPadPlaceFirstMessage, teleportPadPlaceSecondMessage, changeVariableMessage, spawnerCommand, infinitePouchCommand, receiveItemMessage,
        cannotHatMessage;
    public static List<String> radiusMiningDisabledWorlds;
    public static void setup(LunarItems plugin) {
        FileConfiguration config = plugin.getConfig();

        prefix = ConfigUtils.setupConfig("Global.Prefix", config, "&b&lSURVIVAL &8&l▶ ");
        cooldownMessageHours = ConfigUtils.setupConfig("Messages.CooldownMessage.Hours", config, "&7You can use this again in &a%hours% Hours, %minutes% Minutes, & %seconds% Seconds&7.");
        cooldownMessageMinutes = ConfigUtils.setupConfig("Messages.CooldownMessage.Minutes", config, "&7You can use this again in &a%minutes% Minutes & %seconds% Seconds&7.");
        cooldownMessageSeconds = ConfigUtils.setupConfig("Messages.CooldownMessage.Seconds", config, "&7You can use this again in &a%seconds% Seconds&7.");
        invalidTargetLocation = ConfigUtils.setupConfig("Messages.Blocks.InvalidTargetLocation", config, "&cTarget location not found!");
        teleportPadPlaceFirstMessage = ConfigUtils.setupConfig("Messages.Blocks.TeleportPad.PlaceFirst", config, "&aPlace down another teleport pad to link them!");
        teleportPadPlaceSecondMessage = ConfigUtils.setupConfig("Messages.Blocks.TeleportPad.PlaceSecond", config, "&aLinked this teleport pad to the one at %x% %y% %z%!");
        changeVariableMessage = ConfigUtils.setupConfig("Messages.ChangeVariableMessage", config, "&aSet %variable% to %content%!", List.of("Message to send when cycling through item settings."));
        spawnerCommand = ConfigUtils.setupConfig("Commands.SpawnerCommand", config, "ss give %player% %type% %amount%", List.of("Command used to give spawners. %player% for player name, %type% for spawner type, %amount% for amount."));
        infinitePouchCommand = ConfigUtils.setupConfig("Commands.InfinitePouchCommand", config, "superpouches:pouches give %player% %type% 1 1", List.of("Command used for infinite pouches. Use %player% for player name and %type% for pouch id"));
        receiveItemMessage = ConfigUtils.setupConfig("Messages.ReceiveItemMessage", config, "&7You received %item%.", List.of("Message when player receives item from drop. For example spawners, keys, spawn eggs, etc. Typically in format of '1x Parrot Spawner'"));
        radiusMiningDisabledWorlds = ConfigUtils.setupConfig("RadiusMiningDisabledWorlds", config, List.of("pvp"), List.of("Worlds that radius mining isn't allowed in."));
        cannotHatMessage = ConfigUtils.setupConfig("Messages.CannotHat", config, "&c&lYou cannot run /hat with your current helmet on!");


        plugin.saveConfig();
    }


}
