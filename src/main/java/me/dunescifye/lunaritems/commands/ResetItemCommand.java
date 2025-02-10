package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ResetItemCommand {

    private static final Component unbreakableLine = Component.text("").append(Component.text("Unbreakable", NamedTextColor.GRAY).decoration(TextDecoration.OBFUSCATED, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false).decoration(TextDecoration.ITALIC, false));
    private static final Component megaLine = Component.text("").append(Component.text("Mega Variant", NamedTextColor.GRAY).decoration(TextDecoration.OBFUSCATED, false).decoration(TextDecoration.BOLD, false).decoration(TextDecoration.STRIKETHROUGH, false).decoration(TextDecoration.UNDERLINED, false).decoration(TextDecoration.ITALIC, false));

    public static void register() {

        PlayerArgument playerArg = new PlayerArgument("Player");
        StringArgument slotArg = new StringArgument("Slot");
        IntegerArgument loreLinesArg = new IntegerArgument("Lore Lines");

        new CommandAPICommand("resetitem")
            .withArguments(new MultiLiteralArgument("Function", "lore", "attributes"))
            .withOptionalArguments(playerArg.combineWith(slotArg.replaceSuggestions(ArgumentSuggestions.strings(Utils.getItemSlots()))).withPermission("lunaritems.commands.resetitemadmin"))
            .withOptionalArguments(loreLinesArg)
            .executesPlayer((p, args) -> {
                ItemStack item = p.getInventory().getItemInMainHand();
                updateLore(p, item, args.getByClass("Function", String.class).toUpperCase(), "main", null);
            })
            .executes((sender, args) -> {
                Player p = args.getByArgument(playerArg);
                if (p == null) return;
                String slot = args.getByArgument(slotArg);
                ItemStack item = Utils.getInvItem(p, slot);
                if (item == null) return;
                updateLore(p, item, args.getByClass("Function", String.class).toUpperCase(), slot, args.getByArgumentOrDefault(loreLinesArg, null));
            })
            .withPermission("lunaritems.command.resetlore")
            .register();

    }



    private static void updateLore(Player p, ItemStack item, String function, String slot, Integer endLine) {
        if (!item.hasItemMeta()) return;
        String itemID = item.getItemMeta().getPersistentDataContainer().get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;
        List<Component> oldLore = item.lore();
        if (oldLore == null) return;
        int startLine = item.getType() == Material.FISHING_ROD ? oldLore.contains(megaLine) ? oldLore.indexOf(megaLine) : oldLore.indexOf(unbreakableLine) : 0; // Keep Augments if Fishing Rod
        Utils.runConsoleCommands("ei refresh " + p.getName() + " " + itemID + " " + function);
        ItemStack newItem = Utils.getInvItem(p, slot);
        if (newItem == null) return;
        List<Component> newLore = newItem.lore();
        if (newLore == null) return;
        if (endLine == null) for (int i = newLore.size() + startLine; i < oldLore.size(); i++) // Add extra lore to New Lore
            newLore.add(oldLore.get(i));
        else for (int i = endLine + startLine; i < oldLore.size(); i++) // Add extra lore to New Lore with changed End line
            newLore.add(oldLore.get(i));
        for (int i = 0; i < startLine; i ++) // Add Augments to New Lore
            newLore.add(i, oldLore.get(i));
        newItem.lore(newLore);
    }

}