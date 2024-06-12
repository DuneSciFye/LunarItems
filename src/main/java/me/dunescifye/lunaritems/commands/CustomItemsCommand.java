package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;
import org.bukkit.entity.Player;

import static me.dunescifye.lunaritems.files.Config.allItems;

public class CustomItemsCommand {

    public static void register() {
        new CommandTree("customitems")
            .then(new LiteralArgument("reload")
                .executes((sender, args) -> {
                    Config.setup(LunarItems.getPlugin());
                    sender.sendMessage("Reloaded config!");
                })
            )
            .then(new LiteralArgument("give")
                .then(new PlayerArgument("Player")
                    .then(new StringArgument("Item ID")
                        .executes((sender, args) -> {
                            Player p = (Player) args.get("Player");
                            p.getInventory().addItem(allItems.get(args.get("Item ID")));
                        })
                    )
                )
            )
            .withPermission("customitems.reload")
            .register();
    }

}
