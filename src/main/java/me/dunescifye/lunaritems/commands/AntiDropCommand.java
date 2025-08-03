package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AntiDropCommand {


  public static final NamespacedKey keyAntiDrop = new NamespacedKey("lunaritems", "anti-drop");

  public static void register() {

    MultiLiteralArgument functionArg = new MultiLiteralArgument("Function", "on", "off", "enable", "disable");

    new CommandAPICommand("antidrop")
      .withArguments(functionArg)
      .executesPlayer((p, args) -> {
        ItemStack item = p.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        switch (args.getByArgument(functionArg)) {
          case "enable", "on" -> pdc.set(keyAntiDrop, PersistentDataType.INTEGER, 1);
          case "disable", "off" -> {
            if (pdc.has(keyAntiDrop)) pdc.remove(keyAntiDrop);
          }
        }
        item.setItemMeta(meta);
      })
      .withPermission("lunaritems.commands.antidrop")
      .register();
  }

}
