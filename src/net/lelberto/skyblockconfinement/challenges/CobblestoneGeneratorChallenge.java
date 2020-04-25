package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CobblestoneGeneratorChallenge extends Challenge {

    public static final int ID = 0;

    private final ItemStack requiredItem;
    private final ItemStack rewardItem;

    public CobblestoneGeneratorChallenge() {
        super(CobblestoneGeneratorChallenge.ID, "Générateur de cobblestone",
                Arrays.asList("§bCrée un générateur de cobblestone", "§bet récupère un stack de cette", "§bpierre formidable."),
                ChallengeCategory.BASIC, ChallengeDifficulty.EASY, Material.COBBLESTONE, 1, 3);
        this.requiredItem = this.createRequiredItem();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack createRequiredItem() {
        ItemStack item = new ItemStack(Material.COBBLESTONE);
        item.setAmount(64);
        return item;
    }

    private ItemStack createRewardItem() {
        ItemStack item = new ItemStack(Material.LEATHER);
        item.setAmount(5);
        return item;
    }

    @Override
    public ItemStack[] getRequiredView(Player player, SkyBlockPlayer sbPlayer) {
        return new ItemStack[] { this.requiredItem };
    }

    @Override
    public ItemStack[] getRewardView(Player player, SkyBlockPlayer sbPlayer) {
        return new ItemStack[] { this.rewardItem };
    }

    @Override
    public boolean verify(Player player, SkyBlockPlayer sbPlayer) {
        return super.containsItems(player.getInventory(), this.requiredItem);
    }

    @Override
    public void complete(Player player, SkyBlockPlayer sbPlayer) {
        super.complete(player, sbPlayer);
        super.removeItems(player.getInventory(), this.requiredItem);
        super.giveRewardItems(player, new ItemStack[] { this.rewardItem });
    }
}
