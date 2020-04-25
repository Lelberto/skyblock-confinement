package net.lelberto.skyblockconfinement.commands;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
        if (args.length >= 1) {
            Island island = sbPlayer.getIsland();
            if (island != null) {
                StringBuilder sb = new StringBuilder();
                for (String arg : args) {
                    sb.append(arg + " ");
                }
                island.sendMessage("§6" + player.getName() + " §7> §5§o" + sb.toString().trim());
                return true;
            } else {
                player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                return false;
            }
        } else {
            return false;
        }
    }
}
