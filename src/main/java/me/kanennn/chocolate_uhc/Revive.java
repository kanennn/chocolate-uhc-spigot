package me.kanennn.chocolate_uhc;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Revive {

    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void handlePlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (p.getScoreboardTags().contains("active") && !p.getScoreboardTags().contains("dead")) {
            p.addScoreboardTag("dead");
            deathEffect(p);
        } else if (p.getScoreboardTags().contains("dead")) {
            e.setDeathMessage(null);
        }
    }

    public static void handlePlayerRespawn(PlayerRespawnEvent e, Plugin main) {
        Player p = e.getPlayer();
        if (p.getScoreboardTags().contains("dead")) {
//            e.setRespawnLocation(p.getLastDeathLocation());
            e.setRespawnLocation(p.getWorld().getSpawnLocation());
            engageLimbo(p, main);
        }
        else if (p.getScoreboardTags().contains("active")) {
            e.setRespawnLocation(p.getWorld().getSpawnLocation());
            disengageLimbo(p, main);
        }

    }

    public static void engageLimbo(Player p, Plugin main) {
        Bukkit.getOnlinePlayers().forEach(player1 -> player1.hidePlayer(main, p));
        p.addScoreboardTag("limbo");
        p.setCollidable(false);
        p.setInvisible(true);
        p.setInvulnerable(true);
        p.setCanPickupItems(false);
        p.setExpCooldown(-1);
    }
    public static void disengageLimbo(Player p, Plugin main) {
        Bukkit.getOnlinePlayers().forEach(player1 -> player1.showPlayer(main, p));
        p.removeScoreboardTag("limbo");
        p.setCollidable(true);
        p.setInvisible(false);
        p.setInvulnerable(false);
        p.setCanPickupItems(true);
        p.setExpCooldown(0);
    }

    public static void deathEffect(Player p) {
        Bukkit.getOnlinePlayers().forEach(player1 -> globalDeathEffect(player1, p));
        localDeathEffect(p);
    }
    public static void localDeathEffect(Entity e) {
        e.getWorld().playSound(e.getLocation(), Sound.BLOCK_END_PORTAL_SPAWN, 2048, 1.5f);
        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ALLAY_DEATH, 2048, 0.5f);
        e.getWorld().strikeLightningEffect(e.getLocation());
    }

    public static void globalDeathEffect(Player p, Player p2) {
        p.playSound(p.getLocation(), Sound.AMBIENT_CAVE, 2048, 1.5f);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + p2.getName() + " was eliminated!"));
    }

    public static void handlePlayerRevive(Player p) {
        if (p.getScoreboardTags().contains("dead")) {
            p.setHealth(0);
            p.removeScoreboardTag("dead");
            p.spigot().respawn();
            reviveEffect(p);
        }
    }

    public static void reviveEffect(Player p) {
        Bukkit.getOnlinePlayers().forEach(player1 -> globalReviveEffect(player1, p));
        localReviveEffect(p);
    }
    public static void localReviveEffect(Entity e) {
        e.getWorld().playSound(e.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 2048, 0.7f);
        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ALLAY_AMBIENT_WITHOUT_ITEM, 2048, 0.7f);
        e.getWorld().strikeLightningEffect(e.getLocation());
    }

    public static void globalReviveEffect(Player p, Player p2) {
        //p.playSound(p.getLocation());
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + p2.getName() + " has returned!"));
    }
}