package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class SpruceUpgradeChallenge extends Challenge {

    public static final int ID = 1;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public SpruceUpgradeChallenge() {
        super(SpruceUpgradeChallenge.ID, "Spruce upgrade",
                Arrays.asList("§bCoupe du bois de chêne", "§bpour obtenir un nouveau", "§btype d'arbre : le sapin."),
                ChallengeCategory.BASIC, ChallengeDifficulty.EASY, Material.SPRUCE_SAPLING, 3, 6);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        ItemStack item = new ItemStack(Material.OAK_LOG);
        item.setAmount(64);
        return new ItemStack[] { item, new ItemStack(Material.OAK_SAPLING) };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.SPRUCE_SAPLING);
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
