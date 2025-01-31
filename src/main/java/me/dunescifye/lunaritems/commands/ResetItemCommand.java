package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ResetItemCommand {

    public static void register() {

        PlayerArgument playerArg = new PlayerArgument("Player");
        StringArgument slotArg = new StringArgument("Slot");
        new CommandAPICommand("resetitem")
            .withArguments(new MultiLiteralArgument("Function", "lore", "attributes"))
            .withOptionalArguments(playerArg.combineWith(slotArg.replaceSuggestions(ArgumentSuggestions.strings(Utils.getItemSlots()))).withPermission("lunaritems.commands.resetitemadmin"))
            .executesPlayer((p, args) -> {
                ItemStack item = p.getInventory().getItemInMainHand();
                updateLore(p, item, args.getByClass("Function", String.class).toUpperCase(), "main");
            })
            .executes((sender, args) -> {
                Player p = args.getByArgument(playerArg);
                if (p == null) return;
                String slot = args.getByArgument(slotArg);
                ItemStack item = Utils.getInvItem(p, slot);
                if (item == null) return;
                updateLore(p, item, args.getByClass("Function", String.class).toUpperCase(), slot);
            })
            .withPermission("lunaritems.command.resetlore")
            .register();

    }



    private static void updateLore(Player p, ItemStack item, String function, String slot) {
        if (!item.hasItemMeta()) return;
        String itemID = item.getItemMeta().getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;
        List<Component> oldLore = item.lore();
        Utils.runConsoleCommands("ei refresh " + p.getName() + " " + itemID + " " + function);
        ItemStack newItem = Utils.getInvItem(p, slot);
        if (newItem == null) return;
        List<Component> newLore = newItem.lore();
        if (oldLore == null || newLore == null) return;
        for (int i = newLore.size(); i < oldLore.size(); i++)
            newLore.add(oldLore.get(i));
        newItem.lore(newLore);
    }

}