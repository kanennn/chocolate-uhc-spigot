package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;

import java.lang.classfile.Signature;

public class Timing {

    public static void runEvery5s(Main main) {
        if (main.isEndgame) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.getWorld().equals(main.overworld)) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 0));
                }
            }
        }
        WorldBorder border = main.overworld.getWorldBorder();
        double centerX = border.getCenter().getX();
        double centerZ = border.getCenter().getZ();
        double radius = border.getSize()/2;
        BoundingBox warningBox = new BoundingBox(centerX + radius - 10, 1024, centerZ + radius - 10, centerX - radius + 10, -64, centerZ - radius + 10);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (warningBox.contains(p.getBoundingBox())) {
                p.kickPlayer("AAAA");
            }
        }
    }
}3