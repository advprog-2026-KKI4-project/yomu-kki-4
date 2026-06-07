package id.ac.ui.cs.advprog.yomu.clans.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DivisionTest {

    @Test
    void testNextPromotion() {
        assertEquals(Division.SILVER, Division.BRONZE.next(), "BRONZE should promote to SILVER");
        assertEquals(Division.GOLD, Division.SILVER.next(), "SILVER should promote to GOLD");

        assertEquals(Division.DIAMOND, Division.GOLD.next(), "GOLD should promote to DIAMOND");
        assertEquals(Division.DIAMOND, Division.DIAMOND.next(), "DIAMOND should remain DIAMOND when promoted");
    }

    @Test
    void testPreviousRelegation() {
        assertEquals(Division.GOLD, Division.DIAMOND.previous(), "DIAMOND should relegate to GOLD");
        assertEquals(Division.SILVER, Division.GOLD.previous(), "GOLD should relegate to SILVER");

        assertEquals(Division.BRONZE, Division.SILVER.previous(), "SILVER should relegate to BRONZE");
        assertEquals(Division.BRONZE, Division.BRONZE.previous(), "BRONZE should remain BRONZE when relegated");
    }
}