package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class UpdateLoreCommand {

    public static void register() {
        new CommandAPICommand("updatelore")
            .withArguments(new PlayerArgument("Player"))
            .withArguments(new IntegerArgument("Item Slot"))
            .withArguments(new TextArgument("Old Text"))
            .withArguments(new TextArgument("New Text"))
            .executes((sender, args) -> {
                Player p = (Player) args.get("Player");
                ItemStack item = p.getInventory().getItem((Integer) args.get("Item Slot"));
                if (item == null)
                    return;

                if (!item.hasItemMeta()) return;
                ItemMeta meta = item.getItemMeta();
                meta.lore(Utils.updateLore(item, (String) args.get("Old Text"), (String) args.get("New Text")));
                item.setItemMeta(meta);

            })
            .withPermission("lunaritems.command.updatekey")
            .register();
    }
}
