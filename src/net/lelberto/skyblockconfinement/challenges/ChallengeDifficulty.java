package net.lelberto.skyblockconfinement.challenges;

public enum ChallengeDifficulty {

    EASY("§aFacile"),
    MEDIUM("§6Moyen"),
    HARD("§cDifficile"),
    HARDCORE("§4§oHardcore");

    public String name;

    ChallengeDifficulty(String name) {
        this.name = name;
    }
}
