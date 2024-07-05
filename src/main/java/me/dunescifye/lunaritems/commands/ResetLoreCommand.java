package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Set;

public class ResetLoreCommand {

    public static void register() {
        new CommandAPICommand("resetlore")
            .executesPlayer((player, args) -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!item.hasItemMeta()) return;
                PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
                if (itemID == null) return;

                Set<NamespacedKey> keys = container.getKeys();

                for (NamespacedKey key : keys) {
                    if (key.equals(LunarItems.keyEIID)) continue;
                    try {
                        String currentValue = container.get(key, PersistentDataType.STRING);
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " temp");
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " " + currentValue);
                        /*
                        Bukkit.getScheduler().runTask(LunarItems.getPlugin(), () -> {
                            ItemMeta meta = item.getItemMeta();
                            meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, currentValue);
                            item.setItemMeta(meta);
                        });
                         */
                        break;
                    }
                    catch (IllegalArgumentException e) {
                        double currentValue = container.get(key, PersistentDataType.DOUBLE);
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " 432.324");
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " " + currentValue);
                        /*
                        Bukkit.getScheduler().runTask(LunarItems.getPlugin(), () -> {
                            ItemMeta meta = item.getItemMeta();
                            meta.getPersistentDataContainer().set(key, PersistentDataType.DOUBLE, currentValue);
                            item.setItemMeta(meta);
                        });

                         */
                        break;
                    }
                }
            })
            .withPermission("lunaritems.command.resetlore")
            .register();
    }

}