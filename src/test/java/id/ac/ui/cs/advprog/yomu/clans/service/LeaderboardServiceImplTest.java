package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.model.Division;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceImplTest {

    @Mock
    private ClanRepository clanRepository;

    @InjectMocks
    private LeaderboardServiceImpl leaderboardService;

    private Clan clan1;
    private Clan clan2;
    private Clan clan3;
    private Clan clan4;
    private Clan clan5;

    @BeforeEach
    void setUp() {
        clan1 = mock(Clan.class);
        clan2 = mock(Clan.class);
        clan3 = mock(Clan.class);
        clan4 = mock(Clan.class);
        clan5 = mock(Clan.class);
    }

    @Test
    void updateClanScore_success() {
        Clan testClan = new Clan();
        testClan.setActiveMultiplier(1.2);

        ClanMember member1 = new ClanMember();
        member1.setStatus("ACCEPTED");
        member1.setLocalScore(100);

        ClanMember member2 = new ClanMember();
        member2.setStatus("PENDING");
        member2.setLocalScore(50);

        ClanMember member3 = new ClanMember();
        member3.setStatus("ACCEPTED");
        member3.setLocalScore(200);

        testClan.setMembers(List.of(member1, member2, member3));

        leaderboardService.updateClanScore(testClan);

        assertEquals(360L, testClan.getTotalScore());
        verify(clanRepository, times(1)).save(testClan);
    }

    @Test
    void endCurrentSeason_success() {
        Division[] divisions = Division.values();
        Division testDivision = divisions.length > 2 ? divisions[1] : divisions[0];

        when(clan1.getDivision()).thenReturn(testDivision);
        when(clan2.getDivision()).thenReturn(testDivision);
        when(clan3.getDivision()).thenReturn(testDivision);
        when(clan4.getDivision()).thenReturn(testDivision);
        when(clan5.getDivision()).thenReturn(testDivision);

        when(clan1.getMembers()).thenReturn(new ArrayList<>());
        when(clan2.getMembers()).thenReturn(new ArrayList<>());
        when(clan3.getMembers()).thenReturn(new ArrayList<>());
        when(clan4.getMembers()).thenReturn(new ArrayList<>());
        when(clan5.getMembers()).thenReturn(new ArrayList<>());

        List<Clan> allClans = List.of(clan1, clan2, clan3, clan4, clan5);
        when(clanRepository.findAllByOrderByTotalScoreDesc()).thenReturn(allClans);

        leaderboardService.endCurrentSeason();

        verify(clan1).setPreviousRank(1);
        verify(clan1).setDivision(testDivision.next());

        verify(clan2).setPreviousRank(2);
        verify(clan2, never()).setDivision(any());

        verify(clan3).setPreviousRank(3);
        verify(clan3, never()).setDivision(any());

        verify(clan4).setPreviousRank(4);
        verify(clan4, never()).setDivision(any());

        verify(clan5).setPreviousRank(5);
        verify(clan5).setDivision(testDivision.previous());

        verify(clan1).setTotalScore(0L);
        verify(clan5).setTotalScore(0L);
        verify(clanRepository).saveAll(allClans);
    }

    @Test
    void endCurrentSeason_emptyList() {
        when(clanRepository.findAllByOrderByTotalScoreDesc()).thenReturn(new ArrayList<>());

        leaderboardService.endCurrentSeason();

        verify(clanRepository).saveAll(anyList());
    }

    @Test
    void getGlobalLeaderboard_success() {
        List<Clan> mockLeaderboard = List.of(clan1, clan2);
        when(clanRepository.findAllByOrderByTotalScoreDesc()).thenReturn(mockLeaderboard);

        List<Clan> result = leaderboardService.getGlobalLeaderboard();

        assertEquals(2, result.size());
        assertEquals(clan1, result.get(0));
        verify(clanRepository).findAllByOrderByTotalScoreDesc();
    }

    @Test
    void getDivisionLeaderboard_validDivision() {
        Division testDivision = Division.values()[0];
        List<Clan> mockList = List.of(clan1);

        when(clanRepository.findAllByDivisionOrderByTotalScoreDesc(testDivision))
                .thenReturn(mockList);

        List<Clan> result = leaderboardService.getDivisionLeaderboard(testDivision.name());

        assertEquals(1, result.size());
        verify(clanRepository).findAllByDivisionOrderByTotalScoreDesc(testDivision);
    }

    @Test
    void getDivisionLeaderboard_invalidDivision() {
        List<Clan> result = leaderboardService.getDivisionLeaderboard("INVALID_DIVISION_NAME");

        assertTrue(result.isEmpty());
        verify(clanRepository, never()).findAllByDivisionOrderByTotalScoreDesc(any());
    }

    @Test
    void getDivisionLeaderboard_nullDivision() {
        List<Clan> result = leaderboardService.getDivisionLeaderboard(null);

        assertTrue(result.isEmpty());
        verify(clanRepository, never()).findAllByDivisionOrderByTotalScoreDesc(any());
    }
}