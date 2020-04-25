package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MulticolorChallenge extends Challenge {

    public static final int ID = 8;

    private final ItemStack[] requiredItems;

    public MulticolorChallenge() {
        super(MulticolorChallenge.ID, "Multicolor",
                Arrays.asList("§bRécupère tous les colorants différents."),
                ChallengeCategory.BASIC, ChallengeDifficulty.MEDIUM, Material.MAGENTA_DYE, 30, 50);
        this.requiredItems = this.createRequiredItems();
    }

    private ItemStack[] createRequiredItems() {
        return new ItemStack[] {
                new ItemStack(Material.BLACK_DYE),
                new ItemStack(Material.BLUE_DYE),
                new ItemStack(Material.BROWN_DYE),
                new ItemStack(Material.CYAN_DYE),
                new ItemStack(Material.GRAY_DYE),
                new ItemStack(Material.GREEN_DYE),
                new ItemStack(Material.LIGHT_BLUE_DYE),
                new ItemStack(Material.LIGHT_GRAY_DYE),
                new ItemStack(Material.LIME_DYE),
                new ItemStack(Material.MAGENTA_DYE),
                new ItemStack(Material.ORANGE_DYE),
                new ItemStack(Material.PINK_DYE),
                new ItemStack(Material.PURPLE_DYE),
                new ItemStack(Material.RED_DYE),
                new ItemStack(Material.YELLOW_DYE),
                new ItemStack(Material.WHITE_DYE)
        };
    }

    @Override
    public ItemStack[] getRequiredView(Player player, SkyBlockPlayer sbPlayer) {
        return this.requiredItems;
    }

    @Override
    public ItemStack[] getRewardView(Player player, SkyBlockPlayer sbPlayer) {
        return new ItemStack[] { super.createSpecialRewardItem("§bVous pouvez écrire en couleur", "§bsur les panneaux") };
    }

    @Override
    public boolean verify(Player player, SkyBlockPlayer sbPlayer) {
        return super.containsItems(player.getInventory(), this.requiredItems);
    }

    @Override
    public void complete(Player player, SkyBlockPlayer sbPlayer) {
        super.complete(player, sbPlayer);
        super.removeItems(player.getInventory(), this.requiredItems);
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onSignWrite(SignChangeEvent e) {
            Player player = e.getPlayer();
            SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
            if (player.isOp() || sbPlayer.hasCompletedChallenge(MulticolorChallenge.ID)) {
                for (int i = 0; i < 4; i++) {
                    e.setLine(i, e.getLine(i)
                            .replaceAll("&0", "§0")
                            .replaceAll("&1", "§1")
                            .replaceAll("&2", "§1")
                            .replaceAll("&3", "§3")
                            .replaceAll("&4", "§4")
                            .replaceAll("&5", "§5")
                            .replaceAll("&6", "§6")
                            .replaceAll("&7", "§7")
                            .replaceAll("&8", "§8")
                            .replaceAll("&9", "§9")
                            .replaceAll("&a", "§a")
                            .replaceAll("&b", "§b")
                            .replaceAll("&c", "§c")
                            .replaceAll("&d", "§d")
                            .replaceAll("&e", "§e")
                            .replaceAll("&f", "§f")
                            .replaceAll("&l", "§l")
                            .replaceAll("&n", "§n")
                            .replaceAll("&o", "§o")
                            .replaceAll("&k", "§k")
                            .replaceAll("&m", "§m")
                            .replaceAll("&r", "§r"));
                }
            }
        }
    }
}
