package me.kanennn.chocolate_uhc;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EndgameCommand implements CommandExecutor {
    private final Main main;

    public EndgameCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        main.Endgame();
        return true;
    }
}