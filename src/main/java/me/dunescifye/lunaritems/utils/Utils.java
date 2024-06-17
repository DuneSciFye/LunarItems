package me.dunescifye.lunaritems.utils;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Utils {

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    //Drop item at player
    public static void dropItems(Location location, ItemStack... items) {
        for (ItemStack item : items) {
            Item drop = location.getWorld().dropItemNaturally(location, item);
            drop.setPickupDelay(0);
        }
    }

    //Drop item with owner
    public static void dropItems(Location location, UUID uuid, ItemStack... items) {
        for (ItemStack item : items) {
            Item drop = location.getWorld().dropItemNaturally(location, item);
            drop.setPickupDelay(0);
            drop.setOwner(uuid);
        }
    }
}
