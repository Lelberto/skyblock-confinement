package net.lelberto.skyblockconfinement;

import net.lelberto.skyblockconfinement.challenges.Challenge;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.entity.EntityDamageEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Configuration {

    private static Configuration INSTANCE;

    public static Configuration getInstance() {
        if (Configuration.INSTANCE == null) {
            Configuration.INSTANCE = new Configuration(new File(Bukkit.getPluginManager().getPlugin(Plugin.getPluginName()).getDataFolder(), "config.yml"));
            Configuration.INSTANCE.load();
        }
        return Configuration.INSTANCE;
    }

    private final File configFile;
    public int day;
    public Location spawn;
    public int spawnProtectionradius;
    public int islandCreatedCount;
    public int islandAreaSize;
    public int islandNameMaxLength;
    public int firstLevelXp;
    public double xpMultiplier;
    public double coalGenerationChance;
    public double ironGenerationChance;
    public double goldGenerationChance;
    public double diamondGenerationChance;
    public int worldEventInitTime;
    public int worldEventTime;
    public int bossEventInitTime;
    public int bossEventTime;
    public Location bossEventSpawn;
    public final List<Island> islands;
    public final List<SkyBlockPlayer> sbPlayers;

    public Configuration(File configFile) {
        this.configFile = configFile;
        this.day = 1;
        this.spawn = new Location(Plugin.getDefaultWorld(), 0, 133, 0);
        this.spawnProtectionradius = 50;
        this.islandCreatedCount = 0;
        this.islandAreaSize = 500;
        this.islandNameMaxLength = 16;
        this.firstLevelXp = 10;
        this.xpMultiplier = 1.3;
        this.coalGenerationChance = 10.0;
        this.ironGenerationChance = 5.0;
        this.goldGenerationChance = 2.0;
        this.diamondGenerationChance = 0.3;
        this.worldEventInitTime = 3 * 60;
        this.worldEventTime = 30 * 60;
        this.bossEventInitTime = 5 * 60;
        this.bossEventTime = 30 * 60;
        this.bossEventSpawn = new Location(Plugin.getDefaultWorld(), -1000, 133, 0);
        this.islands = new ArrayList<>();
        this.sbPlayers = new ArrayList<>();

        // Configuration file creation
        try {
            if (!configFile.exists()) {
                YamlConfiguration config = new YamlConfiguration();
                config.set("day", this.day);
                config.set("spawn", this.spawn);
                config.set("spawnProtectionRadius", this.spawnProtectionradius);
                config.set("islandCreatedCount", this.islandCreatedCount);
                config.set("islandAreaSize", this.islandAreaSize);
                config.set("islandNameMaxLength", this.islandNameMaxLength);
                config.set("firstLevelXp", this.firstLevelXp);
                config.set("xpMultiplier", this.xpMultiplier);
                ConfigurationSection generationChanceSection = config.createSection("generationChance");
                generationChanceSection.set("coal", this.coalGenerationChance);
                generationChanceSection.set("iron", this.ironGenerationChance);
                generationChanceSection.set("gold", this.goldGenerationChance);
                generationChanceSection.set("diamond", this.diamondGenerationChance);
                ConfigurationSection eventsSection = config.createSection("events");
                ConfigurationSection worldEventSection = eventsSection.createSection("worldEvent");
                worldEventSection.set("initTime", this.worldEventInitTime);
                worldEventSection.set("time", this.worldEventTime);
                ConfigurationSection bossEventSection = eventsSection.createSection("bossEvent");
                bossEventSection.set("initTime", this.bossEventInitTime);
                bossEventSection.set("time", this.bossEventTime);
                bossEventSection.set("spawn", this.bossEventSpawn);
                config.createSection("islands");
                config.createSection("players");
                config.save(configFile);
            }
        } catch (IOException ex) {
            Plugin.log("Could not create the configuration file : " + ex.getMessage());
        }
    }

    public void load() {
        this.islands.clear();
        this.sbPlayers.clear();

        YamlConfiguration config = YamlConfiguration.loadConfiguration(this.configFile);
        this.day = config.getInt("day");
        this.spawn = config.getLocation("spawn", this.spawn);
        this.spawnProtectionradius = config.getInt("spawnProtectionRadius");
        this.islandCreatedCount = config.getInt("islandCreatedCount", this.islandCreatedCount);
        this.islandAreaSize = config.getInt("islandAreaSize", this.islandAreaSize);
        this.islandNameMaxLength = config.getInt("islandNameMaxLength", this.islandNameMaxLength);
        this.firstLevelXp = config.getInt("firstLevelXp", this.firstLevelXp);
        this.xpMultiplier = config.getDouble("xpMultiplier", this.xpMultiplier);

        ConfigurationSection generationChanceSection = config.getConfigurationSection("generationChance");
        this.coalGenerationChance = generationChanceSection.getDouble("coal", this.coalGenerationChance);
        this.ironGenerationChance = generationChanceSection.getDouble("iron", this.ironGenerationChance);
        this.goldGenerationChance = generationChanceSection.getDouble("gold", this.goldGenerationChance);
        this.diamondGenerationChance = generationChanceSection.getDouble("diamond", this.diamondGenerationChance);

        ConfigurationSection eventsSection = config.getConfigurationSection("events");
        ConfigurationSection worldEventSection = eventsSection.getConfigurationSection("worldEvent");
        this.worldEventInitTime = worldEventSection.getInt("initTime");
        this.worldEventTime = worldEventSection.getInt("time");
        ConfigurationSection bossEventSection = eventsSection.getConfigurationSection("bossEvent");
        this.bossEventInitTime = bossEventSection.getInt("initTime");
        this.bossEventInitTime = bossEventSection.getInt("time");
        this.bossEventSpawn = bossEventSection.getLocation("spawn");

        ConfigurationSection islandsSection = config.getConfigurationSection("islands");
        for (String islandName : islandsSection.getKeys(false)) {
            ConfigurationSection islandSection = islandsSection.getConfigurationSection(islandName);
            islands.add(new Island(islandName, islandSection.getLocation("spawn"), islandSection.getLocation("home")));
        }

        ConfigurationSection playersSection = config.getConfigurationSection("players");
        for (String uuidStr : playersSection.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            ConfigurationSection playerSection = playersSection.getConfigurationSection(uuidStr);
            SkyBlockPlayer sbPlayer = new SkyBlockPlayer(uuid, this.islands.stream().filter(island -> island.getName().equals(playerSection.get("island"))).findFirst().orElse(null), playerSection.getLong("xp"));
            sbPlayers.add(sbPlayer);
            ConfigurationSection completedChallengesSection = playerSection.getConfigurationSection("completedChallenges");
            for (String idStr : completedChallengesSection.getKeys(false)) {
                int id = Integer.parseInt(idStr);
                sbPlayer.getCompletedChallenges().put(Game.challenges.stream().filter(challenge -> challenge.getId() == id).findFirst().get(), completedChallengesSection.getInt(idStr));
            }
        }
        this.scheduleSave();
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        config.set("day", this.day);
        config.set("spawn", this.spawn);
        config.set("spawnProtectionRadius", this.spawnProtectionradius);
        config.set("islandCreatedCount", this.islandCreatedCount);
        config.set("islandAreaSize", this.islandAreaSize);
        config.set("islandNameMaxLength", this.islandNameMaxLength);
        config.set("firstLevelXp", this.firstLevelXp);
        config.set("xpMultiplier", this.xpMultiplier);

        ConfigurationSection generationChanceSection = config.createSection("generationChance");
        generationChanceSection.set("coal", this.coalGenerationChance);
        generationChanceSection.set("iron", this.ironGenerationChance);
        generationChanceSection.set("gold", this.goldGenerationChance);
        generationChanceSection.set("diamond", this.diamondGenerationChance);

        ConfigurationSection eventsSection = config.createSection("events");
        ConfigurationSection worldEventSection = eventsSection.createSection("worldEvent");
        worldEventSection.set("initTime", this.worldEventInitTime);
        worldEventSection.set("time", this.worldEventTime);
        ConfigurationSection bossEventSection = eventsSection.createSection("bossEvent");
        bossEventSection.set("initTime", this.bossEventInitTime);
        bossEventSection.set("time", this.bossEventTime);
        bossEventSection.set("spawn", this.bossEventSpawn);

        ConfigurationSection islandsSection = config.createSection("islands");
        for (Island island : this.islands) {
            ConfigurationSection islandSection = islandsSection.createSection(island.getName());
            islandSection.set("spawn", island.getSpawn());
            islandSection.set("home", island.getHome());
        }

        ConfigurationSection playersSection = config.createSection("players");
        for (SkyBlockPlayer sbPlayer : this.sbPlayers) {
            ConfigurationSection playerSection = playersSection.createSection(sbPlayer.getUUID().toString());
            if (sbPlayer.getIsland() != null) {
                playerSection.set("island", sbPlayer.getIsland().getName());
            }
            playerSection.set("xp", sbPlayer.getXp());
            ConfigurationSection completedChallengesSection = playerSection.createSection("completedChallenges");
            for (Challenge challenge : sbPlayer.getCompletedChallenges().keySet()) {
                completedChallengesSection.set(String.valueOf(challenge.getId()), sbPlayer.getCompletedChallenges().get(challenge));
            }
        }
        try {
            config.save(this.configFile);
        } catch (IOException ex) {
            Plugin.log("Could not create the configuration file : " + ex.getMessage());
        }
    }

    private void scheduleSave() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin.getPlugin(), () -> {
            this.save();
        }, 0L, 10L * 10L * 20L);
    }
}
