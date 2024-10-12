package me.kanennn.chocolate_uhc;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;

public class Revive {

    public static void handlePlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (p.getGameMode().equals(GameMode.SURVIVAL)) {
            Revive.deathEffect(p);
        } else {
            e.setDeathMessage(null);
        }
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
}