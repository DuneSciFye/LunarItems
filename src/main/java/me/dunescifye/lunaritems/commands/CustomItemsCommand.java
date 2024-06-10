package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.LiteralArgument;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;

public class CustomItemsCommand {

    public static void register() {
        new CommandTree("customitems")
                .then(new LiteralArgument("reload")
                        .executes((sender, args) -> {
                            Config.setup(LunarItems.getPlugin());
                            sender.sendMessage("Reloaded config!");
                        })
                )
                .withPermission("customitems.reload")
                .register();
    }

}
