package net.lelberto.skyblockconfinement.game;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.challenges.*;
import net.lelberto.skyblockconfinement.events.Event;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Game {

    public static List<Challenge> challenges = new ArrayList<>();
    private static Event currentEvent = null;

    public static void initChallenges() {
        challenges.addAll(Arrays.asList(
                // Basic category
                new CobblestoneGeneratorChallenge(),
                new SpruceUpgradeChallenge(),
                new BirchUpgradeChallenge(),
                new DarkOakUpgradeChallenge(),
                new AcaciaUpgradeChallenge(),
                new JungleUpgradeChallenge(),
                new IceSandChallenge(),
                new MulticolorChallenge(),
                // farming category
                new BakerChallenge(),
                new HalloweenChallenge(),
                new BeetrootsChallenge(),
                // Combat category
                new HyperHunterChallenge(),
                // Nether category
                new PlayerHeadChallenge()
        ));
    }

    public static void startEvent(Event event) {
        if (Game.currentEvent == null) {
            Game.currentEvent = event;
            Game.currentEvent.init();
        }
    }

    public static void resetCurrentEvent() {
        Game.currentEvent = null;
    }

    public static Event getCurrentEvent() {
        return Game.currentEvent;
    }

    public static void startCountIslandsBlocks() {
        Plugin.getConfiguration().islands.forEach(Island::countBlocks); // First time : counting all islands
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getPlugin(), () -> {
            Plugin.getConfiguration().islands.forEach(island -> {
                if (!island.isCountingBlocksRunning() && island.getOwners().stream().map(sbOwner -> sbOwner.toBukkitPlayer()).filter(offPlayer -> offPlayer.isOnline()).collect(Collectors.toList()).size() > 0) {
                    island.countBlocks();
                }
            });
        }, 10L * 60L * 20L,  3L * 60L * 20L);
    }

    public static void startDayCounter() {
        Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), () -> {
            if (Bukkit.getWorlds().get(0).getTime() < 20L) {
                Plugin.getConfiguration().day++;
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.sendTitle("§bJour §e" + Plugin.getConfiguration().day, null, 2 * 20, 5 * 20, 2 * 20);
                    player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.AMBIENT, 1.0F, 0.6F);
                });
            }
        }, 0L, 20L);
    }

    public static Location getNextIslandSpawn() {
        int distance = Plugin.getConfiguration().islandAreaSize * 2;
        return new Location(Bukkit.getWorlds().get((0)), distance * Plugin.getConfiguration().islandCreatedCount + 1000, 100, 0);
    }

    public static void startScoreboardRefresh() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Plugin.getPlugin(), () -> {
            Plugin.getScoreboards().refreshBoards();
        }, 0L, 20L);
    }

    public static Firework spawnRandomFirework(Location location) {
        return Game.spawnRandomFirework(location, 1);
    }

    public static Firework spawnRandomFirework(Location location, int effectCount) {
        Random rand = new Random();
        Firework firework = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        for (int i = 0; i < effectCount; i++) {
            FireworkEffect.Type type;
            switch (rand.nextInt(4)) {
                default:
                case 0:
                    type = FireworkEffect.Type.BALL;
                    break;
                case 1:
                    type = FireworkEffect.Type.BALL_LARGE;
                    break;
                case 2:
                    type = FireworkEffect.Type.BURST;
                    break;
                case 3:
                    type = FireworkEffect.Type.CREEPER;
                    break;
                case 4:
                    type = FireworkEffect.Type.STAR;
                    break;
            }
            Color color = Color.fromRGB(rand.nextInt(254) + 1, rand.nextInt(254) + 1, rand.nextInt(254) + 1);
            Color fade = Color.fromRGB(rand.nextInt(254) + 1, rand.nextInt(254) + 1, rand.nextInt(254) + 1);
            FireworkEffect fireworkEffect = FireworkEffect.builder().with(type).withColor(color).withFade(fade).flicker(rand.nextBoolean()).trail(rand.nextBoolean()).build();
            fireworkMeta.addEffect(fireworkEffect);
        }
        fireworkMeta.setPower(rand.nextInt(2) + 1);
        firework.setFireworkMeta(fireworkMeta);
        return firework;
    }

    public static String formatTime(int time) {
        long minutes = TimeUnit.SECONDS.toMinutes(time);
        long seconds = time - TimeUnit.MINUTES.toSeconds(minutes);
        return String.format("%02d:%02d", minutes, seconds);
    }

    public static void refreshPlayerList(Player player, SkyBlockPlayer sbPlayer) {
        Island island = sbPlayer.getIsland();
        player.setPlayerListHeaderFooter("§7---------- §dSkyBlock§bConfinement §7----------", "§7---------------------------------------");
        String islandNameInList = (island != null) ? "§e" + island.getName() + "§r - " : "";
        String playerNameInList = "§6" + player.getName() + " §7(§a" + sbPlayer.getLevel() + "§7)";
        player.setPlayerListName(islandNameInList + playerNameInList);
    }

    public static TextComponent getLeaderboardMessage() {
        TextComponent leaderboardText = new TextComponent(Plugin.getPluginPrefix() + "§bLeaderboard");
        List<Island> islands = Plugin.getConfiguration().islands.stream().filter(island -> island.getBlockCount() > 0).sorted((island1, island2) -> {
            int blocks1 = island1.getBlockCount();
            int blocks2 = island2.getBlockCount();
            if (blocks1 < blocks2) {
                return 1;
            }
            if (blocks1 > blocks2) {
                return -1;
            }
            return 0;
        }).limit(10L).collect(Collectors.toList());
        if (islands.size() > 0) {
            int position = 1;
            for (Island island : islands) {
                String positionColor;
                switch (position) {
                    case 1:
                        positionColor = "§6§l";
                        break;
                    case 2:
                    case 3:
                        positionColor = "§6";
                        break;
                    default:
                        positionColor = "§7";
                        break;
                }
                String ownersStr = "Propriétaires :";
                for (SkyBlockPlayer sbOwner : island.getOwners()) {
                    ownersStr += "\n§6" + sbOwner.toBukkitPlayer().getName();
                }
                TextComponent islandText = new TextComponent("\n" + positionColor + position + "§r - §e" + island.getName() + " §r(§a" + island.getBlockCount() + " blocks§r)");
                TextComponent islandHoverText = new TextComponent(ownersStr);
                HoverEvent islandHoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{islandHoverText});
                islandText.setHoverEvent(islandHoverEvent);
                leaderboardText.addExtra(islandText);
                position++;
            }
        } else {
            leaderboardText.addExtra(new TextComponent("\n§cAucune île n'est dans le leaderboard pour le moment"));
        }
        return leaderboardText;
    }

    public static TextComponent getStatisticsMessage(Island island) {
        Map<Material, Integer> blocks = island.getBlocks();
        TextComponent statsText = new TextComponent(Plugin.getPluginPrefix() + "§dStatistiques de l'île \"§e" + island.getName() + "§d\"");
        String ownersStr = "\n§ePropriétaires :";
        for (SkyBlockPlayer sbOwner : island.getOwners()) {
            ownersStr += "\n- §6" + sbOwner.toBukkitPlayer().getName() + " §7(§a" + sbOwner.getLevel() + "§7)";
        }
        TextComponent ownersText = new TextComponent(ownersStr);
        String blocksStr = "\n§eBlocks :";
        if (!blocks.isEmpty()) {
            for (Material material : blocks.keySet().stream().sorted((material1, material2) -> {
                int count1 = blocks.get(material1);
                int count2 = blocks.get(material2);
                if (count1 < count2) {
                    return 1;
                }
                if (count1 > count2) {
                    return -1;
                }
                return 0;
            }).limit(10L).collect(Collectors.toList())) {
                blocksStr += "\n- §9" + material.name() + "§r: x§a" + blocks.get(material);
            }
        } else {
            blocksStr += " §cChargement...";
        }
        TextComponent blocksText = new TextComponent(blocksStr);
        statsText.addExtra(ownersText);
        statsText.addExtra(blocksText);

        return statsText;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onDamage(EntityDamageEvent e) {
            Entity entity = e.getEntity();
            if (entity.getWorld().equals(Plugin.getDefaultWorld()) && e.getEntityType().equals(EntityType.PLAYER) && entity.getLocation().distance(Plugin.getConfiguration().spawn) <= Plugin.getConfiguration().spawnProtectionradius) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent e) {
            Player player = e.getPlayer();
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            Block block = e.getBlock();
            Island island = sbPlayer.getIsland();
            if (player.getWorld().equals(Plugin.getDefaultWorld()) && !player.isOp() && (island == null || block.getLocation().distance(island.getSpawn()) > Plugin.getConfiguration().islandAreaSize)) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e) {
            Player player = e.getPlayer();
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            Block block = e.getBlock();
            Island island = sbPlayer.getIsland();
            if (player.getWorld().equals(Plugin.getDefaultWorld()) && !player.isOp() && (island == null || block.getLocation().distance(island.getSpawn()) > Plugin.getConfiguration().islandAreaSize)) {
                e.setCancelled(true);
            }
        }

        @EventHandler
        public void onCobbleStoneGenerate(BlockFromToEvent e) {
            Material sourceBlockType = e.getBlock().getType();
            Block targetBlock = e.getToBlock();
            if ((sourceBlockType == Material.LAVA || sourceBlockType == Material.WATER) && (targetBlock.getType() == Material.AIR || targetBlock.getType() == Material.LAVA || targetBlock.getType() == Material.WATER)) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(Plugin.getPlugin(), () -> {
                    if (targetBlock.getWorld().getBlockAt(targetBlock.getLocation()).getType() == Material.COBBLESTONE) {
                        e.setCancelled(true);
                        Island nearIsland = Island.getNearIsland(targetBlock.getLocation());
                        double islandDiff = nearIsland != null ? nearIsland.getBlockCount() / 100000.0 : 0.0;
                        if (islandDiff > 0.3) {
                            islandDiff = 0.3;
                        }
                        Random rand = new Random();
                        switch (rand.nextInt(4)) {
                            case 0:
                                if (rand.nextDouble() <= (Plugin.getConfiguration().coalGenerationChance / 100.0) + islandDiff) {
                                    targetBlock.setType(Material.COAL_ORE);
                                }
                                break;
                            case 1:
                                if (rand.nextDouble() <= (Plugin.getConfiguration().ironGenerationChance / 100.0) + islandDiff) {
                                    targetBlock.setType(Material.IRON_ORE);
                                }
                                break;
                            case 2:
                                if (rand.nextDouble() <= (Plugin.getConfiguration().goldGenerationChance / 100.0) + islandDiff) {
                                    targetBlock.setType(Material.GOLD_ORE);
                                }
                                break;
                            case 3:
                                if (rand.nextDouble() <= (Plugin.getConfiguration().diamondGenerationChance / 100.0) + islandDiff) {
                                    targetBlock.setType(Material.DIAMOND_ORE);
                                }
                                break;
                            default: break;
                        }
                    }
                }, 1L);
            }
        }
    }
}
