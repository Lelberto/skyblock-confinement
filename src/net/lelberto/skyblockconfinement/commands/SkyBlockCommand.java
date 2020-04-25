package net.lelberto.skyblockconfinement.commands;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.SkyblockException;
import net.lelberto.skyblockconfinement.challenges.Challenge;
import net.lelberto.skyblockconfinement.events.BossEvent;
import net.lelberto.skyblockconfinement.events.WorldEvent;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import net.lelberto.skyblockconfinement.inventories.ChallengeCategoriesInventory;
import net.lelberto.skyblockconfinement.inventories.ChallengeDetailsInventory;
import net.lelberto.skyblockconfinement.inventories.IslandInventory;
import net.lelberto.skyblockconfinement.inventories.SkyblockInventory;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

public class SkyBlockCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
        if (args.length == 0) {
            player.openInventory(new SkyblockInventory(sbPlayer).create());
            return true;
        } else {
            switch (args[0]) {
                default:
                    player.openInventory(new SkyblockInventory(sbPlayer).create());
                    return true;
                case "load": // Load plugin configuration
                    Plugin.getConfiguration().load();
                    Bukkit.broadcastMessage("Configuration loaded");
                    return true;
                case "save": // Save plugin configuration
                    Plugin.getConfiguration().save();
                    Bukkit.broadcastMessage("Configuration saved");
                    return true;
                case "island": // Island command
                case "i":
                    return this.processIslandCommand(player, sbPlayer, args);
                case "challenge":
                case "c":
                    return this.processChallengeCommand(player, sbPlayer, args);
                case "leaderboard":
                case "l":
                    player.spigot().sendMessage(Game.getLeaderboardMessage());
                    return true;
                case "stat":
                case "stats":
                case "s":
                    if (args.length == 1) {
                        Island island = SkyBlockPlayer.getPlayer(player).getIsland();
                        if (island != null) {
                            player.spigot().sendMessage(Game.getStatisticsMessage(island));
                            return true;
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cVous devez avoir une île pour visualiser ses statistiques");
                            return false;
                        }
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 1; i < args.length; i++) {
                            sb.append(args[i]).append(' ');
                        }
                        String name = sb.toString().trim();
                        Island island = Plugin.getConfiguration().islands.stream().filter(currentIsland -> currentIsland.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                        if (island != null) {
                            player.spigot().sendMessage(Game.getStatisticsMessage(island));
                            return true;
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cAucune île ne se nomme \"§e" + name + "§c\"");
                            return false;
                        }
                    }
                case "test":
                    Game.startEvent(new BossEvent(10, Integer.parseInt(args[1])));
                    return true;
            }
        }
    }

    private boolean processIslandCommand(Player player, SkyBlockPlayer sbPlayer, String[] args) {
        Island island = sbPlayer.getIsland();
        if (args.length == 1 && island != null) {
            player.openInventory(new IslandInventory().create());
            return true;
        } else {
            switch (args[1]) {
                default: return false;
                case "create":
                case "c":
                    if (island == null) {
                        String name = "Île de " + player.getName();
                        if (args.length >= 3) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]).append(' ');
                            }
                            name = sb.toString();
                        }
                        Location islandSpawn = Game.getNextIslandSpawn();
                        island = new Island(name, islandSpawn, islandSpawn.clone());
                        try {
                            island.create();
                            sbPlayer.setIsland(island);
                            player.teleport(islandSpawn);
                            Plugin.getConfiguration().islands.add(island);
                            Plugin.getConfiguration().islandCreatedCount++;
                            player.sendMessage(Plugin.getPluginPrefix() + "Bienvenue sur votre nouvelle île !");
                            if (sbPlayer.getLevel() == 0) {
                                sbPlayer.addXp(Plugin.getConfiguration().firstLevelXp);
                            }
                            Game.refreshPlayerList(player, sbPlayer);
                            return true;
                        } catch (SkyblockException ex) {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cImpossible de créer une île");
                            Plugin.log("Could not create island : " + ex.getMessage());
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous avez déjà une île");
                        return false;
                    }
                case "home":
                    if (island != null) {
                        Location playerLocation = player.getLocation();
                        player.sendMessage(Plugin.getPluginPrefix() + "Téléportation à votre île dans 5 secondes, ne bougez pas");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getPlugin(), () -> {
                            if (player.getLocation().distance(playerLocation) <= 1) {
                                player.teleport(sbPlayer.getIsland().getHome());
                            } else {
                                player.sendMessage(Plugin.getPluginPrefix() + "§cTéléportation annulée");
                            }
                        }, 5L * 20L);
                        return true;
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                        return false;
                    }
                case "sethome":
                    if (island != null) {
                        Location home = player.getLocation();
                        Bukkit.broadcastMessage((home.getBlock().getType() == Material.AIR) + "");
                        if (home.distance(island.getSpawn()) < Plugin.getConfiguration().islandAreaSize / 2) {
                            island.setHome(player.getLocation());
                            island.sendMessage("Le home a été redéfinit par §6" + player.getName() + "§r aux coordonnées (§a" + home.getBlockX() + "§r, §a" + home.getBlockY() + "§r, §a" + home.getBlockZ() + "§r)");
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cImpossible de redéfinir votre home ici, vous êtes trop loin du spawn de votre île");
                        }
                        return true;
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                        return false;
                    }
                case "name":
                case "rename": // Rename island
                    if (island != null) {
                        if (args.length >= 3) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 2; i < args.length; i++) {
                                sb.append(args[i]).append(' ');
                            }
                            String name = sb.toString().trim();
                            if (name.length() <= Plugin.getConfiguration().islandNameMaxLength) {
                                if (Plugin.getConfiguration().islands.stream().filter(currentIsland -> currentIsland.getName().equalsIgnoreCase(name)).count() == 0L) {
                                    island.setName(name);
                                    island.sendMessage("§6" + player.getName() + "§r a renommé l'île \"§a" + island.getName() + "§r\"");
                                    for (SkyBlockPlayer sbOwner : island.getOwners()) {
                                        OfflinePlayer offOwner = sbOwner.toBukkitPlayer();
                                        if (offOwner.isOnline()) {
                                            Game.refreshPlayerList(offOwner.getPlayer(), sbOwner);
                                        }
                                    }
                                    return true;
                                } else {
                                    player.sendMessage(Plugin.getPluginPrefix() + "§cCe nom existe déjà");
                                    return false;
                                }
                            } else {
                                player.sendMessage(Plugin.getPluginPrefix() + "§cLe nom ne doit pas dépasser les " + Plugin.getConfiguration().islandNameMaxLength + " caractères");
                                return false;
                            }
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cVeuillez renseigner un nom");
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                        return false;
                    }
                case "invite":
                    if (island != null) {
                        if (args.length >= 3) {
                            Player target = Bukkit.getOnlinePlayers().stream().filter(currentPlayer -> currentPlayer.getName().equals(args[2])).findFirst().orElse(null);
                            if (target != null && target.isOnline()) {
                                SkyBlockPlayer sbTarget = SkyBlockPlayer.getPlayer(target);
                                if (sbTarget.getIsland() == null) {
                                    island.getPendingInvites().add(sbTarget);
                                    player.sendMessage(Plugin.getPluginPrefix() + "§aInvitation envoyée à §6" + target.getName());
                                    TextComponent msg = new TextComponent(Plugin.getPluginPrefix() + "§6" + player.getName() + "§r vous a invité à rejoindre son île ");
                                    TextComponent acceptText = new TextComponent("§7[§a✔§7]");
                                    TextComponent acceptHover = new TextComponent("Cliquez ici pour accepter");
                                    HoverEvent acceptHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{acceptHover});
                                    ClickEvent acceptClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb island join " + player.getName());
                                    acceptText.setHoverEvent(acceptHoverEvent);
                                    acceptText.setClickEvent(acceptClickEvent);
                                    msg.addExtra(acceptText);
                                    target.spigot().sendMessage(msg);
                                    return true;
                                } else {
                                    player.sendMessage(Plugin.getPluginPrefix() + "§cCe joueur a déjà une île");
                                    return false;
                                }
                            } else {
                                player.sendMessage(Plugin.getPluginPrefix() + "§cCe joueur est déconnecté");
                                return false;
                            }
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cVeuillez entrer le nom du joueur que vous voulez inviter");
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                        return false;
                    }
                case "join": // Accept invitation
                    if (island == null) {
                        if (args.length >= 3) {
                            Player target = Bukkit.getPlayer(args[2]);
                            SkyBlockPlayer sbTarget = SkyBlockPlayer.getPlayer(target);
                            if (sbTarget != null && sbTarget.getIsland() != null) {
                                Island targetIsland = sbTarget.getIsland();
                                if (targetIsland.getPendingInvites().contains(sbPlayer)) {
                                    targetIsland.getPendingInvites().remove(sbPlayer);
                                    sbPlayer.setIsland(targetIsland);
                                    targetIsland.sendMessage("§6" + player.getName() + "§r vient de rejoindre l'île");
                                    if (sbPlayer.getLevel() == 0) {
                                        sbPlayer.addXp(Plugin.getConfiguration().firstLevelXp);
                                    }
                                    Game.refreshPlayerList(player, sbPlayer);
                                    return true;
                                } else {
                                    player.sendMessage(Plugin.getPluginPrefix() + "§cVoous n'avez pas reçu d'invitation de la part de ce joueur");
                                    return false;
                                }
                            } else {
                                player.sendMessage(Plugin.getPluginPrefix() + "§cVoous n'avez pas reçu d'invitation de la part de ce joueur");
                                return false;
                            }
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cVous devez entrer le pseudo du joueur qui vous a invité");
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous avez déjà une île");
                        return false;
                    }
                case "leave": // Leave island
                    if (island != null) {
                        sbPlayer.setIsland(null);
                        Plugin.getConfiguration().islands.remove(island);
                        player.teleport(Plugin.getConfiguration().spawn);
                        player.sendMessage(Plugin.getPluginPrefix() + "Vous avez quitté votre île");
                        island.sendMessage("§6" + player.getName() + "§r a quitté l'île");
                        Game.refreshPlayerList(player, sbPlayer);
                        return true;
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVous n'avez pas d'île");
                        return false;
                    }
            }
        }
    }

    private boolean processChallengeCommand(Player player, SkyBlockPlayer sbPlayer, String[] args) {
        if (args.length == 1) {
            player.openInventory(new ChallengeCategoriesInventory(sbPlayer).create());
            return true;
        } else {
            switch (args[1]) {
                default: return false;
                case "view":
                case "reward":
                case "rewards":
                    if (args.length >= 3) {
                        int id = Integer.parseInt(args[2]);
                        Challenge challenge = Game.challenges.stream().filter(currentChallenge -> currentChallenge.getId() == id).findFirst().orElse(null);
                        if (challenge != null) {
                            player.openInventory(new ChallengeDetailsInventory(player, sbPlayer, challenge).create());
                            return true;
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cIdentifiant de challenge \"§a" + id + "§c\" invalide");
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVeuillez entrez l'identifiant du challenge que vous souhaitez compléter");
                        return false;
                    }
                case "complete":
                case "make":
                case "finish":
                    if (args.length >= 3) {
                        int id = Integer.parseInt(args[2]);
                        Challenge challenge = Game.challenges.stream().filter(currentChallenge -> currentChallenge.getId() == id).findFirst().orElse(null);
                        if (challenge != null) {
                            if (challenge.verify(player, sbPlayer)) {
                                challenge.complete(player, sbPlayer);
                                if (sbPlayer.getCompletedChallenges().get(challenge) == 1) {
                                    TextComponent msg = new TextComponent(Plugin.getPluginPrefix() + "§6" + player.getName() + "§r vient de compléter un challenge:\n");
                                    TextComponent nameText = new TextComponent("§b>>>>>>>>>>§r §e" + challenge.getName() + "§r (" + challenge.getCategory().name + "§r - " + challenge.getDifficulty().name + "§r)");
                                    TextComponent descriptionHover = new TextComponent("§b" + challenge.getDescription().stream().collect(Collectors.joining(" ")) + "§r\n§7§oCliquez pour voir les détails");
                                    HoverEvent descriptionHoverHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] { descriptionHover });
                                    ClickEvent showChallengeClickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sb challenge rewards " + challenge.getId());
                                    nameText.setHoverEvent(descriptionHoverHoverEvent);
                                    nameText.setClickEvent(showChallengeClickEvent);
                                    msg.addExtra(nameText);
                                    Bukkit.spigot().broadcast(msg);
                                }
                                return true;
                            } else {
                                player.sendMessage(Plugin.getPluginPrefix() + "§cVous ne pouvez pas compléter ce challenge");
                                return false;
                            }
                        } else {
                            player.sendMessage(Plugin.getPluginPrefix() + "§cIdentifiant de challenge \"§a" + id + "§c\" invalide");
                            return false;
                        }
                    } else {
                        player.sendMessage(Plugin.getPluginPrefix() + "§cVeuillez entrez l'identifiant du challenge que vous souhaitez compléter");
                        return false;
                    }
            }
        }
    }
}
