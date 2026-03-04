package me.dunescifye.lunaritems.utils;

import org.bukkit.Material;

import java.util.Map;

// Contains all groups of materials
public class MaterialGroups {

  public static final Map<Material, Material> stripWood = Map.ofEntries(
    Map.entry(Material.OAK_LOG, Material.STRIPPED_OAK_LOG),
    Map.entry(Material.OAK_WOOD, Material.STRIPPED_OAK_WOOD),
    Map.entry(Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG),
    Map.entry(Material.SPRUCE_WOOD, Material.STRIPPED_SPRUCE_WOOD),
    Map.entry(Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG),
    Map.entry(Material.BIRCH_WOOD, Material.STRIPPED_BIRCH_WOOD),
    Map.entry(Material.JUNGLE_LOG, Material.STRIPPED_JUNGLE_LOG),
    Map.entry(Material.JUNGLE_WOOD, Material.STRIPPED_JUNGLE_WOOD),
    Map.entry(Material.ACACIA_LOG, Material.STRIPPED_ACACIA_LOG),
    Map.entry(Material.ACACIA_WOOD, Material.STRIPPED_ACACIA_WOOD),
    Map.entry(Material.DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG),
    Map.entry(Material.DARK_OAK_WOOD, Material.STRIPPED_DARK_OAK_WOOD),
    Map.entry(Material.MANGROVE_LOG, Material.STRIPPED_MANGROVE_LOG),
    Map.entry(Material.MANGROVE_WOOD, Material.STRIPPED_MANGROVE_WOOD),
    Map.entry(Material.CHERRY_LOG, Material.STRIPPED_CHERRY_LOG),
    Map.entry(Material.CHERRY_WOOD, Material.STRIPPED_CHERRY_WOOD),
    Map.entry(Material.CRIMSON_STEM, Material.STRIPPED_CRIMSON_STEM),
    Map.entry(Material.CRIMSON_HYPHAE, Material.STRIPPED_CRIMSON_HYPHAE),
    Map.entry(Material.WARPED_STEM, Material.STRIPPED_WARPED_STEM),
    Map.entry(Material.WARPED_HYPHAE, Material.STRIPPED_WARPED_HYPHAE),
    Map.entry(Material.BAMBOO_BLOCK, Material.STRIPPED_BAMBOO_BLOCK),
    Map.entry(Material.PALE_OAK_LOG, Material.STRIPPED_PALE_OAK_LOG),
    Map.entry(Material.PALE_OAK_WOOD, Material.STRIPPED_PALE_OAK_WOOD)
  );


  public static final Map<String, Map<Material, Material>> materialMap = Map.ofEntries(
    Map.entry("LOGS", stripWood)
  );
}
