package net.lelberto.skyblockconfinement.events;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldEvent extends Event {

    private static final List<String> playersInWorld = new ArrayList<>();
    private World world;

    public WorldEvent(int initTime, int time) {
        super("World Event", initTime, time);
        this.world = null;
    }

    @Override
    public void runInit() {
        WorldType type = (Math.random() <= 0.1) ? WorldType.AMPLIFIED : WorldType.NORMAL; // Chance of 10% for the "Amplified" world type
        this.world = new WorldCreator("world_event").environment(World.Environment.NORMAL).type(type).generateStructures(true).createWorld();
    }

    @Override
    public void run() {
        Random rand = new Random();
        for (Player player : Bukkit.getOnlinePlayers()) {
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            if (sbPlayer.getIsland() != null) {
                double x = rand.nextInt(1000);
                if (rand.nextBoolean()) {
                    x = -x;
                }
                double z = rand.nextInt(1000);
                if (rand.nextBoolean()) {
                    x = -x;
                }
                WorldEvent.playersInWorld.add(player.getName());
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 30 * 20, 10, true, true, true));
                player.teleport(new Location(this.world, x, 300, z));
            }
        }
    }

    @Override
    public void runStop() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().equals(this.world)) {
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                Island island = sbPlayer.getIsland();
                WorldEvent.playersInWorld.remove(player.getName());
                if (island != null) {
                    player.teleport(sbPlayer.getIsland().getHome());
                } else {
                    player.teleport(Plugin.getConfiguration().spawn);
                }
            }
        }
        Bukkit.unloadWorld(this.world.getName(), false);
        try {
            FileUtils.deleteDirectory(new File("world_event"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onWorldLoad(WorldInitEvent e) {
            World world = e.getWorld();
            if (world.getName().equals("world_event")) {
                world.setKeepSpawnInMemory(false);
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            Event event = Game.getCurrentEvent();
            if ((!(event instanceof WorldEvent) || event.getStatus() == Status.FINISHED) && WorldEvent.playersInWorld.contains(player.getName())) {
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                WorldEvent.playersInWorld.remove(player.getName());
                Island island = sbPlayer.getIsland();
                if (island != null) {
                    player.teleport(sbPlayer.getIsland().getHome());
                } else {
                    player.teleport(Plugin.getConfiguration().spawn);
                }
            }
        }
    }
}
