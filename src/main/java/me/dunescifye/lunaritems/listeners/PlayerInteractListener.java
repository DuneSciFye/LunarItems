package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import eu.decentsoftware.holograms.api.DHAPI;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.utils.BlockUtils;
import me.dunescifye.lunaritems.utils.CooldownManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.time.Duration;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    public void playerInteractHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;


        if (e.getAction().isLeftClick()) {
            Block b = e.getClickedBlock();
            if (b != null) {
                if (p.isSneaking()) {
                    PersistentDataContainer pdc = new CustomBlockData(b, LunarItems.getPlugin());
                    String blockID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                    if (blockID != null) {
                        switch (blockID) {
                            case "teleport_pad" -> {
                                String uuid = pdc.get(LunarItems.keyUUID, PersistentDataType.STRING);
                                if (uuid != null) {
                                    DHAPI.removeHologram(uuid);
                                    pdc.remove(LunarItems.keyUUID);
                                } else {
                                    String hologramID = UUID.randomUUID().toString();
                                    pdc.set(LunarItems.keyUUID, PersistentDataType.STRING, hologramID);
                                    DHAPI.createHologram(hologramID, b.getLocation().toCenterLocation().add(0, BlocksConfig.teleport_padHologramOffset, 0), true, BlocksConfig.teleport_padHologram);
                                }
                            }
                            case "elevator" -> {
                                String uuid = pdc.get(LunarItems.keyUUID, PersistentDataType.STRING);
                                if (uuid != null) {
                                    DHAPI.removeHologram(uuid);
                                    pdc.remove(LunarItems.keyUUID);
                                } else {
                                    String hologramID = UUID.randomUUID().toString();
                                    pdc.set(LunarItems.keyUUID, PersistentDataType.STRING, hologramID);
                                    DHAPI.createHologram(hologramID, b.getLocation().toCenterLocation().add(0, BlocksConfig.elevatorHologramOffset, 0), true, BlocksConfig.elevatorHologram);
                                }
                            }
                        }
                    }
                }
            }
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;

        if (e.getAction().isRightClick()) {
            if (itemID.contains("nexushoe")) {
                if (p.isSneaking()) {
                    //If has cooldown
                    if (CooldownManager.hasCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId()))
                        CooldownManager.sendCooldownMessage(p, CooldownManager.getRemainingCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId()));
                        //If doesn't have cooldown
                    else {
                        double uses = container.get(LunarItems.keyUses, PersistentDataType.DOUBLE);
                        BlockUtils.boneMealRadius(p.getLocation().getBlock(), 5);
                        // If out of uses, set cooldown
                        if (uses == 1) {
                            CooldownManager.setCooldown(CooldownManager.nexusHoeCooldowns, p.getUniqueId(), Duration.ofMinutes(10));
                            container.set(LunarItems.keyUses, PersistentDataType.DOUBLE, 10.0);
                        } else
                            container.set(LunarItems.keyUses, PersistentDataType.DOUBLE, uses - 1);
                        item.setItemMeta(meta);
                    }
                }
            }
        }

    }
}
