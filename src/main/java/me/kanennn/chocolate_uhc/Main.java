package me.kanennn.chocolate_uhc;

import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public final class Main extends JavaPlugin {
    public static Material SACRIFICIAL_ITEM = Material.ENCHANTED_GOLDEN_APPLE;
    public static int TIME_LENGTH = 216000;
    public static int WORLD_BORDER_SIZE = 2048;
    public static int CENTER_SIZE = 32;

    public Events events;

    public boolean isInitialized;
    public boolean isEndgame = false;
    public boolean isStarted = false;

    public List<World> worlds = new ArrayList<>();
    public World overworld;
    public Location centerBlock;
    public Vector centerCoords = new Vector(960, 70, 960); // The South East / Positive X, Y corner of the 4 center blocks
    public BoundingBox soulWell;

    public ConsoleCommandSender console;

    public BukkitTask run23500;
    public BukkitTask run22500;
    public BukkitTask run5s;

    @Override
    public void onEnable() {
        events = new Events(this);
        Bukkit.getPluginManager().registerEvents(events, this);

        Bukkit.getLogger().log(Level.INFO, "Opened");
        Bukkit.getServerTickManager().setFrozen(true);

        getCommand("startgame").setExecutor(new StartCommand(this));
        getCommand("stopgame").setExecutor(new StopCommand(this));
        getCommand("endgame").setExecutor(new EndgameCommand(this));

        console = Bukkit.getServer().getConsoleSender();

        Bukkit.setDefaultGameMode(GameMode.ADVENTURE);

        // Plugin startup logic
    }

    public void whenWorldLoad(World w) {
        w.setGameRule(GameRule.NATURAL_REGENERATION, Boolean.FALSE);
        w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, Boolean.FALSE);
        w.setGameRule(GameRule.PLAYERS_SLEEPING_PERCENTAGE, 101);
        if (!isInitialized) {
            overworld = w;
            Bukkit.getLogger().log(Level.INFO, "Initialized");
            Initialize();
        }
    }

    public void Initialize() {

        centerBlock = new Location(overworld, centerCoords.getX(), centerCoords.getY(), centerCoords.getZ());
        soulWell = new BoundingBox(centerBlock.getX() - (double) CENTER_SIZE /2, centerBlock.getY() - (double) CENTER_SIZE /2, centerBlock.getZ() - (double) CENTER_SIZE /2, centerBlock.getX() + (double) CENTER_SIZE /2 - 1, centerBlock.getY() + (double) CENTER_SIZE /2 - 1, centerBlock.getZ() + (double) CENTER_SIZE /2 - 1);

        overworld.getWorldBorder().setSize(CENTER_SIZE);
        overworld.getWorldBorder().setWarningDistance(4);
        overworld.getWorldBorder().setWarningTime(60);
        overworld.getWorldBorder().setCenter(centerBlock);

        isInitialized = true;
    }

    public void Start() {
        overworld.setTime(0);

        run23500 = new BukkitRunnable() {
            @Override
            public void run() {
                Timing.runAt23500(Main.this);
            }
        }.runTaskTimer(this, 23500L, 24000L);

        run22500 = new BukkitRunnable() {
            @Override
            public void run() {
                Timing.runAt22500(overworld);
            }
        }.runTaskTimer(this, 22500L, 24000L);

        run5s = new BukkitRunnable() {
            @Override
            public void run() {
                Timing.runEvery5s(Main.this);
            }
        }.runTaskTimer(this, 0L, 100L);

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboardTags().contains("ready")) {
                p.setInvulnerable(false);
                p.setHealth(0);
                p.spigot().respawn();
                p.setGameMode(GameMode.SURVIVAL);
                p.removeScoreboardTag("ready");
                p.addScoreboardTag("active");

//                Bukkit.getServer().dispatchCommand(console, String.format("give %s filled_map[minecraft:map_id=0]", p.getName()));
            }
        }

        int x = centerBlock.getBlockX();  // Marks the x coord of the centre of your map.
        int z = centerBlock.getBlockZ();  // Marks the y coord of the centre of your map.
        int minDistance = WORLD_BORDER_SIZE / 8   ;  // The minimum distance between players / teams.
        int maxRange = WORLD_BORDER_SIZE/2 - 64;  // The maximum range (applies to x and z coordinates)
        boolean respectTeams = true;  // Whether players in teams should be teleported to the same location (if applicable).
        String players = "@a[tag=active]";  // Here you specify a list of player names separated by spaces, or use commandblock specifiers.
        Bukkit.getServer().dispatchCommand(console, String.format("spreadplayers %d %d %d %d %b %s", x, z, minDistance, maxRange, respectTeams, players));

        Bukkit.setDefaultGameMode(GameMode.SPECTATOR);

        Bukkit.getServerTickManager().setFrozen(false);

        overworld.getWorldBorder().setSize(WORLD_BORDER_SIZE);
        overworld.getWorldBorder().setSize(CENTER_SIZE,Main.TIME_LENGTH);
        overworld.getWorldBorder().setCenter(centerBlock);

        Bukkit.getScheduler().runTaskLater(this, Main.this::Endgame, Main.TIME_LENGTH);

        isStarted = true;
    }

    public void Endgame() {
        run23500.cancel();
        run22500.cancel();

        isEndgame = true;
        overworld.spawnEntity(centerBlock, EntityType.TNT);
        events.maintainDeadOffline.clear();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getScoreboardTags().contains("dead")) {
                Revive.deactivateFunction(p);
            }
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.ENTITY_WITHER_DEATH, WORLD_BORDER_SIZE,0.5f);
            p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "Endgame", ChatColor.RED + "has begun",0, 0, 100);
        }
    }

    public void Stop() {
        if (!isEndgame) {
            Endgame();
        }
        run5s.cancel();

        isEndgame = false;
        isStarted = false;
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p, Sound.ENTITY_ENDER_DRAGON_DEATH, WORLD_BORDER_SIZE, 0.5f);
            p.sendTitle(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "Game Over", "", 0, 300, 50);
            if (p.getScoreboardTags().contains("active")) {
                if (p.getScoreboardTags().contains("dead")) {
                    Revive.deactivateFunction(p);
                } else {
                    p.removeScoreboardTag("active");
                    p.setInvulnerable(true);
                    p.setAllowFlight(true);
                }
            }

        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}