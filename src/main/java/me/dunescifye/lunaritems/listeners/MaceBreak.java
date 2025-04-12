package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static me.dunescifye.lunaritems.files.Config.prefix;

public class MaceBreak implements Listener {

    public static boolean enabled = true;
    public static String message = "&7Your mace broke on death! Repair it to reuse it!";

    NamespacedKey statusKey = new NamespacedKey("score", "score-status");

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getPlayer();

        ArrayList<ItemStack> items = new ArrayList<>(Arrays.asList(p.getInventory().getContents()));
        items.addFirst(p.getItemOnCursor());

        for (ItemStack item : items) {
            if (item == null || !item.hasItemMeta()) continue;
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();

            if (pdc.has(LunarItems.keyEIID, PersistentDataType.STRING) &&
                item.getType() == Material.MACE &&
                p.getWorld().getName().equals("pvp") &&
                Objects.equals(pdc.get(statusKey, PersistentDataType.STRING), "&aWorking")) {

                pdc.set(statusKey, PersistentDataType.STRING, "&cBroken");

                ((Damageable) meta).setDamage(498);

                meta.lore(Utils.updateLore(item, "&aWorking", "&cBroken"));

                item.setItemMeta(meta);

                p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message));
            }
        }
    }

}
