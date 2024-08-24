package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public Events events;

    @Override
    public void onEnable() {
        // Plugin startup logic
        events = new Events(this);

        Bukkit.getPluginManager().registerEvents(events, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}