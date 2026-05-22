package id.ac.ui.cs.advprog.yomu.clans.model;

public enum Division {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    // OCP: If we add a 'PLATINUM' division later, we only change this enum,
    // without touching the LeaderboardService!
    public Division next() {
        return switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD, DIAMOND -> DIAMOND;
        };
    }
}