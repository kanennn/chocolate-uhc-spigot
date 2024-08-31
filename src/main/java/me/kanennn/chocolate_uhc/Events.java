package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Events implements Listener {

    public List<UUID> maintainDeadOffline = new ArrayList<>();
    public List<UUID> maintainAliveOffline = new ArrayList<>();
    private final Main main;


    public Events(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent e) {
        main.worlds.add(e.getWorld());
        main.whenWorldLoad(e.getWorld());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e)
    {
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.SPECTATE) && e.getPlayer().getScoreboardTags().contains("dead"))
        {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.SPECTATOR)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Revive.handlePlayerDeath(e);
        // does not work (ends game at the start)
//        if (Revive.checkIfGameOver()) {
//            main.Stop();
//        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        if (p.getScoreboardTags().contains("dead") && !main.isEndgame) {
            e.setRespawnLocation(main.overworld.getSpawnLocation());
            p.setGameMode(GameMode.SPECTATOR);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                Entity hamster = Revive.newHamster(p);
                p.setSpectatorTarget(hamster);
            }, 20L);
        } else if (p.getScoreboardTags().contains("dead") && main.isEndgame) {
            p.getScoreboardTags().remove("dead");
            e.setRespawnLocation(main.overworld.getSpawnLocation());
            p.setGameMode(GameMode.SPECTATOR);
        } else if (p.getScoreboardTags().contains("active")) {
            e.setRespawnLocation(main.overworld.getSpawnLocation());
            p.setGameMode(GameMode.SURVIVAL);
            Revive.noHamster(p);
        } else if (!p.getScoreboardTags().contains("ready")) {
            e.setRespawnLocation(main.overworld.getSpawnLocation());
            p.setGameMode(GameMode.SPECTATOR);
        } else {
            e.setRespawnLocation(main.overworld.getSpawnLocation());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (e.getPlayer().getScoreboardTags().contains("dead")) {
            maintainDeadOffline.add(e.getPlayer().getUniqueId());
        } else if (e.getPlayer().getScoreboardTags().contains("active")) {
            maintainAliveOffline.add(e.getPlayer().getUniqueId());
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> maintainDeadOffline.remove(e.getPlayer().getUniqueId()), 6000);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
            e.getPlayer().setInvulnerable(true);
        }
        if (e.getPlayer().getScoreboardTags().contains("dead")) {
            if (!maintainDeadOffline.contains(e.getPlayer().getUniqueId())) {
                Revive.deactivateFunction(e.getPlayer());
            } else {
                for (Allay a : main.overworld.getEntitiesByClass(Allay.class)) {
                    if (Objects.equals(a.getCustomName(), e.getPlayer().getName())) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                            e.getPlayer().setSpectatorTarget(a);
                        }, 20L);
                        break;
                    }
                }
            }
        } else if (e.getPlayer().getScoreboardTags().contains("active")) {
            if (!maintainAliveOffline.contains(e.getPlayer().getUniqueId())) {
                e.getPlayer().setHealth(0);
            }
        }
    }

//    @EventHandler
//      public void onInteract(PlayerInteractEvent e) {
//        if (e.getPlayer().getScoreboardTags().contains("dead")) {
//            e.setCancelled(true);
//        }
//    }
//
//    @EventHandler
//    public void onInteractEntity(PlayerInteractEntityEvent e) {
//        if (e.getPlayer().getScoreboardTags().contains("dead")) {
//            e.setCancelled(true);
//        }
//    }

    @EventHandler
    public void enterEndPortal(PlayerPortalEvent e) {
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.END_PORTAL)) {
            e.setCancelled(true);
            e.getPlayer().teleport(main.overworld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }
    }

    @EventHandler
    public void onBurnEntity(EntityDeathEvent e) {
        if ((Objects.requireNonNull(e.getEntity().getLastDamageCause()).getCause().equals(EntityDamageEvent.DamageCause.FIRE) || e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.FIRE_TICK) || e.getEntity().getLastDamageCause().getCause().equals(EntityDamageEvent.DamageCause.LAVA))) {
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
        if (e.getEntity() instanceof Item && ((Item) e.getEntity()).getItemStack().getType().equals(Main.SACRIFICIAL_ITEM) && (main.soulWell.contains(e.getEntity().getBoundingBox())) && main.isStarted && !main.isEndgame) {
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
    }

    @EventHandler
    public void onBurnBlock(BlockBurnEvent e) {
        if (main.soulWell.contains(e.getBlock().getBoundingBox()) && !main.isEndgame) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onDamageBlock(BlockDamageEvent e) {
        if (main.soulWell.contains(e.getBlock().getBoundingBox()) && !main.isEndgame && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent e) {
        if (main.soulWell.contains(e.getBlock().getBoundingBox()) && !main.isEndgame && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onExplodeBlock(BlockExplodeEvent e) {
        if (main.soulWell.contains(e.getBlock().getBoundingBox()) && !main.isEndgame) {
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent e) {
        if (main.soulWell.contains(e.getBlock().getBoundingBox()) && !main.isEndgame && !e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
            e.setCancelled(true);
        }
    }

}