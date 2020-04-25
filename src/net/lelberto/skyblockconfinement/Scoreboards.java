package net.lelberto.skyblockconfinement;

import net.lelberto.skyblockconfinement.events.Event;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Scoreboards {

    private static Scoreboards INSTANCE;

    public static Scoreboards getInstance() {
        if (Scoreboards.INSTANCE == null) {
            Scoreboards.INSTANCE = new Scoreboards();
        }
        return Scoreboards.INSTANCE;
    }

    private Scoreboards() {}

    public void createScordboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public void refreshBoards() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        for (Player player : Bukkit.getOnlinePlayers()) {
            int position = 10;
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            Scoreboard board = player.getScoreboard();
            Objective infosObjective = board.getObjective("infos");
            if (infosObjective == null) {
                infosObjective = board.registerNewObjective("infos", "dummy", "§dSkyBlock§bConfinement");
                infosObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
            }
            board.getEntries().forEach(board::resetScores);
            Score playerScore = infosObjective.getScore("§c➤§r §6§L" + player.getName());
            playerScore.setScore(position--);
            Score levelScore = infosObjective.getScore("§eNiveau§r: §a" + sbPlayer.getLevel());
            levelScore.setScore(position--);
            Score xpScore = infosObjective.getScore("§eXP§r: §a" + sbPlayer.getXp() + "§r/§a" + sbPlayer.getNextLevelXp());
            xpScore.setScore(position--);
            Score levelProgressScore = infosObjective.getScore("§eProgression§r: §a" + this.createLevelProgressBar(sbPlayer));
            levelProgressScore.setScore(position--);

            Island island = sbPlayer.getIsland();
            if (island != null) {
                Score separatorScore = infosObjective.getScore(" ");
                separatorScore.setScore(position--);
                Score islandScore = infosObjective.getScore("§c➤§r §6§L" + island.getName());
                islandScore.setScore(position--);
                Score ownersScore = infosObjective.getScore("§ePropriétaires§r: §a" + island.getOwners().stream().filter(owner -> owner.toBukkitPlayer().isOnline()).collect(Collectors.toList()).size() + "§r/§a" + island.getOwners().size());
                ownersScore.setScore(position--);
                int blockCount = island.getBlockCount();
                Score blocksScore = infosObjective.getScore("§eTaille§r: " + (blockCount == 0 ? "§cChargement..." : "§a" + blockCount + " blocks"));
                blocksScore.setScore(position--);
            }

            Event e = Game.getCurrentEvent();
            if (e != null) {
                Score separatorScore = infosObjective.getScore("  ");
                separatorScore.setScore(position--);
                Score eventScore = infosObjective.getScore("§c➤§r §6§L" + e.getName());
                eventScore.setScore(position--);
                Score statusScore;
                switch (e.getStatus()) {
                    default:
                        statusScore = infosObjective.getScore("§c???");
                        break;
                    case INIT:
                        int initTime = e.getInitTime();
                        statusScore = infosObjective.getScore("§eDébut dans §a" + (initTime > 30 ? "§a" : "§c") + Game.formatTime(initTime));
                        break;
                    case STARTED:
                        int time = e.getTime();
                        statusScore = infosObjective.getScore("§eFin dans §a" + (time > 60 ? "§a" : "§c") + Game.formatTime(time));
                        break;
                    case FINISHED:
                        statusScore = infosObjective.getScore("§cTerminé");
                        break;
                }
                statusScore.setScore(position--);
            }
        }
    }

    private String createLevelProgressBar(SkyBlockPlayer sbPlayer) {
        int barSize = 10;
        long totalXp = sbPlayer.getNextLevelXp() - sbPlayer.getCurrentLevelXp();
        long obtainedXp = totalXp - (sbPlayer.getNextLevelXp() - sbPlayer.getXp());
        long percent = (obtainedXp * 100) / totalXp;
        long filledCount = (obtainedXp * barSize) / totalXp;
        String bar = "";
        for (int i = 1; i <= barSize; i++) {
            if (i <= filledCount) {
                bar += "●";
            } else {
                bar += "◌";
            }
        }
        return bar + ' ' + percent + "%";
    }
}
