package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.SkyblockException;
import net.lelberto.skyblockconfinement.game.Island;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SkyblockInventory {

    private final SkyBlockPlayer sbPlayer;

    public SkyblockInventory(SkyBlockPlayer sbPlayer) {
        this.sbPlayer = sbPlayer;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Skyblock");

        ItemStack islandItem = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta islandItemMeta = islandItem.getItemMeta();
        if (sbPlayer.getIsland() == null) {
            islandItemMeta.setDisplayName("§aCréer une île");
            islandItemMeta.setLore(Arrays.asList("Si vous souhaitez en rejoindre une,", "demandez au(x) propriétaire(s)", "de vous inviter"));
        } else {
            islandItemMeta.setDisplayName("§aGérer votre île");
        }
        islandItem.setItemMeta(islandItemMeta);
        inventory.setItem(0, islandItem);

        ItemStack spawnItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta spawnItemMeta = spawnItem.getItemMeta();
        spawnItemMeta.setDisplayName("§aAller au spawn");
        spawnItem.setItemMeta(spawnItemMeta);
        inventory.setItem(1, spawnItem);

        ItemStack challengesItem = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta challengesItemMeta = challengesItem.getItemMeta();
        challengesItemMeta.setDisplayName("§aChallenges");
        challengesItem.setItemMeta(challengesItemMeta);
        inventory.setItem(2, challengesItem);

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Skyblock")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                SkyBlockPlayer sbPlayer = SkyBlockPlayer.getPlayer(player);
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    switch (material) {
                        case GRASS_BLOCK: // Island creation / management
                            if (sbPlayer.getIsland() == null) {
                                player.performCommand("sb island create");
                            } else {
                                player.openInventory(new IslandInventory().create());
                            }
                            break;
                        case NETHER_STAR: // Spawn teleportation
                            player.performCommand("spawn");
                            player.closeInventory();
                            break;
                        case ENCHANTED_BOOK: // Challenges
                            player.openInventory(new ChallengeCategoriesInventory(sbPlayer).create());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
