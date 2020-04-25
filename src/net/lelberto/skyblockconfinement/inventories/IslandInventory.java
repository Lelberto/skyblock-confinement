package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.Plugin;
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

public class IslandInventory implements Listener {

    public IslandInventory() {}

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER, "Gestion de votre île");

        ItemStack homeItem = new ItemStack(Material.CHEST);
        ItemMeta homeItemMeta = homeItem.getItemMeta();
        homeItemMeta.setDisplayName("§aAller sur votre île");
        homeItemMeta.setLore(Arrays.asList("Cette action vous téléportera", "au home de votre île"));
        homeItem.setItemMeta(homeItemMeta);
        inventory.setItem(0, homeItem);

        ItemStack setHomeItem = new ItemStack(Material.TRAPPED_CHEST);
        ItemMeta setHomeItemMeta = setHomeItem.getItemMeta();
        setHomeItemMeta.setDisplayName("§aChanger le home de votre île");
        setHomeItemMeta.setLore(Arrays.asList("Cette action redéfinira le home", "de votre île à la position", "où vous êtes actuellement.", "Les autres joueurs de votre île", "seront également impactés"));
        setHomeItem.setItemMeta(setHomeItemMeta);
        inventory.setItem(1, setHomeItem);

        ItemStack inviteItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta inviteItemMeta = inviteItem.getItemMeta();
        inviteItemMeta.setDisplayName("§aInviter un joueur sur votre île");
        inviteItem.setItemMeta(inviteItemMeta);
        inventory.setItem(2, inviteItem);

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Gestion de votre île")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    switch (material) {
                        case CHEST: // Teleport to home
                            player.performCommand("sb island home");
                            player.closeInventory();
                            break;
                        case TRAPPED_CHEST: // Change home
                            player.performCommand("sb island sethome");
                            player.closeInventory();
                            break;
                        case PLAYER_HEAD: // Invite player
                            player.openInventory(new InviteInventory(player).create());
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
