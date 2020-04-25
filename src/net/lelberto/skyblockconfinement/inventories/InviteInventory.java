package net.lelberto.skyblockconfinement.inventories;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class InviteInventory implements Listener {

    private final Player player;

    public InviteInventory(Player player) {
        this.player = player;
    }

    public Inventory create() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.CHEST, "Inviter un joueur sur votre île");

        Bukkit.getOnlinePlayers().stream().filter(anotherPlayer -> {
            SkyBlockPlayer sbAnotherPlayer = SkyBlockPlayer.getPlayer(anotherPlayer);
            return !anotherPlayer.equals(player.getUniqueId()) && sbAnotherPlayer.getIsland() == null;
        }).forEach((player) -> {
            ItemStack playerItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta playerItemMeta = (SkullMeta) playerItem.getItemMeta();
            playerItemMeta.setDisplayName("§aInviter §6" + player.getName());
            playerItemMeta.setOwningPlayer(player);
            playerItem.setItemMeta(playerItemMeta);
            inventory.addItem(playerItem);
        });

        return inventory;
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onClick(InventoryClickEvent e) {
            if (e.getView().getTitle().equals("Inviter un joueur sur votre île")) {
                e.setCancelled(true);
                Player player = (Player) e.getWhoClicked();
                ItemStack item = e.getCurrentItem();
                if (item != null) {
                    Material material = item.getType();
                    switch (material) {
                        case PLAYER_HEAD: // Invite player
                            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
                            player.performCommand("sb island invite " + itemMeta.getOwningPlayer().getName());
                            player.closeInventory();
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }
}
