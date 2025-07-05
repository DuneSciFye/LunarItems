package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.utils.FUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.List;

public class FallingBlockListener implements Listener {


  @EventHandler(priority= EventPriority.LOWEST)
  public void onFallingBlockLand(EntityChangeBlockEvent e) {
    if (e.getEntityType() == EntityType.FALLING_BLOCK) {
      Entity entity = e.getEntity();
      if (!entity.hasMetadata("owner")) return;
      List<MetadataValue> meta = entity.getMetadata("owner");
      String playerUUID = meta.get(0).asString();

      if (!FUtils.isInClaimOrWilderness(Bukkit.getPlayer(playerUUID), e.getBlock().getLocation())) e.setCancelled(true);
    }
  }
}
