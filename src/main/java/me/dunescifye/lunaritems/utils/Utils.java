package me.dunescifye.lunaritems.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;

public class Utils {

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    public static Material getCropDrops(Block crop) {
        if (crop.getType() == Material.WHEAT) return Material.WHEAT;
        return crop.getType();
    }

}
