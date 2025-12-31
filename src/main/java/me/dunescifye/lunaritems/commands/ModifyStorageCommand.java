package me.dunescifye.lunaritems.commands;

import dev.jorel.commandapi.CommandAPICommand;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ModifyStorageCommand {

    public static final NamespacedKey keyDropOres = new NamespacedKey("lunaritems", "drop-ores");

    public static void register() {
        new CommandAPICommand("modify")
            .withSubcommand(new CommandAPICommand("storage")
                .executesPlayer((p, args) -> {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    ItemMeta meta = item.getItemMeta();
                    if (meta == null) {
                        return;
                    }
                    PersistentDataContainer pdc = meta.getPersistentDataContainer();

                    // Check if holding a jollypick or jollypickm
                    String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                    if (itemID == null || (!itemID.contains("jollypick"))) {
                        return;
                    }

                    // Toggle the drop-ores setting
                    if (pdc.has(keyDropOres, PersistentDataType.INTEGER)) {
                        pdc.remove(keyDropOres);
                    } else {
                        pdc.set(keyDropOres, PersistentDataType.INTEGER, 1);
                    }
                    item.setItemMeta(meta);
                })
                .withPermission("lunaritems.commands.modify.storage")
            )
            .register();
    }
}
