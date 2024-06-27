package me.dunescifye.lunaritems.commands;

import com.jeff_media.morepersistentdatatypes.DataType;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.concurrent.ThreadLocalRandom;

public class SpawnNoDamageFireworkCommand {

    public static void register() {
        new CommandAPICommand("spawnnodamagefirework")
            .withArguments(new LocationArgument("Location"))
            .withOptionalArguments(new PlayerArgument("No Damage Player"))
            .executes((sender, args) -> {
                Location loc = (Location) args.get("Location");
                Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();

                fwm.addEffect(FireworkEffect.builder().withColor(Color.fromRGB(ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256), ThreadLocalRandom.current().nextInt(0, 256))).build());

                fw.setFireworkMeta(fwm);
                fw.setMetadata("nodamage", new FixedMetadataValue(LunarItems.getPlugin(), true));

                Player noDamagePlayer = (Player) args.get("No Damage Player");

                if (noDamagePlayer != null) {
                    PersistentDataContainer container = fw.getPersistentDataContainer();
                    container.set(LunarItems.keyNoDamagePlayer, DataType.PLAYER, noDamagePlayer);
                }

                fw.detonate();
            })
            .withPermission("lunaritems.command.spawnnodamagefirework")
            .register();
    }

}
