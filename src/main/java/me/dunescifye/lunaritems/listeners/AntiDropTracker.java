package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
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
import java.util.UUID;

import static me.dunescifye.lunaritems.files.Config.prefix;

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
        if (!pdc.has(LunarItems.keyEIID) && !pdc.has(LunarItems.keyInfiniteBlock) && item.getType() != Material.BLAZE_ROD) return;

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
        e.setCancelled(true);
    }

    private record DropData(ItemStack item, int count, Instant time) {
    }

}
