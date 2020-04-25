package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class IceSandChallenge extends Challenge {

    public static final int ID = 6;

    private final ItemStack requiredItem;
    private final ItemStack rewardItem;

    public IceSandChallenge() {
        super(IceSandChallenge.ID, "Sable glacé",
                Arrays.asList("§bFais fondre du sable", "§bpour en faire du verre.", "§bOn te refilera un équivalent gelé."),
                ChallengeCategory.BASIC, ChallengeDifficulty.EASY, Material.SAND, 3, 5);
        this.requiredItem = this.createRequiredItem();
        this.rewardItem = this.createRewardItem();
    }

    private ItemStack createRequiredItem() {
        ItemStack item = new ItemStack(Material.GLASS);
        item.setAmount(64);
        return item;
    }

    private ItemStack createRewardItem() {
        ItemStack item = new ItemStack(Material.PACKED_ICE);
        item.setAmount(10);
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
