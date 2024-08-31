package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Timing {

    public static void runAt23500(Main main) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboardTags().contains("dead")) {
                Revive.deactivateFunction(p);
            }
        }
    }

    public static void runAt22500(World w) {
        ItemStack i = new ItemStack(Material.ENCHANTED_GOLDEN_APPLE);
        w.dropItem(w.getSpawnLocation().clone().add(0.5, 1.5, 0.5), i);
    }

    public static void runEvery5s(Main main) {
        if (main.isEndgame) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getWorld().equals(main.overworld)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
                }
            }
        }
    }
}