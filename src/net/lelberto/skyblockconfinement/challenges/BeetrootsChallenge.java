package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class BeetrootsChallenge extends Challenge {

    public static final int ID = 102;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public BeetrootsChallenge() {
        super(BeetrootsChallenge.ID, "Beetroots",
                Arrays.asList("§bTu veux encore planter des trucs ?", "§bAlors donne-nous un peu de tes", "§bdernières récoltes et on", "§bt'offrira de belles betteraves."),
                ChallengeCategory.FARMING, ChallengeDifficulty.MEDIUM, Material.BEETROOT, 10, 15);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        ItemStack item1 = new ItemStack(Material.WHEAT);
        item1.setAmount(64);
        ItemStack item2 = new ItemStack(Material.PUMPKIN);
        item2.setAmount(64);
        return new ItemStack[] { item1, item2 };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.BEETROOT_SEEDS);
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
