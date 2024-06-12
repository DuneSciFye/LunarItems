package me.dunescifye.lunaritems.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.dunescifye.lunaritems.files.Config.*;

public class CooldownManager {

    public static final Map<UUID, Instant> nexusHoeCooldowns = new HashMap<>();

    // Set cooldown
    public static void setCooldown(Map<UUID, Instant> map, UUID key, Duration duration) {
        map.put(key, Instant.now().plus(duration));
    }

    // Check if cooldown has expired
    public static boolean hasCooldown(Map<UUID, Instant> map, UUID key) {
        Instant cooldown = map.get(key);
        return cooldown != null && Instant.now().isBefore(cooldown);
    }

    // Remove cooldown
    public static Instant removeCooldown(Map<UUID, Instant> map, UUID key) {
        return map.remove(key);
    }

    // Get remaining cooldown time
    public static Duration getRemainingCooldown(Map<UUID, Instant> map, UUID key) {
        Instant cooldown = map.get(key);
        Instant now = Instant.now();
        if (cooldown != null && now.isBefore(cooldown)) {
            return Duration.between(now, cooldown);
        } else {
            return Duration.ZERO;
        }
    }

    public static void sendCooldownMessage(Player player, Duration duration){
        String message;

        if (duration.compareTo(Duration.ofHours(1)) > 0){
            message = cooldownMessageHours.replace("%hours%", String.valueOf(duration.toHoursPart()))
                .replace("%minutes%", String.valueOf(duration.toMinutesPart()))
                .replace("%seconds%", String.valueOf(duration.toSecondsPart()));
        } else if (duration.compareTo(Duration.ofMinutes(1)) > 0) {
            message = cooldownMessageMinutes.replace("%minutes%", String.valueOf(duration.toMinutesPart()))
                .replace("%seconds%", String.valueOf(duration.toSecondsPart()));
        } else {
            message = cooldownMessageSeconds.replace("%seconds%", String.valueOf(duration.toSecondsPart()));
        }

        player.sendMessage(prefix + LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholders(player, message)));

    }
}
