package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.challenges.Challenge;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChallengeDetailsInventory {

    private final Player player;
    private final SkyBlockPlayer sbPlayer;
    private final Challenge challenge;

    public ChallengeDetailsInventory(Player player, SkyBlockPlayer sbPlayer, Challenge challenge) {
        this.player = player;
        this.sbPlayer = sbPlayer;
        this.challenge = challenge;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, 45, "Détails - " + challenge.getName());

        ItemStack infoRequiredItem = new ItemStack(Material.PAPER);
        ItemMeta infoRequiredItemMeta = infoRequiredItem.getItemMeta();
        infoRequiredItemMeta.setDisplayName("§bRequis >");
        infoRequiredItem.setItemMeta(infoRequiredItemMeta);
        inventory.setItem(0, infoRequiredItem);

        ItemStack infoRewardItem = new ItemStack(Material.PAPER);
        ItemMeta infoRewardItemMeta = infoRewardItem.getItemMeta();
        infoRewardItemMeta.setDisplayName("§bRécompenses >");
        infoRewardItem.setItemMeta(infoRewardItemMeta);
        inventory.setItem(27, infoRewardItem);

        for (int i = 18; i < 27; i++) {
            ItemStack separatorItem = new ItemStack(Material.GLASS_PANE);
            ItemMeta separatorItemMeta = separatorItem.getItemMeta();
            separatorItemMeta.setDisplayName("§0-");
            separatorItem.setItemMeta(separatorItemMeta);
            inventory.setItem(i, separatorItem);
        }

        ItemStack xpItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta xpItemMeta = xpItem.getItemMeta();
        xpItemMeta.setDisplayName("§a" + challenge.getXpMin() + "§r-§a" + challenge.getXpMax() + "§r points d'expérience");
        xpItem.setItemMeta(xpItemMeta);
        inventory.setItem(28, xpItem);

        ItemStack[] requiredItems = challenge.getRequiredView(player, sbPlayer);
        for (int i = 1; i < requiredItems.length + 1; i++) {
            inventory.setItem(i, requiredItems[i - 1]);
        }

        ItemStack[] rewardItems = challenge.getRewardView(player, sbPlayer);
        for (int i = 29; i < rewardItems.length + 29; i++) {
            inventory.setItem(i, rewardItems[i - 29]);
        }

        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeItemMeta = closeItem.getItemMeta();
        closeItemMeta.setDisplayName("§cRetour");
        closeItem.setItemMeta(closeItemMeta);
        inventory.setItem(44, closeItem);

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().startsWith("Détails - ")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    if (material == Material.BARRIER) {
                        String[] splitedTitle = e.getView().getTitle().split("-");
                        Challenge challenge = Game.challenges.stream().filter(currentChallenge -> currentChallenge.getName().equals(splitedTitle[splitedTitle.length - 1].trim())).findFirst().orElse(null);
                        if (challenge != null) {
                            player.openInventory(new ChallengesInventory(sbPlayer, challenge.getCategory()).create());
                        } else {
                            player.openInventory(new ChallengeCategoriesInventory(sbPlayer).create());
                        }
                    }
                }
            }
        }
    }
}
