package me.dunescifye.lunaritems;

import com.jeff_media.customblockdata.CustomBlockData;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import me.dunescifye.lunaritems.commands.CustomItemsCommand;
import me.dunescifye.lunaritems.commands.ResetItemCommand;
import me.dunescifye.lunaritems.commands.UpdateLoreCommand;
import me.dunescifye.lunaritems.commands.WallOfFireCommand;
import me.dunescifye.lunaritems.files.*;
import me.dunescifye.lunaritems.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class LunarItems extends JavaPlugin {
    private static LunarItems plugin;

    public static final NamespacedKey keyEIID = new NamespacedKey("executableitems", "ei-id");
    public static final NamespacedKey keyLocation = new NamespacedKey("lunaritems", "location");
    public static final NamespacedKey keyUses = new NamespacedKey("score", "score-uses");
    public static final NamespacedKey keyUUID = new NamespacedKey("lunaritems", "uuid");
    public static final NamespacedKey keyRadius = new NamespacedKey("score", "score-radius");
    public static final NamespacedKey keyDepth = new NamespacedKey("score", "score-depth");
    public static final NamespacedKey keyDrop = new NamespacedKey("score", "score-drop");
    public static final NamespacedKey keyLoreDrop = new NamespacedKey("score", "score-loredrop");
    public static final NamespacedKey keyLoreRadius = new NamespacedKey("score", "score-loreradius");
    public static final NamespacedKey keyBlocksBroken = new NamespacedKey("score", "score-blocksharvested");

    public static final NamespacedKey keyAutoPickup = new NamespacedKey("score", "score-autopickup");
    public static final NamespacedKey autoPickupKey = new NamespacedKey("commandutils", "autopickup");

    public static Map<String, ItemStack> items = new HashMap<>();
    public static Map<NamespacedKey, PersistentDataType> dataType = new HashMap<>();
    public static Map<NamespacedKey, Object> defaultValue = new HashMap<>();

    static {
        dataType.put(keyUses, PersistentDataType.DOUBLE);
        defaultValue.put(keyUses, 0.0);
        dataType.put(keyUUID, PersistentDataType.STRING);
        defaultValue.put(keyUUID, "");
        dataType.put(keyRadius, PersistentDataType.DOUBLE);
        dataType.put(keyDepth, PersistentDataType.DOUBLE);
        dataType.put(keyDrop, PersistentDataType.STRING);
        dataType.put(keyLoreDrop, PersistentDataType.STRING);
        dataType.put(keyLoreRadius, PersistentDataType.STRING);
        defaultValue.put(keyRadius, 0.0);
        defaultValue.put(keyDepth, 1.0);
        defaultValue.put(keyDrop, "");
        defaultValue.put(keyLoreDrop, "Default");
        defaultValue.put(keyLoreRadius, "1x1");
    }
    public static boolean griefPreventionEnabled, decentHologramsEnabled, factionsUUIDEnabled;

    public static LunarItems getPlugin() {
        return plugin;
    }

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
    }

    @Override
    public void onEnable() {
        Logger logger = this.getLogger();

        plugin = this;
        CommandAPI.onEnable();
        Config.setup(this);
        AquaticItemsConfig.setup();
        NexusItemsConfig.setup();
        BlocksConfig.setup();
        AncienttItemsConfig.setup();
        registerEvents();
        registerCommands();
        CustomBlockData.registerListener(plugin);

        if (Bukkit.getPluginManager().isPluginEnabled("GriefPrevention")) {
            logger.info("Detected GriefPrevention, enabling support for it.");
            griefPreventionEnabled = true;
        }

        if (Bukkit.getPluginManager().isPluginEnabled("DecentHolograms")) {
            logger.info("Detected DecentHolograms, enabling support for it.");
            decentHologramsEnabled = true;
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Factions")) {
            logger.info("Detected FactionsUUID, enabling support for it.");
            factionsUUIDEnabled = true;
        }

        logger.info("Lunar Custom Items Enabled.");
    }

    private void registerEvents() {
        new BlockBreakListener().blockBreakHandler(this);
        new PlayerInteractListener().playerInteractHandler(this);
        new PlayerToggleSneakListener().playerToggleSneakHandler(this);
        new BlockPlaceListener().blockPlaceHandler(this);
        new BlockExplodeListener().blockExplodeHandler(this);
        new BlockPistonExtendListener().blockPistonExtendHandler(this);
        new EntityDeathListener().entityDeathHandler(this);
        new EntityDamageListener().entityDamageHandler(this);
        new PlayerJumpListener().playerJumpHandler(this);
        new PlayerLeaveListener().playerLeaveListener(this);
        new PlayerRunCommandListener().playerRunCommandHandler(this);
        if (AntiDropTracker.enabled) new AntiDropTracker().registerEvents(this);
    }

    private void registerCommands() {
        CustomItemsCommand.register();
        WallOfFireCommand.register();
        ResetItemCommand.register();
        UpdateLoreCommand.register();
    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("customitems");
        CommandAPI.unregister("walloffire");
        CommandAPI.unregister("resetlore");
        CommandAPI.unregister("updatelore");

        CommandAPI.onDisable();
    }
}
