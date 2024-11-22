package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiftCommand implements CommandExecutor {
    private final Main main;

    public GiftCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player p1 = (Player) commandSender;
            String username = args[0];
            int value;
            try {
                value = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                value = 0;
            }
            Player p2 = Bukkit.getPlayer(username);
            if (value != 0 && p2 != null) {
                if (p1.equals(p2)) {
                    p1.sendMessage(ChatColor.RED + "You cannot heal yourself");
                    return true;
                }
                if (value*2 < p1.getHealth()) {
                    p1.setHealth(p1.getHealth() - 2*value);
                    p1.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p1.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() - 2*value);
                    p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + 2*value);
                    p2.setHealth(p2.getHealth() + 2*value);
                } else {
                    double new_value = p1.getHealth();
                    p1.setHealth(0);
                    p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(p2.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue() + new_value + 10);
                    p2.setHealth(p2.getHealth() + new_value + 10);
                }
                return true;
            }
        }
        return false;
    }
}
