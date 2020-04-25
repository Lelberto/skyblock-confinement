package net.lelberto.skyblockconfinement.game;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.SkyblockException;
import net.lelberto.skyblockconfinement.util.Schematic;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Island {

    public static Island getNearIsland(Location location) {
        Island island = Plugin.getConfiguration().islands.stream().filter(currentIsland -> currentIsland.getSpawn().getWorld().equals(location.getWorld())).sorted((island1, island2) -> location.distance(island1.getSpawn()) < location.distance(island2.getSpawn()) ? -1 : 1).findFirst().orElse(null);
        return island != null ? (island.getSpawn().distance(location) <= Plugin.getConfiguration().islandAreaSize ? island : null) : null;
    }

    private String name;
    private final Location spawn;
    private Location home;
    private Map<Material, Integer> blocks;
    private boolean countingBlocksRunning;
    private List<SkyBlockPlayer> pendingInvites;

    public Island(String name, Location spawn, Location home) {
        this.name = name;
        this.spawn = spawn;
        this.home = home;
        this.blocks = new HashMap<>();
        this.countingBlocksRunning = false;
        this.pendingInvites = new ArrayList<>();
    }

    public void create() throws SkyblockException {
        Schematic.load(new File(new File(Plugin.getPluginFolder(), "schematics"), "island.schem")).build(this.spawn);;
    }

    public void countBlocks() {
        new BukkitRunnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                System.out.println("Start counting blocks for " + Island.this.getName());
                Island.this.countingBlocksRunning = true;
                Map<Material, Integer> blocks = new HashMap<>();
                World world = Island.this.spawn.getWorld();
                int areaSize = Plugin.getConfiguration().islandAreaSize;
                Location location1 = new Location(world, Island.this.spawn.getX() - areaSize / 2, 0, Island.this.spawn.getZ() - areaSize / 2);
                Location location2 = new Location(world, Island.this.spawn.getX() + areaSize / 2, Island.this.spawn.getWorld().getMaxHeight(), Island.this.spawn.getZ() + areaSize / 2);
                for (int x = location1.getBlockX(); x < location2.getBlockX(); x++) {
                    for (int z = location1.getBlockZ(); z < location2.getBlockZ(); z++) {
                        for (int y = location1.getBlockY(); y < location2.getBlockY(); y++) {
                            Material type = world.getBlockAt(x, y, z).getType();
                            if (type.isSolid()) {
                                if (!blocks.containsKey(type)) {
                                    blocks.put(type, 0);
                                }
                                blocks.put(type, blocks.get(type) + 1);
                            }
                        }
                    }
                }
                System.out.println("Finish counting blocks for " + Island.this.getName() + " (" + (TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time)) + " minutes)");
                Island.this.countingBlocksRunning = false;
                Island.this.blocks = blocks;
            }
        }.runTaskAsynchronously(Plugin.getPlugin());
    }

    public void sendMessage(String msg) {
        this.getOwners().forEach(owner -> {
            OfflinePlayer offPlayer = owner.toBukkitPlayer();
            if (offPlayer.isOnline()) {
                offPlayer.getPlayer().sendMessage("§7[§eIle§7]§r " + msg);
            }
        });
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public Location getHome() {
        return home;
    }

    public void setHome(Location home) {
        this.home = home;
    }

    public Map<Material, Integer> getBlocks() {
        return this.blocks;
    }

    public int getBlockCount() {
        return this.blocks.values().stream().mapToInt(Integer::intValue).sum();
    }

    public boolean isCountingBlocksRunning() {
        return countingBlocksRunning;
    }

    public List<SkyBlockPlayer> getOwners() {
        return Plugin.getConfiguration().sbPlayers.stream().filter(sbPlayer -> sbPlayer.getIsland() != null && sbPlayer.getIsland().equals(this)).collect(Collectors.toList());
    }

    public List<SkyBlockPlayer> getPendingInvites() {
        return pendingInvites;
    }
}
