package id.ac.ui.cs.advprog.yomu.clans.model;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ClanMemberModelTest {

    private ClanMember clanMember;

    @BeforeEach
    void setUp() {
        clanMember = new ClanMember();
    }

    @Test
    void testDefaultValues() {
        assertEquals(0, clanMember.getLocalScore());

        assertNotNull(clanMember.getJoinedAt());
        assertTrue(clanMember.getJoinedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testGettersAndSetters() {
        String testId = UUID.randomUUID().toString();
        Clan testClan = new Clan();
        Long testStudentId = 100L;
        User testStudent = new User();
        String testRole = "LEADER";
        String testStatus = "APPROVED";
        int testLocalScore = 150;
        LocalDateTime testJoinedAt = LocalDateTime.of(2025, 1, 1, 12, 0);

        clanMember.setId(testId);
        clanMember.setClan(testClan);
        clanMember.setStudentId(testStudentId);
        clanMember.setStudent(testStudent);
        clanMember.setRole(testRole);
        clanMember.setStatus(testStatus);
        clanMember.setLocalScore(testLocalScore);
        clanMember.setJoinedAt(testJoinedAt);

        assertEquals(testId, clanMember.getId());
        assertEquals(testClan, clanMember.getClan());
        assertEquals(testStudentId, clanMember.getStudentId());
        assertEquals(testStudent, clanMember.getStudent());
        assertEquals(testRole, clanMember.getRole());
        assertEquals(testStatus, clanMember.getStatus());
        assertEquals(testLocalScore, clanMember.getLocalScore());
        assertEquals(testJoinedAt, clanMember.getJoinedAt());
    }
}