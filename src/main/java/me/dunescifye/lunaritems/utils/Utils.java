package me.dunescifye.lunaritems.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static me.dunescifye.lunaritems.files.Config.allItems;

public class Utils {

    public static final NamespacedKey keyItemID = new NamespacedKey("executableitems", "ei-id");
    public static final NamespacedKey keyUses = new NamespacedKey("score", "score-uses");

    private static Map<NamespacedKey, PersistentDataType> dataType = new HashMap<>();
    private static Map<NamespacedKey, Object> defaultValue = new HashMap<>();

    static {
        dataType.put(keyUses, PersistentDataType.DOUBLE);
        defaultValue.put(keyUses, 0.0);
    }

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    public static Material getCropDrops(Block crop) {
        if (crop.getType() == Material.WHEAT) return Material.WHEAT;
        return crop.getType();
    }

    public static ItemStack initializeItem(String itemID, ConfigUtils configUtils){
        FileConfiguration config = configUtils.getConfig();
        Material material = null;
        if (config.isSet(itemID + ".material")) material = Material.getMaterial(config.getString(itemID + ".material").toUpperCase());
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item.getItemMeta();

        meta.getPersistentDataContainer().set(keyItemID, PersistentDataType.STRING, itemID);
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
        ConfigurationSection attributesSection = config.getConfigurationSection(itemID + ".attributes");
        if (attributesSection != null) {
            for (String key : attributesSection.getKeys(false)) {
                ConfigurationSection attributeConfig = attributesSection.getConfigurationSection(key);
                if (attributeConfig != null) {
                    double amount = attributeConfig.isSet("amount") ? attributeConfig.getDouble("amount") : 1.0;
                    UUID uuid = attributeConfig.isSet("uuid") ? UUID.fromString(attributeConfig.getString("uuid")) : UUID.randomUUID();

                    Attribute attribute = attributeConfig.isSet("attribute") ? Attribute.valueOf(attributeConfig.getString("attribute").toUpperCase()) : Attribute.GENERIC_ARMOR;
                    AttributeModifier.Operation operation = attributeConfig.isSet("operation") ? AttributeModifier.Operation.valueOf(attributeConfig.getString("operation").toUpperCase()) : AttributeModifier.Operation.ADD_NUMBER;
                    EquipmentSlot slot = attributeConfig.isSet("slot") ? EquipmentSlot.valueOf(attributeConfig.getString("slot").toUpperCase()) : EquipmentSlot.HAND;

                    AttributeModifier modifier = new AttributeModifier(uuid, key, amount, operation, slot);
                    meta.addAttributeModifier(attribute, modifier);
                }
            }
        }
        if (config.isSet(itemID + ".isNetherite")){
            if ((config.isBoolean(itemID + ".isNetherite"))){
                if (material == Material.LEATHER_BOOTS){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                }
                else if (material == Material.LEATHER_LEGGINGS){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                }
                else if (material == Material.LEATHER_CHESTPLATE){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                }
                else if (material == Material.LEATHER_HELMET){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                }
            }
        }

        if (config.isSet(itemID + ".armorColor")) {
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(config.getInt(itemID + ".armorColor")));
                item.setItemMeta(leatherArmorMeta);
            }
        }

        configUtils.save();

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack initializeItem(String itemID, ConfigUtils configUtils, NamespacedKey... data){
        FileConfiguration config = configUtils.getConfig();
        Material material = null;

        if (config.isSet(itemID + ".material")) material = Material.getMaterial(config.getString(itemID + ".material").toUpperCase());
        ItemStack item = new ItemStack(material != null ? material : Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(keyItemID, PersistentDataType.STRING, itemID);

        for (NamespacedKey key : data) {
            container.set(key, dataType.get(key), defaultValue.get(key));
        }

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
        ConfigurationSection attributesSection = config.getConfigurationSection(itemID + ".attributes");
        if (attributesSection != null) {
            for (String key : attributesSection.getKeys(false)) {
                ConfigurationSection attributeConfig = attributesSection.getConfigurationSection(key);
                if (attributeConfig != null) {
                    double amount = attributeConfig.isSet("amount") ? attributeConfig.getDouble("amount") : 1.0;
                    UUID uuid = attributeConfig.isSet("uuid") ? UUID.fromString(attributeConfig.getString("uuid")) : UUID.randomUUID();

                    Attribute attribute = attributeConfig.isSet("attribute") ? Attribute.valueOf(attributeConfig.getString("attribute").toUpperCase()) : Attribute.GENERIC_ARMOR;
                    AttributeModifier.Operation operation = attributeConfig.isSet("operation") ? AttributeModifier.Operation.valueOf(attributeConfig.getString("operation").toUpperCase()) : AttributeModifier.Operation.ADD_NUMBER;
                    EquipmentSlot slot = attributeConfig.isSet("slot") ? EquipmentSlot.valueOf(attributeConfig.getString("slot").toUpperCase()) : EquipmentSlot.HAND;

                    AttributeModifier modifier = new AttributeModifier(uuid, key, amount, operation, slot);
                    meta.addAttributeModifier(attribute, modifier);
                }
            }
        }
        if (config.isSet(itemID + ".isNetherite")){
            if ((config.isBoolean(itemID + ".isNetherite"))){
                if (material == Material.LEATHER_BOOTS){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                }
                else if (material == Material.LEATHER_LEGGINGS){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 6.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.LEGS));
                }
                else if (material == Material.LEATHER_CHESTPLATE){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
                }
                else if (material == Material.LEATHER_HELMET){
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "netherite", 8.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_ARMOR_TOUGHNESS, new AttributeModifier(UUID.randomUUID(), "netherite", 3.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                    meta.addAttributeModifier(Attribute.GENERIC_KNOCKBACK_RESISTANCE, new AttributeModifier(UUID.randomUUID(), "netherite", 0.1, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.FEET));
                }
            }
        }

        if (config.isSet(itemID + ".armorColor")) {
            if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
                leatherArmorMeta.setColor(Color.fromRGB(config.getInt(itemID + ".armorColor")));
                item.setItemMeta(leatherArmorMeta);
            }
        }

        configUtils.save();

        item.setItemMeta(meta);

        allItems.put(itemID, item);

        return item;
    }

}
