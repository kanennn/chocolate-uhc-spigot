package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {

    public List<UUID> maintainDeadOffline = new ArrayList<>();
    public List<UUID> maintainAliveOffline = new ArrayList<>();
    private final Main main;


    public Events(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = ((Player) e.getEntity());
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getHealth() - e.getFinalDamage());
        }
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        main.worlds.add(e.getWorld());
        main.whenWorldLoad(e.getWorld());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        AttributeInstance attr = e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH);
        attr.setBaseValue(20);
        if (e.getEntity().getLastDamageCause().getDamageSource().getCausingEntity() instanceof Player) {
            Player p = ((Player) e.getEntity().getLastDamageCause().getDamageSource().getCausingEntity());
            p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 10);
            p.setHealth(p.getHealth() + 10);
        }
        Revive.handlePlayerDeath(e);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            if (!main.isEndgame) {
                maintainAliveOffline.add(e.getPlayer().getUniqueId());
                Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> maintainDeadOffline.remove(e.getPlayer().getUniqueId()), 12000);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            if (main.isStarted) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            } else {
                e.getPlayer().setInvulnerable(true);
            }
        }
        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            if (!maintainAliveOffline.contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().setHealth(0);
            }
        }
    }

    @EventHandler
    public void enterEndPortal(PlayerPortalEvent e) {
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            e.setCancelled(true);
            e.getPlayer().teleport(main.overworld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }
}