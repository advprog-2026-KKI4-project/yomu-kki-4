package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.model.Division;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LeaderboardViewControllerTest {

    @Mock
    private LeaderboardService leaderboardService;

    @Mock
    private ClanService clanService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderboardViewController controller;

    private Principal principal;
    private User testUser;
    private Clan testClan;
    private ClanMember testMembership;

    @BeforeEach
    void setUp() {
        // Setup User
        testUser = User.builder()
                .id(1L)
                .email("student@yomu.id")
                .username("student")
                .password("password")
                .build();

        // Setup Principal
        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("student@yomu.id");
        when(userRepository.findByEmail("student@yomu.id")).thenReturn(Optional.of(testUser));

        // Setup Clan
        testClan = new Clan();
        testClan.setName("Top Readers");
        testClan.setDivision(Division.SILVER);

        // Setup Membership
        testMembership = new ClanMember();
        testMembership.setClan(testClan);
        testMembership.setStudentId(1L);
    }

    @Test
    void viewLeaderboard_globalTab_returnsGlobalLeaderboard() {
        when(clanService.getAcceptedMembership(1L)).thenReturn(Optional.of(testMembership));
        when(leaderboardService.getGlobalLeaderboard()).thenReturn(List.of(testClan));

        Model model = new ExtendedModelMap();
        String view = controller.viewLeaderboard("global", principal, model);

        assertThat(view).isEqualTo("clans/clanLeaderboard");
        assertThat(model.asMap().get("clans")).isEqualTo(List.of(testClan));
        assertThat(model.asMap().get("activeTab")).isEqualTo("global");

        assertThat(model.asMap().containsKey("currentDivision")).isFalse();

        verify(leaderboardService).getGlobalLeaderboard();
        verify(leaderboardService, never()).getDivisionLeaderboard(anyString());
    }

    @Test
    void viewLeaderboard_divisionTab_withMembership_returnsDivisionLeaderboard() {
        when(clanService.getAcceptedMembership(1L)).thenReturn(Optional.of(testMembership));
        when(leaderboardService.getDivisionLeaderboard("SILVER")).thenReturn(List.of(testClan));

        Model model = new ExtendedModelMap();
        String view = controller.viewLeaderboard("division", principal, model);

        assertThat(view).isEqualTo("clans/clanLeaderboard");
        assertThat(model.asMap().get("clans")).isEqualTo(List.of(testClan));
        assertThat(model.asMap().get("activeTab")).isEqualTo("division");
        assertThat(model.asMap().get("currentDivision")).isEqualTo("SILVER");

        verify(leaderboardService).getDivisionLeaderboard("SILVER");
    }

    @Test
    void viewLeaderboard_divisionTab_withoutMembership_defaultsToBronze() {
        when(clanService.getAcceptedMembership(1L)).thenReturn(Optional.empty());
        when(leaderboardService.getDivisionLeaderboard("BRONZE")).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.viewLeaderboard("division", principal, model);

        assertThat(view).isEqualTo("clans/clanLeaderboard");
        assertThat(model.asMap().get("clans")).isEqualTo(Collections.emptyList());
        assertThat(model.asMap().get("activeTab")).isEqualTo("division");
        assertThat(model.asMap().get("currentDivision")).isEqualTo("BRONZE");

        verify(leaderboardService).getDivisionLeaderboard("BRONZE");
    }

    @Test
    void viewLeaderboard_throwsException_whenUserNotFound() {
        when(userRepository.findByEmail("student@yomu.id")).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();

        assertThatThrownBy(() -> controller.viewLeaderboard("global", principal, model))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}