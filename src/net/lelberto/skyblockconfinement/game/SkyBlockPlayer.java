package net.lelberto.skyblockconfinement.game;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.challenges.Challenge;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.*;

public class SkyBlockPlayer {

    public static SkyBlockPlayer createPlayer(OfflinePlayer player) {
        SkyBlockPlayer sbPlayer = new SkyBlockPlayer(player.getUniqueId());
        Plugin.getConfiguration().sbPlayers.add(sbPlayer);
        return sbPlayer;
    }

    public static SkyBlockPlayer getPlayer(UUID uuid) {
        return Plugin.getConfiguration().sbPlayers.stream().filter(sbPlayer -> sbPlayer.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public static SkyBlockPlayer getPlayer(OfflinePlayer player) {
        if (player != null) {
            return SkyBlockPlayer.getPlayer(player.getUniqueId());
        }
        return null;
    }

    private final UUID uuid;
    private Island island;
    private long xp;
    private int level;
    private long currentLevelXp;
    private long nextLevelXp;
    private Map<Challenge, Integer> completedChallenges;

    public SkyBlockPlayer(UUID uuid) {
        this(uuid, null, 0);
    }

    public SkyBlockPlayer(UUID uuid, Island island, long xp) {
        this.uuid = uuid;
        this.island = island;
        this.xp = xp;
        this.level = 0;
        this.currentLevelXp = 0;
        this.nextLevelXp = 0;
        this.completedChallenges = new HashMap<>();
        this.updateLevelInternal(false); // Level and XP initialization
    }

    public void updateLevel() {
        this.updateLevelInternal(true);
    }

    private void updateLevelInternal(boolean callEvent) {
        long xp = Plugin.getConfiguration().firstLevelXp;
        int level = 0;
        long currentLevelXp = 0;
        while (xp <= this.xp) {
            currentLevelXp = xp;
            xp *= Plugin.getConfiguration().xpMultiplier;
            level++;
        }
        int currentLevel = this.level;
        this.level = level;
        this.currentLevelXp = currentLevelXp;
        this.nextLevelXp = xp;
        if (callEvent && currentLevel != level) {
            Bukkit.getPluginManager().callEvent(new LevelChangeEvent(this, level));
        }
    }

    public boolean isInIsland() {
        if (this.island != null) {
            Player player = (Player) this.toBukkitPlayer();
            Location playerLocation = player.getLocation();
            Location islandSpawnPoint = island.getSpawn();
            if (playerLocation.distance(islandSpawnPoint) <= Plugin.getConfiguration().islandAreaSize / 2) {
                return true;
            } else if (playerLocation.distance(islandSpawnPoint) > Plugin.getConfiguration().islandAreaSize / 2) {
                return false;
            }
        }
        return false;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public Island getIsland() {
        return this.island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public long getXp() {
        return xp;
    }

    public void setXp(long xp) {
        this.xp = xp;
        this.updateLevel();
    }

    public void addXp(int xp) {
        OfflinePlayer offPlayer = this.toBukkitPlayer();
        if (offPlayer.isOnline()) {
            offPlayer.getPlayer().sendMessage(Plugin.getPluginPrefix() + "+§a" + xp + "§r points d'expérience");
        }
        this.setXp(this.xp + xp);
    }

    public int getLevel() {
        return this.level;
    }

    public long getCurrentLevelXp() {
        return currentLevelXp;
    }

    public long getNextLevelXp() {
        return nextLevelXp;
    }

    public Map<Challenge, Integer> getCompletedChallenges() {
        return completedChallenges;
    }

    public boolean hasCompletedChallenge(Challenge challenge) {
        return this.completedChallenges.containsKey(challenge);
    }

    public boolean hasCompletedChallenge(int id) {
        return this.hasCompletedChallenge(Game.challenges.stream().filter(challenge -> challenge.getId() == id).findFirst().orElse(null));
    }

    public OfflinePlayer toBukkitPlayer() {
        return Bukkit.getOfflinePlayer(uuid);
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            Player player = e.getPlayer();
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            if (sbPlayer == null) {
                sbPlayer = SkyBlockPlayer.createPlayer(player);
                player.teleport(Plugin.getConfiguration().spawn);
                e.setJoinMessage("§6" + player.getName() + " §avient de rejoindre le Skyblock Confinement ! §5§kl§2§kl§b§kl");
            } else {
                e.setJoinMessage("§7[§a+§7]§r §6" + player.getName());
            }
            Plugin.getScoreboards().createScordboard(player);
            Game.refreshPlayerList(player, sbPlayer);
        }

        @EventHandler
        public void onQuit(PlayerQuitEvent e) {
            Player player = e.getPlayer();
            e.setQuitMessage("§7[§c-§7]§r §6" + player.getName());
        }

        @EventHandler
        public void onDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();
            EntityDamageEvent.DamageCause cause = player.getLastDamageCause().getCause();
            String msg;
            switch (cause) {
                case VOID:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cest tombé(e) plus bas que terre",
                            "§6" + player.getName() + " §ca glissé",
                            "§6" + player.getName() + " §ctombe à l'infini",
                            "§6" + player.getName() + " §ca disparu"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case LAVA:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca essayé de nager dans la lave",
                            "§6" + player.getName() + " §cs'est noyé(e) dans la lave",
                            "§6" + player.getName() + " §ca fondu"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case BLOCK_EXPLOSION:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca explosé",
                            "§6" + player.getName() + " §ca pété"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case CRAMMING:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cs'est étouffé",
                            "§6" + player.getName() + " §ca choppé le COVID-19 à cause de son non-respect des distances de sécurité"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case DRAGON_BREATH:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cs'est fait(e) soufflé le cul"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case DROWNING:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cs'est noyé(e)",
                            "§6" + player.getName() + " §ca oublié de respirer",
                            "§6" + player.getName() + " §ca cru qu'il/elle avait des branchies"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case WITHER:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca été witherifié",
                            "§6" + player.getName() + " §cs'est fait soulever par le Wither"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case FIRE:
                case FIRE_TICK:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca brûlé",
                            "§6" + player.getName() + " §ca fondu",
                            "§6" + player.getName() + " §cest devenu(e) juif(ve)",
                            "§6" + player.getName() + " §ca trop joué avec le feu"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case STARVATION:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cest mort(e) de faim",
                            "§6" + player.getName() + " §cest victime de la famine",
                            "§6" + player.getName() + " §ca oublié de manger"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case SUFFOCATION:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca suffoqué",
                            "§6" + player.getName() + " §cne pouvait plus respirer"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case LIGHTNING:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §ca prit la foudre",
                            "§6" + player.getName() + " §cs'est fait éléctrocuté(e)",
                            "§6" + player.getName() + " §cs'est fait foudroyé(e)",
                            "§6" + player.getName() + " §ca prit une tonne de volts dans la gueule"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                case FALL:
                    msg = Arrays.asList(
                            "§6" + player.getName() + " §cest tombé",
                            "§6" + player.getName() + " §ca brisé ses jambes",
                            "§6" + player.getName() + " §ca glissé",
                            "§6" + player.getName() + " §cs'est suicidé(e)",
                            "§6" + player.getName() + " §ca oublié qu'il/elle était en hauteur"
                    ).stream().sorted((a, b) -> new Random().nextInt(3) - 1).findFirst().get();
                    break;
                default: msg = "§c" + e.getDeathMessage();
            }
            e.setDeathMessage("§4☠§r " + msg);
        }

        @EventHandler
        public void onRespawn(PlayerRespawnEvent e) {
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(e.getPlayer());
            Island island = sbPlayer.getIsland();
            e.setRespawnLocation(island != null ? island.getHome() : Plugin.getConfiguration().spawn);
        }

        @EventHandler
        public void onChat(AsyncPlayerChatEvent e) {
            Player player = e.getPlayer();
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            e.setFormat("§7(§a" + sbPlayer.getLevel() + "§7) §6" + player.getName() + " §7> §r" + e.getMessage());
        }

        @EventHandler
        public void onLevelChange(LevelChangeEvent e) {
            SkyBlockPlayer sbPlayer = e.getSkyBlockPlayer();
            OfflinePlayer offPlayer = sbPlayer.toBukkitPlayer();
            if (offPlayer.isOnline()) {
                Player player = offPlayer.getPlayer();
                Game.spawnRandomFirework(player.getLocation());
                Game.refreshPlayerList(player, sbPlayer);
            }
            Bukkit.broadcastMessage(Plugin.getPluginPrefix() + "§6" + sbPlayer.toBukkitPlayer().getName() + "§r vient de passer au niveau §a" + e.getLevel());
        }
    }



    public static class LevelChangeEvent extends Event {

        private static final HandlerList HANDLERS = new HandlerList();

        public static HandlerList getHandlerList() {
            return LevelChangeEvent.HANDLERS;
        }

        private final SkyBlockPlayer sbPlayer;
        private final int level;

        public LevelChangeEvent(SkyBlockPlayer sbPlayer, int level) {
            this.sbPlayer = sbPlayer;
            this.level = level;
        }

        @Override
        public HandlerList getHandlers() {
            return LevelChangeEvent.HANDLERS;
        }

        public SkyBlockPlayer getSkyBlockPlayer() {
            return sbPlayer;
        }

        public int getLevel() {
            return level;
        }
    }
}
