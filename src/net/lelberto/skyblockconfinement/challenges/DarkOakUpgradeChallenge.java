package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class DarkOakUpgradeChallenge extends Challenge {

    public static final int ID = 3;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public DarkOakUpgradeChallenge() {
        super(DarkOakUpgradeChallenge.ID, "Dark oak upgrade",
                Arrays.asList("§bCoupe du bois de chêne, de sapin et de bouleau", "§bpour obtenir un nouveau", "§btype d'arbre : le chêne noir."),
                ChallengeCategory.BASIC, ChallengeDifficulty.EASY, Material.DARK_OAK_SAPLING, 5, 8);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        ItemStack oakItem = new ItemStack(Material.OAK_LOG);
        oakItem.setAmount(64);
        ItemStack spruceItem = new ItemStack(Material.SPRUCE_LOG);
        spruceItem.setAmount(64);
        ItemStack birchItem = new ItemStack(Material.BIRCH_LOG);
        birchItem.setAmount(64);
        return new ItemStack[] { oakItem, spruceItem, birchItem, new ItemStack(Material.OAK_SAPLING), new ItemStack(Material.SPRUCE_SAPLING), new ItemStack(Material.BIRCH_SAPLING) };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.DARK_OAK_SAPLING);
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
