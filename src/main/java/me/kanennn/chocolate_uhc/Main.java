package me.kanennn.chocolate_uhc;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    public Events events;
    public Cherries cherries;

    public World world;
    public Location spawn;

    @Override
    public void onEnable() {
        world = Bukkit.getServer().getWorlds().get(0);
        spawn = new Location(world, Pineapples.SPAWN_COORDS.getX(), Pineapples.SPAWN_COORDS.getY(), Pineapples.SPAWN_COORDS.getZ());

        // Plugin startup logic
        events = new Events(this);
        Bukkit.getPluginManager().registerEvents(events, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}