package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public abstract class Challenge {

    protected final int id;
    protected final String name;
    protected final List<String> description;
    protected final ChallengeCategory category;
    protected final ChallengeDifficulty difficulty;
    protected final Material material;
    protected final int xpMin;
    protected final int xpMax;

    public Challenge(int id, String name, List<String> description, ChallengeCategory category, ChallengeDifficulty difficulty, Material material, int xpMin, int xpMax) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty;
        this.material = material;
        this.xpMin = xpMin;
        this.xpMax = xpMax;
    }

    public abstract ItemStack[] getRequiredView(Player player, SkyBlockPlayer sbPlayer);

    protected ItemStack createSpecialRequiredItem(String... lore) {
        ItemStack item = new ItemStack(Material.MAP);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§eAutres");
        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    protected ItemStack createSpecialRewardItem(String... lore) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("§dPouvoir");
        itemMeta.setLore(Arrays.asList(lore));
        itemMeta.addEnchant(Enchantment.LUCK, 0, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(itemMeta);
        return item;
    }

    public abstract ItemStack[] getRewardView(Player player, SkyBlockPlayer sbPlayer);

    public abstract boolean verify(Player player, SkyBlockPlayer sbPlayer);

    public void complete(Player player, SkyBlockPlayer sbPlayer) {
        if (!sbPlayer.getCompletedChallenges().containsKey(this)) {
            sbPlayer.getCompletedChallenges().put(this, 0);
        }
        sbPlayer.getCompletedChallenges().put(this, sbPlayer.getCompletedChallenges().get(this) + 1);
        sbPlayer.addXp(new Random().nextInt(this.xpMax - this.xpMin + 1) + this.xpMin);
    }

    protected boolean containsItems(Inventory inventory, ItemStack... items) {
        for (ItemStack item : items) {
            int amount = 0;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack currentItem = inventory.getItem(i);
                if (currentItem != null && currentItem.getType() == item.getType()) {
                    amount += currentItem.getAmount();
                }
            }
            if (amount < item.getAmount()) {
                return false;
            }
        }
        return true;
    }

    protected void removeItems(Inventory inventory, ItemStack... items) {
        for (ItemStack item : items) {
            int amount = item.getAmount();
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack currentItem = inventory.getItem(i);
                if (currentItem != null && currentItem.getType() == item.getType()) {
                    if (amount >= currentItem.getAmount()) {
                        inventory.clear(i);
                    } else {
                        currentItem.setAmount(currentItem.getAmount() - amount);
                    }
                    amount -= currentItem.getAmount();
                }
                if (amount == 0) {
                    break;
                }
            }
        }
    }

    protected void giveRewardItems(Player player, ItemStack... items) {
        Inventory inventory = player.getInventory();
        inventory.addItem(items);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getDescription() {
        return description;
    }

    public ChallengeCategory getCategory() {
        return category;
    }

    public ChallengeDifficulty getDifficulty() {
        return difficulty;
    }

    public Material getMaterial() {
        return material;
    }

    public int getXpMin() {
        return xpMin;
    }

    public int getXpMax() {
        return xpMax;
    }
}
