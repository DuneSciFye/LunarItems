package me.dunescifye.lunaritems.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import eu.decentsoftware.holograms.api.DHAPI;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.files.BlocksConfig;
import me.dunescifye.lunaritems.gui.ColorGUI;
import me.dunescifye.lunaritems.utils.*;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.generator.structure.Structure;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.BiomeSearchResult;
import org.bukkit.util.StructureSearchResult;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerInteractListener implements Listener {

    public void playerInteractHandler(LunarItems plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    // Block shulker box opening for elevator/teleport_pad - runs first with HIGH priority
    @EventHandler(priority = EventPriority.HIGH)
    public void onShulkerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;

        Block b = e.getClickedBlock();
        if (b == null) return;

        // Check if it's a shulker box
        if (!BlocksConfig.isShulkerBox(b.getType())) return;

        // Check if it's an elevator or teleport_pad
        PersistentDataContainer pdc = new CustomBlockData(b, LunarItems.getPlugin());
        String blockID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);

        if (blockID != null && (blockID.equals("elevator") || blockID.equals("teleport_pad"))) {
            // Block the shulker from opening
            e.setCancelled(true);

            Player p = e.getPlayer();

            // If sneaking, open color GUI (with protection check)
            if (p.isSneaking()) {
                if (!p.hasPermission(BlocksConfig.colorChangeBypassPermission)
                    && !FUtils.isInClaimOrWilderness(p, b.getLocation())) {
                    return;
                }
                new ColorGUI(p, b, blockID);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (e.getHand() == EquipmentSlot.OFF_HAND) return;
        Block b = e.getClickedBlock();


        if (e.getAction().isLeftClick()) {
            if (b != null) {
                if (p.isSneaking()) {
                    PersistentDataContainer pdc = new CustomBlockData(b, LunarItems.getPlugin());
                    String blockID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
                    if (blockID != null) {
                        switch (blockID) {
                            case "teleport_pad" -> {
                                if (LunarItems.decentHologramsEnabled) {
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
                            }
                            case "elevator" -> {
                                if (LunarItems.decentHologramsEnabled) {
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
        }

        ItemStack item = p.getInventory().getItemInMainHand();
        if (!item.hasItemMeta()) return;

        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer container = meta.getPersistentDataContainer();
        String itemID = container.get(LunarItems.keyEIID, PersistentDataType.STRING);
        if (itemID == null) return;

        if (e.getAction().isRightClick()) {
            if (itemID.contains("erospick")) {
                if (!p.isSneaking()) {
                    if (CooldownManager.hasCooldown(CooldownManager.erosPickCDs, p.getUniqueId())) {
                        CooldownManager.sendCooldownMessage(p, CooldownManager.getRemainingCooldown(CooldownManager.erosPickCDs, p.getUniqueId()));
                    } else {
                        Structure structure = RegistryAccess.registryAccess().getRegistry(RegistryKey.STRUCTURE).get(new NamespacedKey(
                            "minecraft",
                            container.getOrDefault(new NamespacedKey("score", "score-structure"), PersistentDataType.STRING,
                                "trial_chambers").toLowerCase())
                        );
                        if (structure != null) {
                            CooldownManager.setCooldown(CooldownManager.erosPickCDs, p.getUniqueId(),
                                Duration.ofSeconds(5));
//                            World world = p.getWorld();
//                            Location loc = p.getLocation();
//                            UUID uuid = p.getUniqueId();
//
//                            Bukkit.getScheduler().runTask(LunarItems.getPlugin(), () -> {
//                                StructureSearchResult searchResult = world.locateNearestStructure(loc, structure, 1000, false);
//                                Player player = Bukkit.getPlayer(uuid);
//                                if (player == null) return; // player logged out
//
//                                if (searchResult == null) player.sendMessage(Utils.translateMessage("&6&lCUSTOM" +
//                                    " &8&l▶ &7No Structure found nearby."));
//                                else {
//                                    Location location = searchResult.getLocation();
//                                    player.sendMessage(Utils.translateMessage("&6&lCUSTOM" +
//                                        " &8&l▶ &7Nearest Structure at " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()));
//                                    CooldownManager.setCooldown(CooldownManager.erosPickCDs, uuid, Duration.ofMinutes(8));
//                                }
//                            });
                        }
                    }
                }
            }
            else if (itemID.contains("nexushoe")) {
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
            else if (itemID.contains("amberlightbuilderschisel") && b != null) {
                if (!FUtils.isInClaimOrWilderness(p, b.getLocation())) return;
                if (!WorldGuardUtils.getRegions(p.getLocation()).isEmpty()) return;
                int radius = (int) (double) container.getOrDefault(LunarItems.keyRadius,  PersistentDataType.DOUBLE,
                  0.0);
                for (Block block : Utils.getBlocksInRadius(b, radius)) {
                    String mat = block.getType().toString();
                    Material newMat;
                    checkMats: {
                        newMat = Material.getMaterial(mat + "_STAIRS");
                        if (newMat != null) break checkMats;
                        if (mat.contains("_STAIRS")) mat = mat.replace("_STAIRS", "");
                        newMat = Material.getMaterial(mat + "_SLAB");
                        if (newMat != null) break checkMats;
                        if (mat.contains("_SLAB")) mat = mat.replace("_SLAB", "");
                        newMat = Material.getMaterial(mat + "_WALL");
                        if (newMat != null) break checkMats;
                        if (mat.contains("_WALL")) mat = mat.replace("_WALL", "");
                        newMat = Material.getMaterial("CRACKED_" + mat);
                        if (newMat != null) break checkMats;
                        if (mat.contains("CRACKED_")) mat = mat.replace("CRACKED_", "");
                        newMat = Material.getMaterial("CHISELED_" + mat);
                        if (newMat != null) break checkMats;
                        if (mat.contains("CHISELED_")) mat = mat.replace("CHISELED_", "");
                        newMat = Material.getMaterial(mat);
                    }
                    if (newMat != null && !Objects.equals(newMat.toString(), block.getType().toString())) {
                        block.setType(newMat);
                    }
                }
            }
        } else {
            if (itemID.contains("seraphimxp")) {
                if (!p.isSneaking()) {
                    if (CooldownManager.hasCooldown(CooldownManager.seraphimXpCDs, p.getUniqueId())) {
                        CooldownManager.sendCooldownMessage(p, CooldownManager.getRemainingCooldown(CooldownManager.seraphimXpCDs, p.getUniqueId()));
                    } else {
                        Biome biome = RegistryAccess.registryAccess().getRegistry(RegistryKey.BIOME).get(new NamespacedKey(
                          "minecraft",
                          container.getOrDefault(new NamespacedKey("score", "score-biome"), PersistentDataType.STRING,
                            "plains").toLowerCase())
                        );
                        if (biome != null) {
                            CooldownManager.setCooldown(CooldownManager.seraphimXpCDs, p.getUniqueId(),
                              Duration.ofSeconds(5));
                            World world = p.getWorld();
                            CompletableFuture<Location> cf = new CompletableFuture<>();
                            Bukkit.getScheduler().runTaskAsynchronously(LunarItems.getPlugin(), () -> {
                                BiomeSearchResult searchResult = world.locateNearestBiome(p.getLocation(), 5000, biome);
                                cf.complete(searchResult == null ? null : searchResult.getLocation());
                            });

                            cf.whenComplete((location, err) -> {
                                if (err != null || location == null) p.sendMessage(Utils.translateMessage("&6&lCUSTOM" +
                                  " &8&l▶ &7No biome found nearby."));
                                else {
                                    p.teleportAsync(world.getHighestBlockAt(location).getLocation());
                                    CooldownManager.setCooldown(CooldownManager.seraphimXpCDs, p.getUniqueId(), Duration.ofMinutes(8));
                                }
                            });
                        }
                    }
                }
            }
        }

    }
}
