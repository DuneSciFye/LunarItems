package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandTree;
import dev.jorel.commandapi.arguments.*;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.files.NexusItemsConfig;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomItemsCommand {

    public static void register() {
        new CommandTree("customitems")
            .then(new LiteralArgument("reload")
                .executes((sender, args) -> {
                    Config.setup(LunarItems.getPlugin());
                    NexusItemsConfig.setup();
                    BlocksConfig.setup();
                    sender.sendMessage("Reloaded config!");
                })
            )
            .then(new LiteralArgument("give")
                .then(new EntitySelectorArgument.OnePlayer("Player")
                    .then(new StringArgument("Item ID")
                        .replaceSuggestions(ArgumentSuggestions.strings(
                            LunarItems.items.keySet()
                        ))
                        .executes((sender, args) -> {
                            Player p = (Player) args.get("Player");
                            String key = (String) args.get("Item ID");
                            if (LunarItems.items.containsKey(key)) {
                                Utils.dropItems(p.getLocation(), p.getUniqueId(), LunarItems.items.get(key));
                            }
                            else {
                                sender.sendMessage("Item not found!");
                            }
                        })
                        .then(new IntegerArgument("Amount", 1)
                            .executes((sender, args) -> {
                                Player p = (Player) args.get("Player");
                                String key = (String) args.get("Item ID");
                                int amount = (int) args.get("Amount");
                                if (LunarItems.items.containsKey(key)) {
                                    ItemStack item = LunarItems.items.get(key).clone();
                                    item.setAmount(amount);
                                    Utils.dropItems(p.getLocation(), p.getUniqueId(), item);
                                }
                                else {
                                    sender.sendMessage("Item not found!");
                                }
                            })
                        )
                    )
                )
            )
            .withPermission("customitems.reload")
            .register();
    }

}
