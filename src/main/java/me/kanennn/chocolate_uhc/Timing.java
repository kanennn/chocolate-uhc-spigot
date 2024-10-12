package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Timing {

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