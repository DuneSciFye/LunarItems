package me.dunescifye.lunaritems.listeners;

import me.dunescifye.lunaritems.LunarItems;
import me.dunescifye.lunaritems.utils.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerTeleportListener implements Listener {

  private static final NamespacedKey keyWorld4 = new NamespacedKey("score", "score-world4");
  private static final NamespacedKey keyWorld3 = new NamespacedKey("score", "score-world3");
  private static final NamespacedKey keyWorld2 = new NamespacedKey("score", "score-world2");
  private static final NamespacedKey keyWorld1 = new NamespacedKey("score", "score-world1");
  private static final NamespacedKey keyCoords4 = new NamespacedKey("score", "score-coords4");
  private static final NamespacedKey keyCoords3 = new NamespacedKey("score", "score-coords3");
  private static final NamespacedKey keyCoords2 = new NamespacedKey("score", "score-coords2");
  private static final NamespacedKey keyCoords1 = new NamespacedKey("score", "score-coords1");

  @EventHandler
  public void onPlayerTeleport(PlayerTeleportEvent e) {

    PlayerTeleportEvent.TeleportCause teleportCause = e.getCause();
    if (teleportCause.equals(PlayerTeleportEvent.TeleportCause.COMMAND) || teleportCause.equals(PlayerTeleportEvent.TeleportCause.PLUGIN)) {
      Player p = e.getPlayer();
      if (!p.hasMetadata("backcommand")) {

        ItemStack[] items = p.getInventory().getContents();
        for (int i = 0; i < items.length; i++) {
          ItemStack invItem = items[i];
          if (invItem != null && invItem.hasItemMeta()) {
            ItemMeta meta = invItem.getItemMeta();
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            String itemID = pdc.get(LunarItems.keyEIID, PersistentDataType.STRING);
            if (itemID == null || !itemID.contains("sunbuilder")) continue;

            String world4 = pdc.get(keyWorld4, PersistentDataType.STRING);
            String world3 = pdc.get(keyWorld3, PersistentDataType.STRING);
            String world2 = pdc.get(keyWorld2, PersistentDataType.STRING);
            String world1 = pdc.get(keyWorld1, PersistentDataType.STRING);
            String coords4 = pdc.get(keyCoords4, PersistentDataType.STRING);
            String coords3 = pdc.get(keyCoords3, PersistentDataType.STRING);
            String coords2 = pdc.get(keyCoords2, PersistentDataType.STRING);
            String coords1 = pdc.get(keyCoords1, PersistentDataType.STRING);

            if (world4 != null && !world4.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " world5 " + world4);
            if (world3 != null && !world3.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " world4 " + world3);
            if (world2 != null && !world2.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() +
              " " + i + " world3 " + world2);
            if (world1 != null && !world1.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " world2 " + world1);
            if (coords4 != null && !coords4.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() +
              " " + i + " coords5 " + coords4);
            if (coords3 != null && !coords3.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " coords4 " + coords3);
            if (coords2 != null && !coords2.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " coords3 " + coords2);
            if (coords1 != null && !coords1.isEmpty()) Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " coords2 " + coords1);

            Location loc = p.getLocation();

            Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " world1 " + p.getWorld().getName());
            Utils.runConsoleCommands("ei console-modification set variable " + p.getName() + " " + i + " coords1 " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());

          }
        }
      }
    }

  }

}
