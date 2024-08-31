package me.kanennn.chocolate_uhc;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class Revive {

    public static Allay newHamster(Player p) {
        Allay hamster = (Allay) p.getWorld().spawnEntity(p.getWorld().getSpawnLocation(), EntityType.ALLAY, false);
        hamster.setInvulnerable(true);
        hamster.setPersistent(true);
        hamster.setCustomNameVisible(true);
        hamster.setCustomName(p.getName());
        hamster.setCanDuplicate(false);
        return hamster;
    }
    public static void noHamster(Player p) {
        for (Allay e : p.getWorld().getEntitiesByClass(Allay.class)) {
            if (e.getName().equals(p.getName()) | Objects.equals(e.getCustomName(), p.getName())) {
                e.setHealth(0.0);
            }
        }
    }

    public static void deactivateFunction(Player p) {
        p.removeScoreboardTag("dead");
        p.removeScoreboardTag("active");
        p.setHealth(0);
        p.spigot().respawn();
        noHamster(p);
    }

    // this does not seem to work
    public static boolean checkIfGameOver() {
        int i = 0;
        for (Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getScoreboardTags().contains("dead") && p.getScoreboardTags().contains("active")) {
                    if (team.hasEntry(p.getName())) {
                        i++;
                        break;
                    }
                }
            }
        }
        return (i <= 1);
    }

    public static void handlePlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (p.getScoreboardTags().contains("active") && !p.getScoreboardTags().contains("dead")) {
            deathFunction(p);
            Revive.deathEffect(p);
        } else if (p.getScoreboardTags().contains("dead")) {
            e.setDeathMessage(null);
        } else {
            e.setDeathMessage(null);
        }
    }

    public static void deathFunction(Player p) {
        p.addScoreboardTag("dead");
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
            reviveFunction(p);
            reviveEffect(p);
        }
    }

    public static void reviveFunction(Player p) {
        p.setHealth(0);
        p.removeScoreboardTag("dead");
        p.spigot().respawn();
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