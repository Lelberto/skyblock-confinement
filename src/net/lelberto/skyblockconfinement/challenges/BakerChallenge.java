package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BakerChallenge extends Challenge {

    public static final int ID = 100;

    private final ItemStack requiredItem;
    private final ItemStack rewardItem;

    public BakerChallenge() {
        super(BakerChallenge.ID, "Boulanger",
                Arrays.asList("§bReconvertis-toi en boulanger", "§bet fabrique un stack de pain.", "§bSi le pain est bon, on",
                        "§bs'arrangera pour te trouver d'autres", "§btrucs à cultiver (et on parle", "§bpas de la weed petit junkie)"),
                ChallengeCategory.FARMING, ChallengeDifficulty.EASY, Material.BREAD, 1, 3);
        this.requiredItem = this.createRequiredItem();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack createRequiredItem() {
        ItemStack item = new ItemStack(Material.BREAD);
        item.setAmount(64);
        return item;
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.PUMPKIN_SEEDS);
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
        super.giveRewardItems(player, this.rewardItem);
    }
}
