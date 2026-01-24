package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.commands.AntiDropCommand;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.dunescifye.lunaritems.files.Config.prefix;
import static me.dunescifye.lunaritems.utils.Overflow.addOverflow;

public class AntiDropTracker implements Listener {
    public static boolean enabled;
    private final HashMap<UUID, DropData> drops = new HashMap<>();
    public static int requiredCount;
    public static Duration duration = Duration.ofSeconds(2);
    public static String message;

    public void registerEvents(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrop(PlayerDropItemEvent e) {
        ItemStack item = e.getItemDrop().getItemStack();
        if (!item.hasItemMeta()) return;
        PersistentDataContainer pdc = item.getItemMeta().getPersistentDataContainer();
        Material mat = item.getType();
        String matString = mat.toString();
        if (!pdc.has(AntiDropCommand.keyAntiDrop) && !pdc.has(LunarItems.keyEIID) && !pdc.has(LunarItems.keyInfiniteBlock) && !pdc.has(LunarItems.keyCosmosEnchant)
          && mat != Material.BLAZE_ROD && mat != Material.TRIPWIRE_HOOK && !matString.contains("SHULKER_BOX")) return;

        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        DropData drop = drops.getOrDefault(uuid, new DropData(item, 1, Instant.now()));
        int count = drop.count;
        if (drop.item.equals(item) && Instant.now().isBefore(drop.time.plus(duration))) {
            if (drop.count >= requiredCount) {
                drops.remove(uuid);
                return;
            }
        } else count = 1;
        drops.put(uuid, new DropData(item, count + 1, Instant.now()));
        p.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(prefix + message.replace("%amount%", String.valueOf(requiredCount - count))));
        // Restore item to offhand if it was dropped from there
        if (p.getInventory().getItemInOffHand().getType() == Material.AIR) {
            p.getInventory().setItemInOffHand(item.clone());
        }
        e.getItemDrop().remove();
        Map<Integer, ItemStack> excess = p.getInventory().addItem(item);
        addOverflow(p, excess);
    }

    private record DropData(ItemStack item, int count, Instant time) {
    }

}
