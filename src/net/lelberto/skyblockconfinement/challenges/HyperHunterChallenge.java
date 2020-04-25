package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class HyperHunterChallenge extends Challenge {

    public static final int ID = 200;

    private final ItemStack[] requiredItems;
    private final ItemStack rewardItem;

    public HyperHunterChallenge() {
        super(BeetrootsChallenge.ID, "Hyper Hunter",
                Arrays.asList("§bChasse ces foutus mobs et", "§bchoppe-leur la tête.", "§bdernières récoltes et on", "§bt'offrira de belles betteraves"),
                ChallengeCategory.COMBAT, ChallengeDifficulty.HARDCORE, Material.ZOMBIE_HEAD, 300, 400);
        this.requiredItems = this.createRequiredItems();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack[] createRequiredItems() {
        return new ItemStack[] { new ItemStack(Material.ZOMBIE_HEAD), new ItemStack(Material.SKELETON_SKULL), new ItemStack(Material.WITHER_SKELETON_SKULL), new ItemStack(Material.CREEPER_HEAD) };
    }

    private ItemStack createRewardItem() {
        return new ItemStack(Material.ELYTRA);
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
