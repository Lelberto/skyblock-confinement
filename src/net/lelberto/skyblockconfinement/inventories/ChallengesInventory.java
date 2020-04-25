package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.challenges.Challenge;
import net.lelberto.skyblockconfinement.challenges.ChallengeCategory;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ChallengesInventory {

    private final SkyBlockPlayer sbPlayer;
    private final ChallengeCategory category;

    public ChallengesInventory(SkyBlockPlayer sbPlayer, ChallengeCategory category) {
        this.sbPlayer = sbPlayer;
        this.category = category;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Challenges - " + category.name);

        int i = 0;
        for (Challenge challenge : Game.challenges.stream().filter(challenge -> challenge.getCategory() == category).collect(Collectors.toList())) {
            ItemStack item = new ItemStack(challenge.getMaterial());
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("§e" + challenge.getName());
            List<String> lore = new ArrayList<>(challenge.getDescription());
            lore.addAll(Arrays.asList(" ", "§7Difficulté: " + challenge.getDifficulty().name));
            int finishedCount = sbPlayer.getCompletedChallenges().containsKey(challenge) ? sbPlayer.getCompletedChallenges().get(challenge) : 0;
            if (finishedCount == 0) {
                lore.add("§7Statut: §cNon terminé");
            } else {
                lore.add("§7Statut: §aTerminé§7 (§a" + finishedCount + "§7 fois)");
                itemMeta.addEnchant(Enchantment.LUCK, 0, true);
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            lore.addAll(Arrays.asList(" ", "§7§oEffectuez un §d§lclic gauche", "§7§opour compléter ce challenge", " ", "§7§oEffectuez un §d§lclic droit", "§7§opour voir les détails"));
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            inventory.setItem(i++, item);
        }
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeItemMeta = closeItem.getItemMeta();
        closeItemMeta.setDisplayName("§cRetour");
        closeItem.setItemMeta(closeItemMeta);
        inventory.setItem(26, closeItem);

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().startsWith("Challenges - ")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    if (material == Material.BARRIER) {
                        player.openInventory(new ChallengeCategoriesInventory(sbPlayer).create());
                    }
                    for (Challenge challenge : Game.challenges) {
                        if (challenge.getMaterial() == material) {
                            if (e.getClick().isRightClick()) {
                                player.openInventory(new ChallengeDetailsInventory(player, sbPlayer, challenge).create());
                            } else {
                                player.performCommand("sb challenge complete " + challenge.getId());
                            }
                        }
                    }
                }
            }
        }
    }
}
