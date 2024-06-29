package me.dunescifye.lunaritems.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.clip.placeholderapi.PlaceholderAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.Config;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

public class Utils {

    public static void runConsoleCommands(String... commands){
        Server server = Bukkit.getServer();
        ConsoleCommandSender console = server.getConsoleSender();
        for (String command : commands){
            server.dispatchCommand(console, command);
        }
    }

    //Drop item at player
    public static void dropItems(Location location, ItemStack... items) {
        for (ItemStack item : items) {
            Item drop = location.getWorld().dropItemNaturally(location, item);
            drop.setPickupDelay(0);
        }
    }

    //Drop item with owner
    public static void dropItems(Location location, UUID uuid, ItemStack... items) {
        for (ItemStack item : items) {
            Item drop = location.getWorld().dropItemNaturally(location, item);
            drop.setPickupDelay(0);
            drop.setOwner(uuid);
        }
    }


    //Update lore and PDC for two variables
    public static void updateKey(Player p, ItemStack item, ItemMeta meta, PersistentDataContainer container, NamespacedKey key1, NamespacedKey key2, String variable, Object... matchers){
        //Add first two to the list again, allows for cycling back
        List<Object> newMatchers = new ArrayList<>(Arrays.asList(matchers));
        newMatchers.add(matchers[0]);
        newMatchers.add(matchers[1]);
        //Obtain current keys
        Object oldKey1 = container.get(key1, LunarItems.dataType.get(key1));
        //Set new keys to first values in case key's get messed up
        Object newKey1 =  matchers[0];
        Object newKey2 =  matchers[1];
        //Match key to our list
        for (int i = 0; i < newMatchers.size(); i+=2) {
            if (Objects.equals(oldKey1, newMatchers.get(i))) {
                newKey1 = newMatchers.get(i + 2);
                newKey2 = newMatchers.get(i + 3);
                break;
            }
        }
        //Send player message
        sendPlayerChangeVariableMessage(p, Config.changeVariableMessage, variable, String.valueOf(newKey1));
        //Update PDC, lore, and Meta
        container.set(key1, LunarItems.dataType.get(key1), newKey1);
        container.set(key2, LunarItems.dataType.get(key2), newKey2);
        meta.lore(updateLore(item, String.valueOf(oldKey1), String.valueOf(newKey1)));
        item.setItemMeta(meta);
    }
    //Update lore and PDC for three variables
    public static void updateKey(Player p, ItemStack item, ItemMeta meta, PersistentDataContainer container, NamespacedKey key1, NamespacedKey key2, NamespacedKey key3, String variable, Object... matchers){
        //Add first three to the list again, allows for cycling back
        List<Object> newMatchers = new ArrayList<>(Arrays.asList(matchers));
        newMatchers.add(matchers[0]);
        newMatchers.add(matchers[1]);
        newMatchers.add(matchers[2]);
        //Obtain current keys
        Object oldKey1 = container.get(key1, LunarItems.dataType.get(key1));
        //Set new keys to first values in case key's get messed up
        Object newKey1 =  matchers[0];
        Object newKey2 =  matchers[1];
        Object newKey3 = matchers[2];
        //Match key to our list
        for (int i = 0; i < newMatchers.size(); i+=3) {
            if (Objects.equals(oldKey1, newMatchers.get(i))) {
                newKey1 = newMatchers.get(i + 3);
                newKey2 = newMatchers.get(i + 4);
                newKey3 = newMatchers.get(i + 5);
                break;
            }
        }
        //Send player message
        sendPlayerChangeVariableMessage(p, Config.changeVariableMessage, variable, String.valueOf(newKey1));
        //Update PDC, lore, and Meta
        container.set(key1, LunarItems.dataType.get(key1), newKey1);
        container.set(key2, LunarItems.dataType.get(key2), newKey2);
        container.set(key3, LunarItems.dataType.get(key3), newKey3);
        meta.lore(updateLore(item, String.valueOf(oldKey1), String.valueOf(newKey1)));
        item.setItemMeta(meta);
    }


    public static void sendPlayerChangeVariableMessage(Player player, String message, String variable, String content){
        player.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(PlaceholderAPI.setPlaceholders(player, Config.prefix + message.replace("%player%", player.getName()).replace("%variable%", variable).replace("%content%", content))));
    }

    public static List<Component> updateLore(ItemStack item, String matcher, String replacement){
        List<Component> loreList = item.lore();

        TextReplacementConfig config = TextReplacementConfig.builder()
            .match(" " + matcher + " ")
            .replacement(" " + replacement + " ")
            .build();

        if (loreList != null)
            loreList.replaceAll(component -> component.replaceText(config));

        return loreList;
    }

    public static boolean isInRegion(Player p, List<String> regions) {
        for (ProtectedRegion rg : WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery().getApplicableRegions(BukkitAdapter.adapt(p.getLocation()))) {
            if (regions.contains(rg.getId().toLowerCase())) return true;
        }
        return false;
    }
}
