package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanServiceImplTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private ClanMemberRepository memberRepository;

    @Mock
    private LeaderboardService leaderboardService;

    @InjectMocks
    private ClanServiceImpl clanService;

    private Clan clan;
    private ClanMember leaderMember;
    private ClanMember studentMember;
    private UUID clanId;
    private final Long leaderId = 1L;
    private final Long studentId = 2L;

    @BeforeEach
    void setUp() {
        clanId = UUID.randomUUID();

        clan = new Clan();
        clan.setId(clanId);
        clan.setName("Test Clan");
        clan.setBio("Bio here");
        clan.setLeaderId(leaderId);
        clan.setMembers(new ArrayList<>());

        leaderMember = new ClanMember();
        leaderMember.setClan(clan);
        leaderMember.setStudentId(leaderId);
        leaderMember.setRole("LEADER");
        leaderMember.setStatus("ACCEPTED");
        leaderMember.setLocalScore(0);

        studentMember = new ClanMember();
        studentMember.setClan(clan);
        studentMember.setStudentId(studentId);
        studentMember.setRole("MEMBER");
        studentMember.setLocalScore(0);
    }


    @Test
    void findAllClans_returnsList() {
        when(clanRepository.findAll()).thenReturn(List.of(clan));
        List<Clan> result = clanService.findAllClans();
        assertEquals(1, result.size());
    }

    @Test
    void createClan_success() {
        when(memberRepository.findByStudentId(leaderId)).thenReturn(new ArrayList<>());
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);

        Clan created = clanService.createClan("New Clan", "New Bio", leaderId);

        assertNotNull(created);
        verify(clanRepository).save(any(Clan.class));
        verify(memberRepository).save(any(ClanMember.class));
        verify(memberRepository).deleteAll(anyList());
    }

    @Test
    void createClan_throwsIfAlreadyInClan() {
        when(memberRepository.findByStudentId(leaderId)).thenReturn(List.of(leaderMember));
        assertThrows(RuntimeException.class, () -> clanService.createClan("New Clan", "New Bio", leaderId));
    }

    @Test
    void updateClan_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(clanRepository.save(any(Clan.class))).thenReturn(clan);

        Clan updated = clanService.updateClan(clanId, leaderId, "Updated Name", "Updated Bio");

        assertEquals("Updated Name", updated.getName());
        assertEquals("Updated Bio", updated.getBio());
    }

    @Test
    void updateClan_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.updateClan(clanId, leaderId, "Name", "Bio"));
    }

    @Test
    void updateClan_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.updateClan(clanId, 99L, "Name", "Bio"));
    }

    @Test
    void deleteClan_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        clanService.deleteClan(clanId, leaderId);
        verify(clanRepository).delete(clan);
    }

    @Test
    void deleteClan_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, leaderId));
    }

    @Test
    void deleteClan_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.deleteClan(clanId, 99L));
    }

    @Test
    void requestToJoin_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());

        clanService.requestToJoin(clanId, studentId);

        ArgumentCaptor<ClanMember> captor = ArgumentCaptor.forClass(ClanMember.class);
        verify(memberRepository).save(captor.capture());
        assertEquals("PENDING_REQUEST", captor.getValue().getStatus());
    }

    @Test
    void requestToJoin_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.requestToJoin(clanId, studentId));
    }

    @Test
    void requestToJoin_alreadyRequested() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        studentMember.setStatus("PENDING_REQUEST");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        assertThrows(RuntimeException.class, () -> clanService.requestToJoin(clanId, studentId));
    }

    @Test
    void approveMember_success() {
        studentMember.setStatus("PENDING_REQUEST");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        clanService.approveMember(clanId, leaderId, studentId);

        assertEquals("ACCEPTED", studentMember.getStatus());
        verify(memberRepository).save(studentMember);
        verify(leaderboardService).updateClanScore(clan);
    }

    @Test
    void approveMember_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.approveMember(clanId, leaderId, studentId));
    }

    @Test
    void approveMember_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.approveMember(clanId, 99L, studentId));
    }

    @Test
    void approveMember_memberNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.approveMember(clanId, leaderId, studentId));
    }

    @Test
    void approveMember_notPendingRequest() {
        studentMember.setStatus("ACCEPTED");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));
        assertThrows(RuntimeException.class, () -> clanService.approveMember(clanId, leaderId, studentId));
    }

    @Test
    void rejectRequest_success() {
        studentMember.setStatus("PENDING_REQUEST");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));

        clanService.rejectRequest(clanId, leaderId, studentId);
        verify(memberRepository).delete(studentMember);
    }

    @Test
    void rejectRequest_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.rejectRequest(clanId, leaderId, studentId));
    }

    @Test
    void rejectRequest_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.rejectRequest(clanId, 99L, studentId));
    }

    @Test
    void rejectRequest_memberNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.rejectRequest(clanId, leaderId, studentId));
    }

    @Test
    void rejectRequest_notPending() {
        studentMember.setStatus("ACCEPTED");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));
        assertThrows(RuntimeException.class, () -> clanService.rejectRequest(clanId, leaderId, studentId));
    }

    @Test
    void inviteStudent_success() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        clanService.inviteStudent(clanId, leaderId, studentId);
        ArgumentCaptor<ClanMember> captor = ArgumentCaptor.forClass(ClanMember.class);
        verify(memberRepository).save(captor.capture());
        assertEquals("PENDING_INVITE", captor.getValue().getStatus());
    }

    @Test
    void inviteStudent_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.inviteStudent(clanId, leaderId, studentId));
    }

    @Test
    void inviteStudent_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.inviteStudent(clanId, 99L, studentId));
    }

    @Test
    void acceptInvitation_success() {
        studentMember.setStatus("PENDING_INVITE");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));

        clanService.acceptInvitation(clanId, studentId);

        assertEquals("ACCEPTED", studentMember.getStatus());
        verify(memberRepository).save(studentMember);
        verify(leaderboardService).updateClanScore(clan);
    }

    @Test
    void acceptInvitation_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.acceptInvitation(clanId, studentId));
    }

    @Test
    void acceptInvitation_inviteNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.acceptInvitation(clanId, studentId));
    }

    @Test
    void acceptInvitation_notPending() {
        studentMember.setStatus("ACCEPTED");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));
        assertThrows(RuntimeException.class, () -> clanService.acceptInvitation(clanId, studentId));
    }

    @Test
    void declineInvitation_success() {
        studentMember.setStatus("PENDING_INVITE");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));

        clanService.declineInvitation(clanId, studentId);
        verify(memberRepository).delete(studentMember);
    }

    @Test
    void declineInvitation_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.declineInvitation(clanId, studentId));
    }

    @Test
    void declineInvitation_inviteNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.declineInvitation(clanId, studentId));
    }

    @Test
    void declineInvitation_notPending() {
        studentMember.setStatus("PENDING_REQUEST");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));
        assertThrows(RuntimeException.class, () -> clanService.declineInvitation(clanId, studentId));
    }

    @Test
    void kickMember_success() {
        studentMember.setStatus("ACCEPTED");
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.of(studentMember));

        clanService.kickMember(clanId, leaderId, studentId);
        verify(memberRepository).delete(studentMember);
        verify(leaderboardService).updateClanScore(clan);
    }

    @Test
    void kickMember_clanNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, leaderId, studentId));
    }

    @Test
    void kickMember_unauthorized() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, 99L, studentId));
    }

    @Test
    void kickMember_memberNotFound() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, studentId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, leaderId, studentId));
    }

    @Test
    void kickMember_failsKickingLeader() {
        when(clanRepository.findById(clanId)).thenReturn(Optional.of(clan));
        when(memberRepository.findByClanAndStudentId(clan, leaderId)).thenReturn(Optional.of(leaderMember));
        assertThrows(RuntimeException.class, () -> clanService.kickMember(clanId, leaderId, leaderId));
    }

    @Test
    void leaveClan_success() {
        studentMember.setStatus("ACCEPTED");
        clan.getMembers().add(studentMember);
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        clanService.leaveClan(studentId);

        verify(memberRepository).delete(studentMember);
        verify(leaderboardService).updateClanScore(clan);
        verify(clanRepository).save(clan);
        assertNull(studentMember.getClan());
    }

    @Test
    void leaveClan_failsIfLeader() {
        when(memberRepository.findByStudentId(leaderId)).thenReturn(List.of(leaderMember));
        assertThrows(RuntimeException.class, () -> clanService.leaveClan(leaderId));
    }

    @Test
    void leaveClan_notActive() {
        studentMember.setStatus("PENDING_REQUEST");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));
        assertThrows(RuntimeException.class, () -> clanService.leaveClan(studentId));
    }

    @Test
    void isUserInAnyClan_true() {
        studentMember.setStatus("ACCEPTED");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));
        assertTrue(clanService.isUserInAnyClan(studentId));
    }

    @Test
    void isUserInAnyClan_false() {
        when(memberRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());
        assertFalse(clanService.isUserInAnyClan(studentId));
    }

    @Test
    void getAcceptedMembership_present() {
        studentMember.setStatus("ACCEPTED");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));
        Optional<ClanMember> result = clanService.getAcceptedMembership(studentId);
        assertTrue(result.isPresent());
        assertEquals("ACCEPTED", result.get().getStatus());
    }

    @Test
    void getAcceptedMembership_empty() {
        studentMember.setStatus("PENDING_REQUEST");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));
        Optional<ClanMember> result = clanService.getAcceptedMembership(studentId);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPendingRequestClanIds_success() {
        studentMember.setStatus("PENDING_REQUEST");

        ClanMember otherMember = new ClanMember();
        otherMember.setClan(clan);
        otherMember.setStatus("ACCEPTED");

        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember, otherMember));

        List<UUID> ids = clanService.getPendingRequestClanIds(studentId);

        assertEquals(1, ids.size());
        assertEquals(clanId, ids.get(0));
    }

    @Test
    void getPendingInvitations_success() {
        studentMember.setStatus("PENDING_INVITE");

        ClanMember otherMember = new ClanMember();
        otherMember.setClan(clan);
        otherMember.setStatus("ACCEPTED");

        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember, otherMember));

        List<ClanMember> invites = clanService.getPendingInvitations(studentId);

        assertEquals(1, invites.size());
    }

    @Test
    void addPoints_success() {
        studentMember.setStatus("ACCEPTED");
        studentMember.setLocalScore(10);
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        clanService.addPoints(studentId, 5);

        assertEquals(15, studentMember.getLocalScore());
        verify(memberRepository).save(studentMember);
        verify(leaderboardService).updateClanScore(clan);
    }

    @Test
    void addPoints_userNotInClan() {
        studentMember.setStatus("PENDING_REQUEST");
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        clanService.addPoints(studentId, 5);
        verify(memberRepository, never()).save(any());
        verify(leaderboardService, never()).updateClanScore(any());
    }

    @Test
    void updateMemberScoreMock_success() {
        studentMember.setStatus("ACCEPTED");
        studentMember.setLocalScore(10);
        when(memberRepository.findByStudentId(studentId)).thenReturn(List.of(studentMember));

        clanService.updateMemberScoreMock(studentId, 100);

        assertEquals(100, studentMember.getLocalScore());
        verify(memberRepository).save(studentMember);
        verify(leaderboardService).updateClanScore(clan);
    }

    @Test
    void updateMemberScoreMock_notAccepted() {
        when(memberRepository.findByStudentId(studentId)).thenReturn(new ArrayList<>());
        clanService.updateMemberScoreMock(studentId, 100);
        verify(memberRepository, never()).save(any());
    }
}