package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class Events implements Listener {

    private final Main main;

    public Events(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Revive.handlePlayerDeath(e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Revive.handlePlayerRespawn(e, main);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getScoreboardTags().contains("limbo")) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (e.getPlayer().getScoreboardTags().contains("limbo")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBurnEntity(EntityDeathEvent e) {
        if ((e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.LAVA))) {
            checkIsSacrifice(e);
        }
    }

    @EventHandler
    public void onCombustEntity(EntityCombustEvent e) {
          {
            checkIsSacrifice(e);
        }
    }

    public void checkIsSacrifice(EntityEvent e) {
        if (e.getEntity() instanceof Item && ((Item) e.getEntity()).getItemStack().getType().equals(Pineapples.SACRIFICIAL_ITEM)) {
            // && (Pineapples.SOUL_BOX.contains(e.getEntity().getBoundingBox()))
            handleSacrifice(e);
        }
    }

    public void handleSacrifice(EntityEvent e) {
        ItemMeta item = ((Item) e.getEntity()).getItemStack().getItemMeta();
        assert item != null;
        if (item.hasDisplayName()) {
            String name = item.getDisplayName();
            Player player = Bukkit.getPlayer(name);
            if (player != null) {
                Revive.handlePlayerRevive(player);
            }
        }

//                Bukkit.getOnlinePlayers().forEach(player1 -> player1.playSound(e.getEntity().getLocation(), Sound.ITEM_TRIDENT_THUNDER, 50, 1));
//                e.getEntity().getWorld().strikeLightningEffect(e.getEntity().getLocation());
//                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
//                main.playerManager.unBanPlayer(player);
//            }
//        }
    }

}