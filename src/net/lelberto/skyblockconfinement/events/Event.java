package net.lelberto.skyblockconfinement.events;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.concurrent.TimeUnit;

public abstract class Event {

    protected final String name;
    protected int initTime;
    protected int time;
    protected Status status;

    public Event(String name, int initTime, int time) {
        this.name = name;
        this.initTime = initTime;
        this.time = time;
        this.status = Status.NULL;
    }

    public final void init() {
        this.status = Status.INIT;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("§b§l" + this.name, "§eL'évènement va commencer", 10, 5 * 20, 2 * 20);
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, SoundCategory.AMBIENT, 1, 0.0F);
        });
        int initTime = this.initTime;
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
            if (this.initTime == initTime) {
                this.runInit();
            } else if (this.initTime <= 0) {
                this.start();
                task.cancel();
            }
            this.initTime--;
        }, 0L, 20L);
    }

    public final void start() {
        this.status = Status.STARTED;
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("§b§l" + this.name, "§eL'évènement commence", 10, 5 * 20, 2 * 20);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, SoundCategory.AMBIENT, 1, 0.0F);
        });
        int time = this.time;
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
            if (this.time == time) {
                this.run();
            } else if (this.time <= 0) {
                this.stop();
                task.cancel();
            }
            this.time--;
        }, 0L, 20L);
    }

    public final void stop() {
        this.status = Status.FINISHED;
        this.runStop();
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle("§b§l" + this.name, "§eL'évènement est terminé", 10, 5 * 20, 2 * 20);
            player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, SoundCategory.AMBIENT, 1, 0.0F);
        });
        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getPlugin(), () -> {
            this.status = Status.NULL;
            Game.resetCurrentEvent();
        }, 60L * 20L);
    }

    public abstract void runInit();

    public abstract void run();

    public abstract void runStop();

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public int getInitTime() {
        return initTime;
    }

    public int getTime() {
        return time;
    }



    public static enum Status {
        NULL, INIT, STARTED, FINISHED;

        Status() {}
    }
}
