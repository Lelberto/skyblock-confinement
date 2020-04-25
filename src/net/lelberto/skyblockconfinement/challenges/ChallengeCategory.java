package net.lelberto.skyblockconfinement.challenges;

import org.bukkit.Material;

public enum ChallengeCategory {

    BASIC("§2Basique", 1, Material.STONE),
    FARMING("§6Farming", 2, Material.WHEAT),
    COMBAT("§5Combat", 5, Material.IRON_SWORD),
    NETHER("§cNether", 10, Material.NETHER_WART),
    END("§bEnd", 20, Material.ENDER_PEARL);

    public String name;
    public int levelsRequired;
    public Material material;

    ChallengeCategory(String name, int levelsRequired, Material material) {
        this.name = name;
        this.levelsRequired = levelsRequired;
        this.material = material;
    }
}
