package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class HalloweenChallenge extends Challenge {

    public static final int ID = 101;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public HalloweenChallenge() {
        super(HalloweenChallenge.ID, "Halloween",
                Arrays.asList("§bQue serait Halloween sans", "§bcitrouilles ?"),
                ChallengeCategory.FARMING, ChallengeDifficulty.EASY, Material.JACK_O_LANTERN, 5, 10);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        ItemStack item1 = new ItemStack(Material.PUMPKIN);
        item1.setAmount(32);
        ItemStack item2 = new ItemStack(Material.JACK_O_LANTERN);
        item2.setAmount(32);
        return new ItemStack[] { item1, item2 };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.MELON_SEEDS);
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
