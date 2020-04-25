package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BirchUpgradeChallenge extends Challenge {

    public static final int ID = 2;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public BirchUpgradeChallenge() {
        super(BirchUpgradeChallenge.ID, "Birch upgrade",
                Arrays.asList("§bCoupe du bois de chêne et de sapin", "§bpour obtenir un nouveau", "§btype d'arbre : le bouleau."),
                ChallengeCategory.BASIC, ChallengeDifficulty.EASY, Material.BIRCH_SAPLING, 4, 7);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        ItemStack oakItem = new ItemStack(Material.OAK_LOG);
        oakItem.setAmount(64);
        ItemStack spruceItem = new ItemStack(Material.SPRUCE_LOG);
        spruceItem.setAmount(64);
        return new ItemStack[] { oakItem, spruceItem, new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.SPRUCE_SAPLING) };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.BIRCH_SAPLING);
    }

    @Override
    public ItemStack[] getRequiredView(Player player, SkyBlockPlayer sbPlayer) {
        return this.requiredItems;
    }

    @Override
    public ItemStack[] getRewardView(Player player, SkyBlockPlayer sbPlayer) {
        return new ItemStack[] { this.rewardItem };
    }

    @Override
    public boolean verify(Player player, SkyBlockPlayer sbPlayer) {
        return super.containsItems(player.getInventory(), this.requiredItems);
    }

    @Override
    public void complete(Player player, SkyBlockPlayer sbPlayer) {
        super.complete(player, sbPlayer);
        super.giveRewardItems(player, this.rewardItem);
    }
}
