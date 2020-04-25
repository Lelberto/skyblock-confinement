package net.lelberto.skyblockconfinement;

import net.lelberto.skyblockconfinement.challenges.MulticolorChallenge;
import net.lelberto.skyblockconfinement.challenges.PlayerHeadChallenge;
import net.lelberto.skyblockconfinement.commands.IslandChatCommand;
import net.lelberto.skyblockconfinement.commands.SkyBlockCommand;
import net.lelberto.skyblockconfinement.commands.SpawnCommand;
import net.lelberto.skyblockconfinement.events.BossEvent;
import net.lelberto.skyblockconfinement.events.WorldEvent;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import net.lelberto.skyblockconfinement.inventories.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Plugin extends JavaPlugin {

    public static void log(String msg) {
        System.out.println("[" + Plugin.getPluginName() + "] " + msg);
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(Plugin.getPluginName());
    }

    public static String getPluginName() {
        return "SkyBlockConfinement";
    }

    public static String getPluginPrefix() {
        return "§7[§bSkyBlock§7]§r ";
    }

    public static File getPluginFolder() {
        return Bukkit.getPluginManager().getPlugin(Plugin.getPluginName()).getDataFolder();
    }

    public static Configuration getConfiguration() {
        return Configuration.getInstance();
    }

    public static Scoreboards getScoreboards() {
        return Scoreboards.getInstance();
    }

    public static World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    @Override
    public void onEnable() {
        // Commands registration
        this.getCommand("skyblock").setExecutor(new SkyBlockCommand());
        this.getCommand("spawn").setExecutor(new SpawnCommand());
        this.getCommand("islandchat").setExecutor(new IslandChatCommand());

        // Listeners registration
        this.getServer().getPluginManager().registerEvents(new SkyBlockPlayer.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new SkyblockInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new IslandInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new InviteInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new ChallengeCategoriesInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new ChallengesInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new ChallengeDetailsInventory.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new MulticolorChallenge.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerHeadChallenge.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new WorldEvent.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new BossEvent.Listener(), this);
        this.getServer().getPluginManager().registerEvents(new Game.Listener(), this);

        Bukkit.getOnlinePlayers().forEach(Plugin.getScoreboards()::createScordboard);

        // Game initialization
        Game.initChallenges();

        // Start schedulers
        Game.startDayCounter();
        Game.startCountIslandsBlocks();
        Game.startScoreboardRefresh();
    }

    @Override
    public void onDisable() {
        Plugin.getConfiguration().save();
        Bukkit.getScheduler().cancelTasks(this);
    }
}
