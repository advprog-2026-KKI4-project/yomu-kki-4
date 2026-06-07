package id.ac.ui.cs.advprog.yomu.clans.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClanModelTest {

    private Clan clan;

    @BeforeEach
    void setUp() {
        clan = new Clan();
    }

    @Test
    void testDefaultValues() {
        assertNull(clan.getId());
        assertNull(clan.getName());
        assertNull(clan.getLeaderId());
        assertNull(clan.getBio());
        assertEquals(0L, clan.getTotalScore());
        assertEquals(Division.BRONZE, clan.getDivision());
        assertEquals(1.0, clan.getActiveMultiplier());
        assertNull(clan.getPreviousRank());

        assertNotNull(clan.getMembers());
        assertTrue(clan.getMembers().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        UUID expectedId = UUID.randomUUID();
        String expectedName = "Yomu Warriors";
        Long expectedLeaderId = 1L;
        String expectedBio = "We read everything.";
        Long expectedTotalScore = 1500L;
        Double expectedMultiplier = 1.5;
        Integer expectedPreviousRank = 5;

        List<ClanMember> expectedMembers = new ArrayList<>();
        ClanMember mockMember = new ClanMember();
        expectedMembers.add(mockMember);

        clan.setId(expectedId);
        clan.setName(expectedName);
        clan.setLeaderId(expectedLeaderId);
        clan.setBio(expectedBio);
        clan.setTotalScore(expectedTotalScore);

        clan.setDivision(null);

        clan.setActiveMultiplier(expectedMultiplier);
        clan.setPreviousRank(expectedPreviousRank);
        clan.setMembers(expectedMembers);

        assertEquals(expectedId, clan.getId());
        assertEquals(expectedName, clan.getName());
        assertEquals(expectedLeaderId, clan.getLeaderId());
        assertEquals(expectedBio, clan.getBio());
        assertEquals(expectedTotalScore, clan.getTotalScore());
        assertNull(clan.getDivision());
        assertEquals(expectedMultiplier, clan.getActiveMultiplier());
        assertEquals(expectedPreviousRank, clan.getPreviousRank());

        assertEquals(1, clan.getMembers().size());
        assertEquals(expectedMembers, clan.getMembers());
    }
}