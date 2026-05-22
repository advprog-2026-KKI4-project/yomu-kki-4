package id.ac.ui.cs.advprog.yomu.clans.model;

public enum Division {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    // Promotion
    public Division next() {
        return switch (this) {
            case BRONZE -> SILVER;
            case SILVER -> GOLD;
            case GOLD, DIAMOND -> DIAMOND;
        };
    }

    // Relegation
    public Division previous() {
        return switch (this) {
            case DIAMOND -> GOLD;
            case GOLD -> SILVER;
            case SILVER, BRONZE -> BRONZE;
        };
    }
}