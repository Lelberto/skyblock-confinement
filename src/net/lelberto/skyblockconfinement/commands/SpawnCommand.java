package net.lelberto.skyblockconfinement.commands;

import net.lelberto.skyblockconfinement.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location playerLocation = player.getLocation();
            player.sendMessage(Plugin.getPluginPrefix() + "Téléportation au spawn dans 5 secondes, ne bougez pas");
            Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getPlugin(), () -> {
                if (player.getLocation().distance(playerLocation) <= 1) {
                    player.teleport(Plugin.getConfiguration().spawn);
                } else {
                    player.sendMessage(Plugin.getPluginPrefix() + "§cTéléportation annulée");
                }
            }, 5L * 20L);
            return true;
        } else {
            sender.sendMessage(Plugin.getPluginPrefix() + "§cCette commande est uniquement utilisable par les joueurs");
            return false;
        }
    }
}
