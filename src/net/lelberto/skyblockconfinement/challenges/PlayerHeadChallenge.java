package net.lelberto.skyblockconfinement.challenges;

import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerHeadChallenge extends Challenge {

    public static final int ID = 300;

    private static final List<UUID> eligiblePlayers = new ArrayList<>();

    public PlayerHeadChallenge() {
        super(PlayerHeadChallenge.ID, "Sur la tête de oim",
                Arrays.asList("§bSaute dans le vide", "§bavec une tête de Wither.", "§bOui oui, on te demande bien", "§bde te suicider là."),
                ChallengeCategory.NETHER, ChallengeDifficulty.MEDIUM, Material.WITHER_SKELETON_SKULL, 15, 20);
    }

    @Override
    public ItemStack[] getRequiredView(Player player, SkyBlockPlayer sbPlayer) {
        return new ItemStack[] { super.createSpecialRequiredItem("§fMourrir dans le vide"), new ItemStack(Material.WITHER_SKELETON_SKULL) };
    }

    @Override
    public ItemStack[] getRewardView(Player player, SkyBlockPlayer sbPlayer) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        itemMeta.setOwningPlayer(player);
        item.setItemMeta(itemMeta);
        return new ItemStack[] { item };
    }

    @Override
    public boolean verify(Player player, SkyBlockPlayer sbPlayer) {
        return PlayerHeadChallenge.eligiblePlayers.contains(player.getUniqueId());
    }

    @Override
    public void complete(Player player, SkyBlockPlayer sbPlayer) {
        super.complete(player, sbPlayer);
        PlayerHeadChallenge.eligiblePlayers.remove(player.getUniqueId());
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
        itemMeta.setOwningPlayer(player);
        item.setItemMeta(itemMeta);
        super.giveRewardItems(player, item);
    }

    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onDeath(PlayerDeathEvent e) {
            Player player = e.getEntity();
            if (player.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID && e.getDrops().contains(new ItemStack(Material.WITHER_SKELETON_SKULL))) {
                PlayerHeadChallenge.eligiblePlayers.add(player.getUniqueId());
            }
        }
    }
}
