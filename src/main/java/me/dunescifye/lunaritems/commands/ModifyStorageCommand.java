package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static me.dunescifye.lunaritems.files.Config.prefix;

public class ModifyStorageCommand {

    public static final NamespacedKey keyDropOres = new NamespacedKey("lunaritems", "drop-ores");

    public static void register() {
        new CommandAPICommand("modify")
            .withSubcommand(new CommandAPICommand("storage")
                .executesPlayer((p, args) -> {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) {
                        p.sendMessage(prefix + "You must be holding an item!");
                        return;
                    }
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();

                    // Check if holding a jollypick or jollypickm
                    String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                    if (itemID == null || (!itemID.contains("jollypick"))) {
                        p.sendMessage(prefix + "You must be holding a Jolly Pickaxe!");
                        return;
                    }

                    // Toggle the drop-ores setting
                    if (pdc.has(keyDropOres, PersistentDataType.INTEGER)) {
                        pdc.remove(keyDropOres);
                        p.sendMessage(prefix + "Storage mode enabled. Ores will be added to your balance.");
                    } else {
                        pdc.set(keyDropOres, PersistentDataType.INTEGER, 1);
                        p.sendMessage(prefix + "Storage mode disabled. Ores will drop on the ground.");
                    }
                    item.setItemMeta(meta);
                })
                .withPermission("lunaritems.commands.modify.storage")
            )
            .register();
    }
}
