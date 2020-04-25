package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.SkyblockException;
import net.lelberto.skyblockconfinement.challenges.ChallengeCategory;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ChallengeCategoriesInventory {

    private final SkyBlockPlayer sbPlayer;

    public ChallengeCategoriesInventory(SkyBlockPlayer sbPlayer) {
        this.sbPlayer = sbPlayer;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Challenges");

        int i = 0;
        for (ChallengeCategory category : ChallengeCategory.values()) {
            ItemStack item = new ItemStack(category.material);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(category.name);
            if (sbPlayer.getLevel() < category.levelsRequired) {
                itemMeta.setLore(Arrays.asList("§cBloqué", "§7Niveau requis: §c" + category.levelsRequired));
            } else {
                itemMeta.setLore(Arrays.asList("§aDébloqué"));
            }
            item.setItemMeta(itemMeta);
            inventory.setItem(i++, item);
        }

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Challenges")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    for (ChallengeCategory category : ChallengeCategory.values()) {
                        if (category.material == material && sbPlayer.getLevel() >= category.levelsRequired) {
                            player.openInventory(new ChallengesInventory(sbPlayer, category).create());
                        }
                    }
                }
            }
        }
    }
}
