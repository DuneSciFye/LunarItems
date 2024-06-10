package me.dunescifye.lunaritems;

import me.dunescifye.lunaritems.commands.CustomItemsCommand;
import me.dunescifye.lunaritems.files.Config;
import me.dunescifye.lunaritems.listeners.BlockBreakListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class LunarItems extends JavaPlugin {

    private static LunarItems plugin;
    public static final NamespacedKey keyItemID = new NamespacedKey("executableitems", "ei-id");

    public static LunarItems getPlugin() {
        return plugin;
    }
    @Override
    public void onEnable() {
        Logger logger = Bukkit.getLogger();

        plugin = this;
        Config.setup(this);
        CustomItemsCommand.register();
        new BlockBreakListener().blockBreakHandler(this);

        logger.info("Lunar Custom Items Enabled.");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
