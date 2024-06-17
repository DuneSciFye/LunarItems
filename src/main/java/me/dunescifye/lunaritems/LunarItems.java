package me.dunescifye.lunaritems;

import com.jeff_media.customblockdata.CustomBlockData;
import me.dunescifye.lunaritems.commands.CustomItemsCommand;
import me.dunescifye.lunaritems.files.AquaticItemsConfig;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.files.NexusItemsConfig;
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

    public static final NamespacedKey keyItemID = new NamespacedKey("executableitems", "ei-id");
    public static final NamespacedKey keyID = new NamespacedKey("lunaritems", "id");
    public static final NamespacedKey keyLocation = new NamespacedKey("lunaritems", "location");
    public static final NamespacedKey keyUses = new NamespacedKey("score", "score-uses");

    public static Map<String, ItemStack> items = new HashMap<>();
    public static Map<NamespacedKey, PersistentDataType> dataType = new HashMap<>();
    public static Map<NamespacedKey, Object> defaultValue = new HashMap<>();

    static {
        dataType.put(keyUses, PersistentDataType.DOUBLE);
        defaultValue.put(keyUses, 0.0);
    }

    public static LunarItems getPlugin() {
        return plugin;
    }
    @Override
    public void onEnable() {
        Logger logger = Bukkit.getLogger();

        plugin = this;
        Config.setup(this);
        AquaticItemsConfig.setup();
        NexusItemsConfig.setup();
        BlocksConfig.setup();
        registerEvents();
        CustomItemsCommand.register();
        CustomBlockData.registerListener(plugin);

        logger.info("Lunar Custom Items Enabled.");

    }

    private void registerEvents() {
        new BlockBreakListener().blockBreakHandler(this);
        new PlayerInteractListener().playerInteractHandler(this);
        new PlayerToggleSneakListener().playerToggleSneakHandler(this);
        new BlockPlaceListener().blockPlaceHandler(this);
        new BlockExplodeListener().blockExplodeHandler(this);
        new BlockPistonExtendListener().blockPistonExtendHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
