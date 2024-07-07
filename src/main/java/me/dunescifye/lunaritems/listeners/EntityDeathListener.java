package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {


    public void entityDeathHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    //Armor
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        Entity entity = e.getEntity();
        Player p = e.getEntity().getKiller();
        if (p == null) return;
        ItemStack helmet = p.getInventory().getHelmet(),
            chestplate = p.getInventory().getChestplate(),
            leggings = p.getInventory().getLeggings(),
            boots = p.getInventory().getBoots();
        ItemMeta helmetMeta, chestplateMeta, leggingsMeta, bootsMeta;
        PersistentDataContainer helmetContainer, chestplateContainer, leggingsContainer, bootsContainer;
        String helmetID = "", chestplateID = "", leggingsID = "", bootsID = "";
        //Obtaining armor info
        if (helmet == null || !helmet.hasItemMeta()) return;
        helmetMeta = helmet.getItemMeta();
        helmetContainer = helmetMeta.getPersistentDataContainer();
        helmetID = helmetContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (helmetID == null) return;

        if (chestplate == null || !chestplate.hasItemMeta()) return;
        chestplateMeta = chestplate.getItemMeta();
        chestplateContainer = chestplateMeta.getPersistentDataContainer();
        chestplateID = chestplateContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (chestplateID == null) return;

        if (leggings == null || !leggings.hasItemMeta()) return;
        leggingsMeta = leggings.getItemMeta();
        leggingsContainer = leggingsMeta.getPersistentDataContainer();
        leggingsID = leggingsContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (leggingsID == null) return;

        if (boots == null || !boots.hasItemMeta()) return;
        bootsMeta = boots.getItemMeta();
        bootsContainer = bootsMeta.getPersistentDataContainer();
        bootsID = bootsContainer.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (bootsID == null) return;

        //Ancient armor double mob drops
        if (helmetID.contains("ancientthelm") && chestplateID.contains("ancienttchest") && leggingsID.contains("ancienttlegs") && bootsID.contains("ancienttboots")) {
            if (!(entity instanceof Player)) {
                List<ItemStack> drops = e.getDrops();
                for (ItemStack drop : drops) {
                    entity.getWorld().dropItemNaturally(entity.getLocation(), drop);
                }
            }
        }
    }
}
