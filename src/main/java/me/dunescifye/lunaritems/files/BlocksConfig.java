package me.dunescifye.lunaritems.files;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.ConfigUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.dunescifye.lunaritems.LunarItems.getPlugin;

public class BlocksConfig {

    // Legacy items (pressure plates) - for backward compatibility
    public static ItemStack teleport_pad, elevator;
    public static Integer teleport_padHologramOffset, elevatorHologramOffset;
    public static List<String> teleport_padHologram, elevatorHologram;

    // Color GUI configuration
    public static String colorGuiTitle;
    public static String colorChangeSuccessMessage;
    public static String colorChangeNoPermissionMessage;
    public static String colorChangeNoMoneyMessage;
    public static String colorChangeBypassPermission;
    public static Map<String, ColorOption> colorOptions = new LinkedHashMap<>();

    public static class ColorOption {
        public Material material;
        public Material shulkerMaterial;
        public String displayName;
        public List<String> lore;
        public double cost;
        public String permission;
        public int slot;

        public ItemStack createGuiItem() {
            ItemStack item = new ItemStack(material);
            ItemMeta meta = item.getItemMeta();
            meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(displayName).decoration(TextDecoration.ITALIC, false));
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(LegacyComponentSerializer.legacyAmpersand()
                    .deserialize(line).decoration(TextDecoration.ITALIC, false));
            }
            meta.lore(loreComponents);
            item.setItemMeta(meta);
            return item;
        }
    }

    public static void setup() {
        ConfigUtils blocksConfig = new ConfigUtils(getPlugin(), "items/blocks.yml");
        FileConfiguration config = blocksConfig.getConfig();

        List<String> hologram = Arrays.asList(
                "#ff032d&lTeleport Pad",
                "&f➥ Shift to teleport!"
        );

        List<String> elevatorHologramDefault = Arrays.asList(
                "&6&lElevator",
                "&f➥ Jump/Sneak to move!"
        );

        // Legacy teleport_pad (pressure plate) - for backward compatibility
        if (!config.isSet("teleport_pad")){
            config.addDefault("teleport_pad.name", "&8[#ff032d&lTELEPORT PAD&8]");
            List<String> lore = Arrays.asList(
                "&7&l&m----------------",
                "&fPlace this teleport pad to fast travel between",
                "&ftwo points on the server. It comes with an A and a B",
                "&7&l&m----------------"
            );
            config.addDefault("teleport_pad.lore", lore);
            config.addDefault("teleport_pad.material", "WHITE_SHULKER_BOX");
            config.addDefault("teleport_pad.hologram", hologram);
        }

        // Legacy elevator (pressure plate) - for backward compatibility
        if (!config.isSet("elevator")){
            config.addDefault("elevator.name", "&6&lElevator");
            List<String> lore = Arrays.asList(
                "&7&l&m----------------",
                "&fPlace this elevator to fast travel",
                "&fvertically. Jump to go up, sneak to go down!",
                "&7&l&m----------------"
            );
            config.addDefault("elevator.lore", lore);
            config.addDefault("elevator.material", "WHITE_SHULKER_BOX");
            config.addDefault("elevator.hologram", elevatorHologramDefault);
        }

        // Color GUI Settings
        if (!config.isSet("color_gui")) {
            config.addDefault("color_gui.title", "&8Change Color");
            config.addDefault("color_gui.success_message", "&aSuccessfully changed color to %color%!");
            config.addDefault("color_gui.no_permission_message", "&cYou don't have permission to use this color!");
            config.addDefault("color_gui.no_money_message", "&cYou need %cost% to change to this color!");
            config.addDefault("color_gui.bypass_permission", "lunaritems.colorchange.bypass");

            // Default color options
            setupDefaultColorOption(config, "white", Material.WHITE_CONCRETE, Material.WHITE_SHULKER_BOX, "&fWhite", 0, 0, "lunaritems.color.white");
            setupDefaultColorOption(config, "orange", Material.ORANGE_CONCRETE, Material.ORANGE_SHULKER_BOX, "&6Orange", 1000, 1, "lunaritems.color.orange");
            setupDefaultColorOption(config, "magenta", Material.MAGENTA_CONCRETE, Material.MAGENTA_SHULKER_BOX, "&dMagenta", 1000, 2, "lunaritems.color.magenta");
            setupDefaultColorOption(config, "light_blue", Material.LIGHT_BLUE_CONCRETE, Material.LIGHT_BLUE_SHULKER_BOX, "&bLight Blue", 1000, 3, "lunaritems.color.light_blue");
            setupDefaultColorOption(config, "yellow", Material.YELLOW_CONCRETE, Material.YELLOW_SHULKER_BOX, "&eYellow", 1000, 4, "lunaritems.color.yellow");
            setupDefaultColorOption(config, "lime", Material.LIME_CONCRETE, Material.LIME_SHULKER_BOX, "&aLime", 1000, 5, "lunaritems.color.lime");
            setupDefaultColorOption(config, "pink", Material.PINK_CONCRETE, Material.PINK_SHULKER_BOX, "&dPink", 1000, 6, "lunaritems.color.pink");
            setupDefaultColorOption(config, "gray", Material.GRAY_CONCRETE, Material.GRAY_SHULKER_BOX, "&8Gray", 1000, 7, "lunaritems.color.gray");
            setupDefaultColorOption(config, "light_gray", Material.LIGHT_GRAY_CONCRETE, Material.LIGHT_GRAY_SHULKER_BOX, "&7Light Gray", 1000, 8, "lunaritems.color.light_gray");
            setupDefaultColorOption(config, "cyan", Material.CYAN_CONCRETE, Material.CYAN_SHULKER_BOX, "&3Cyan", 2000, 9, "lunaritems.color.cyan");
            setupDefaultColorOption(config, "purple", Material.PURPLE_CONCRETE, Material.PURPLE_SHULKER_BOX, "&5Purple", 2000, 10, "lunaritems.color.purple");
            setupDefaultColorOption(config, "blue", Material.BLUE_CONCRETE, Material.BLUE_SHULKER_BOX, "&9Blue", 2000, 11, "lunaritems.color.blue");
            setupDefaultColorOption(config, "brown", Material.BROWN_CONCRETE, Material.BROWN_SHULKER_BOX, "&6Brown", 2000, 12, "lunaritems.color.brown");
            setupDefaultColorOption(config, "green", Material.GREEN_CONCRETE, Material.GREEN_SHULKER_BOX, "&2Green", 2000, 13, "lunaritems.color.green");
            setupDefaultColorOption(config, "red", Material.RED_CONCRETE, Material.RED_SHULKER_BOX, "&cRed", 2000, 14, "lunaritems.color.red");
            setupDefaultColorOption(config, "black", Material.BLACK_CONCRETE, Material.BLACK_SHULKER_BOX, "&0Black", 2000, 15, "lunaritems.color.black");
        }

        // Load legacy items
        teleport_pad = ConfigUtils.initializeItem("teleport_pad", config);
        teleport_padHologram = ConfigUtils.setupConfig("teleport_pad.hologram", config, hologram);
        teleport_padHologramOffset = ConfigUtils.setupConfig("teleport_pad.hologramOffset", config, 1);
        elevator = ConfigUtils.initializeItem("elevator", config);
        elevatorHologram = ConfigUtils.setupConfig("elevator.hologram", config, elevatorHologramDefault);
        elevatorHologramOffset = ConfigUtils.setupConfig("elevator.hologramOffset", config, 1);

        // Load color GUI settings
        colorGuiTitle = ConfigUtils.setupConfig("color_gui.title", config, "&8Change Color");
        colorChangeSuccessMessage = ConfigUtils.setupConfig("color_gui.success_message", config, "&aSuccessfully changed color to %color%!");
        colorChangeNoPermissionMessage = ConfigUtils.setupConfig("color_gui.no_permission_message", config, "&cYou don't have permission to use this color!");
        colorChangeNoMoneyMessage = ConfigUtils.setupConfig("color_gui.no_money_message", config, "&cYou need %cost% to change to this color!");
        colorChangeBypassPermission = ConfigUtils.setupConfig("color_gui.bypass_permission", config, "lunaritems.colorchange.bypass");

        // Load color options
        ConfigurationSection colorsSection = config.getConfigurationSection("color_gui.colors");
        if (colorsSection != null) {
            for (String colorKey : colorsSection.getKeys(false)) {
                ColorOption option = new ColorOption();
                String path = "color_gui.colors." + colorKey;
                option.material = Material.valueOf(config.getString(path + ".gui_material", "WHITE_CONCRETE").toUpperCase());
                option.shulkerMaterial = Material.valueOf(config.getString(path + ".shulker_material", "WHITE_SHULKER_BOX").toUpperCase());
                option.displayName = config.getString(path + ".name", "&fWhite");
                option.lore = config.getStringList(path + ".lore");
                if (option.lore.isEmpty()) {
                    option.lore = Arrays.asList("&7Cost: &a$" + config.getDouble(path + ".cost", 0), "&7Click to change color!");
                }
                option.cost = config.getDouble(path + ".cost", 0);
                option.permission = config.getString(path + ".permission", "lunaritems.color." + colorKey);
                option.slot = config.getInt(path + ".slot", 0);
                colorOptions.put(colorKey, option);
            }
        }

        config.options().copyDefaults(true);
        blocksConfig.save();
    }

    private static void setupDefaultColorOption(FileConfiguration config, String colorKey, Material guiMaterial, Material shulkerMaterial, String name, double cost, int slot, String permission) {
        String path = "color_gui.colors." + colorKey;
        config.addDefault(path + ".gui_material", guiMaterial.toString());
        config.addDefault(path + ".shulker_material", shulkerMaterial.toString());
        config.addDefault(path + ".name", name);
        config.addDefault(path + ".lore", Arrays.asList("&7Cost: &a$" + (int) cost, "&7Click to change color!"));
        config.addDefault(path + ".cost", cost);
        config.addDefault(path + ".permission", permission);
        config.addDefault(path + ".slot", slot);
    }

    /**
     * Check if a material is a shulker box
     */
    public static boolean isShulkerBox(Material material) {
        return material.toString().endsWith("_SHULKER_BOX") || material == Material.SHULKER_BOX;
    }
}
