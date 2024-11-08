package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ResetItemCommand {

    public static void register() {
        new CommandAPICommand("resetitem")
            .withArguments(new MultiLiteralArgument("Function", "lore", "attributes"))
            .executesPlayer((player, args) -> {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (!item.hasItemMeta()) return;
                String itemID = item.getItemMeta().getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
                if (itemID == null) return;
                Utils.runConsoleCommands("ei refresh " + player.getName() + " " + itemID + " " + args.getByClass("Function", String.class).toUpperCase());
                /*
                Set<NamespacedKey> keys = container.getKeys();
                for (NamespacedKey key : keys) {
                    if (key.equals(LunarItems.keyEIID) || !key.value().startsWith("score-")) continue;
                    if (container.has(key, PersistentDataType.STRING)) {
                        String currentValue = container.get(key, PersistentDataType.STRING);
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " temp");
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " " + currentValue);
                    } else if (container.has(key, PersistentDataType.DOUBLE)) {
                        double currentValue = container.get(key, PersistentDataType.DOUBLE);
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " 432.324");
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " " + currentValue);
                    } else if (container.has(key, PersistentDataType.INTEGER)) {
                        int currentValue = container.get(key, PersistentDataType.INTEGER);
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " 3892798");
                        Utils.runConsoleCommands("ei console-modification set variable " + player.getName() + " " + player.getInventory().getHeldItemSlot() + " " + key.value().substring(6) + " " + currentValue);
                    }
                }

                 */
            })
            .withPermission("lunaritems.command.resetlore")
            .register();
    }

}