package me.dunescifye.lunaritems.utils;

import me.dunescifye.lunaritems.LunarItems;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class ConfigUtils {

    private File file;
    private FileConfiguration config;

    public ConfigUtils(Plugin plugin, String path){
        this(plugin.getDataFolder().getAbsolutePath() + "/" + path);
    }

    public ConfigUtils(String path){
        this.file = new File(path);
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean save(){
        try {
            this.config.save(this.file);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile(){
        return this.file;
    }

    public FileConfiguration getConfig(){
        return this.config;
    }

    //Method for checking if is integer by Jonas K https://stackoverflow.com/questions/237159/whats-the-best-way-to-check-if-a-string-represents-an-integer-in-java
    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        /*
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
         */
        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    //Int with min value
    public static int setupConfig(String path, FileConfiguration config, int defaultValue, int minValue) {
        Logger logger = Bukkit.getLogger();
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }

        String valueStr = config.getString(path);

        if (!isInteger(valueStr)) {
            logger.warning("[CustomItems] " + path + " is not a valid number. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        int value = Integer.parseInt(valueStr);

        if (value < minValue) {
            logger.warning("[CustomItems] " + path + " is not a valid number. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        return value;
    }

    //Integer with min value and comment
    public static int setupConfig(String path, FileConfiguration config, int defaultValue, int minValue, List<String> comments) {
        if (!config.isSet(path)) {
            int value =  setupConfig(path, config, defaultValue, minValue);
            config.setComments(path, comments);
            return value;
        } else {
            return setupConfig(path, config, defaultValue, minValue);
        }
    }
    //Double with min value
    public static double setupConfig(String path, FileConfiguration config, double defaultValue, double minValue) {
        Logger logger = Bukkit.getLogger();
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }

        String valueStr = config.getString(path);

        if (!NumberUtils.isCreatable(valueStr)) {
            logger.warning("[CustomItems] " + path + " is not a valid double. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        double value = Double.parseDouble(valueStr);

        if (value < minValue) {
            logger.warning("[CustomItems] " + path + " is not a valid double. Must be a number greater than  " + minValue + ". Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        return value;
    }

    //Double with min value and comment
    public static double setupConfig(String path, FileConfiguration config, double defaultValue, double minValue, List<String> comments) {
        if (!config.isSet(path)) {
            double value = setupConfig(path, config, defaultValue, minValue);
            config.setComments(path, comments);
            return value;
        } else {
            return setupConfig(path, config, defaultValue, minValue);
        }
    }
    //Int without bound
    public static int setupConfig(String path, FileConfiguration config, int defaultValue) {
        Logger logger = Bukkit.getLogger();
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }

        String valueStr = config.getString(path);

        if (!valueStr.matches("-?\\d+(\\.\\d+)?")) {
            logger.warning("[CustomItems] " + path + " is not a valid number. Found " + valueStr + ". Using default value of " + defaultValue + ".");
            return defaultValue;
        }

        return Integer.parseInt(valueStr);
    }
    //String
    public static String setupConfig(String path, FileConfiguration config, String defaultValue) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }
        return config.getString(path);
    }
    //String with comments
    public static String setupConfig(String path, FileConfiguration config, String defaultValue, List<String> comments) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            config.setComments(path, comments);
            return defaultValue;
        }
        return config.getString(path);
    }
    //List of strings
    public static List<String> setupConfig(String path, FileConfiguration config, List<String> defaultValue) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            return defaultValue;
        }
        return config.getStringList(path);
    }
    //List of strings with comments
    public static List<String> setupConfig(String path, FileConfiguration config, List<String> defaultValue, List<String> comments) {
        if (!config.isSet(path)) {
            config.set(path, defaultValue);
            config.setComments(path, comments);
            return defaultValue;
        }
        return config.getStringList(path);

    }

    //Item without keys
    public static ItemStack initializeItem(String itemID, FileConfiguration config){
        Material material;
        if (config.isSet(itemID + ".material")) {
            material = Material.getMaterial(config.getString(itemID + ".material").toUpperCase());
        } else {
            config.set(itemID + ".material", "PAPER");
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(LunarItems.keyEIID, PersistentDataType.STRING, itemID.toLowerCase());
        if (config.isSet(itemID + ".name")) {
            String name = config.getString(itemID + ".name");
            if (name.contains("§")) {
                name = name.replace("§", "&");
                config.set(itemID + ".name", name);
            }
            meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(name).decoration(TextDecoration.ITALIC, false));
        }
        if (config.isSet(itemID + ".lore")) {
            List<Component> lore = new ArrayList<>();
            List<String> modifiedLore = new ArrayList<>();
            for (String line : config.getStringList(itemID + ".lore")) {
                line = line.replace("§", "&");
                modifiedLore.add(line);
                lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line).decoration(TextDecoration.ITALIC, false));
            }
            if (!modifiedLore.isEmpty()) config.set(itemID + ".lore", modifiedLore);
            meta.lore(lore);
        }
        if (config.isSet(itemID + ".enchantments")) {
            for (String enchantment : config.getStringList(itemID + ".enchantments")) {
                String enchantName = enchantment.split(":")[0].toLowerCase();
                int enchantLevel = Integer.parseInt(enchantment.split(":")[1]);
                meta.addEnchant(Registry.ENCHANTMENT.get(NamespacedKey.fromString(enchantName)), enchantLevel, true);
            }
        }
        if (config.isSet(itemID + ".customModelData")) meta.setCustomModelData(config.getInt(itemID + ".customModelData"));
        if (config.isSet(itemID + ".unbreakable")) meta.setUnbreakable(config.getBoolean(itemID + ".unbreakable"));
        if (config.isSet(itemID + ".itemFlags")){
            for (String itemFlag : config.getStringList(itemID + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(itemFlag.toUpperCase()));
            }
        }

        if (config.isSet(itemID + ".armorColor")) {
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(config.getInt(itemID + ".armorColor")));
                item.setItemMeta(leatherArmorMeta);
            }
        }
        item.setItemMeta(meta);

        LunarItems.items.put(itemID, item);

        return item;
    }

    //Item with keys
    public static ItemStack initializeItem(String itemID, FileConfiguration config, NamespacedKey... data){
        Material material;
        if (config.isSet(itemID + ".material")) {
            material = Material.getMaterial(config.getString(itemID + ".material").toUpperCase());
        } else {
            config.set(itemID + ".material", "PAPER");
            material = Material.PAPER;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        //Setting PDC's
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(LunarItems.keyEIID, PersistentDataType.STRING, itemID.toLowerCase());
        for (NamespacedKey key : data) {
            container.set(key, LunarItems.dataType.get(key), LunarItems.defaultValue.get(key));
        }

        //Setting Name
        String name = config.getString(itemID + ".name");
        if (name != null) {
            if (name.contains("§")) {
                name = name.replace("§", "&");
                config.set(itemID + ".name", name);
            }
            meta.displayName(LegacyComponentSerializer.legacyAmpersand()
                .deserialize(name).decoration(TextDecoration.ITALIC, false));
        }
        //Setting lore
        if (config.isSet(itemID + ".lore")) {
            List<Component> lore = new ArrayList<>();
            List<String> modifiedLore = new ArrayList<>();
            for (String line : config.getStringList(itemID + ".lore")) {
                line = line.replace("§", "&");
                modifiedLore.add(line);
                lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line).decoration(TextDecoration.ITALIC, false));
            }
            if (!modifiedLore.isEmpty()) config.set(itemID + ".lore", modifiedLore);
            meta.lore(lore);
        }
        //Setting enchantments
        if (config.isSet(itemID + ".enchantments")) {
            for (String enchantment : config.getStringList(itemID + ".enchantments")) {
                String enchantName = enchantment.split(":")[0].toLowerCase();
                int enchantLevel = Integer.parseInt(enchantment.split(":")[1]);
                meta.addEnchant(Registry.ENCHANTMENT.get(NamespacedKey.fromString(enchantName)), enchantLevel, true);
            }
        }
        if (config.isSet(itemID + ".customModelData")) meta.setCustomModelData(config.getInt(itemID + ".customModelData"));
        if (config.isSet(itemID + ".unbreakable")) meta.setUnbreakable(config.getBoolean(itemID + ".unbreakable"));
        if (config.isSet(itemID + ".itemFlags")){
            for (String itemFlag : config.getStringList(itemID + ".itemFlags")) {
                meta.addItemFlags(ItemFlag.valueOf(itemFlag.toUpperCase()));
            }
        }
        //Setting armor color
        if (config.isSet(itemID + ".armorColor")) {
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(config.getInt(itemID + ".armorColor")));
                item.setItemMeta(leatherArmorMeta);
            }
        }

        item.setItemMeta(meta);
        //Putting item in map
        LunarItems.items.put(itemID, item);

        return item;
    }

}
