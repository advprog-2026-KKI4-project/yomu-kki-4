package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DailyMissionControllerTest {

    @Mock
    private DailyMissionService dailyMissionService;

    @Mock
    private MissionTrackingService missionTrackingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DailyMissionController controller;

    private User user;
    private DailyMission mission;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("alice@test.com").username("alice").password("pass123").build();
        authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("alice@test.com");
        when(authentication.getAuthorities()).thenReturn(Collections.emptyList());
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));

        mission = DailyMission.builder()
                .id(UUID.randomUUID()).name("Daily Reader").description("Read 1 article")
                .type(MissionType.READING).targetCount(1).rewardPoints(10).active(true).build();
    }

    @Test
    void listMissions_returnsMissionListView() {
        when(missionTrackingService.getUserProgressToday(user)).thenReturn(Collections.emptyList());
        when(dailyMissionService.findAll()).thenReturn(List.of(mission));
        when(dailyMissionService.findActiveMissions()).thenReturn(List.of(mission));

        Model model = new ExtendedModelMap();
        String view = controller.listMissions(model, authentication);

        assertThat(view).isEqualTo("mission/missionList");
        assertThat(model.asMap().get("missions")).isEqualTo(List.of(mission));
        assertThat(model.asMap().get("activeMissions")).isEqualTo(List.of(mission));
        assertThat(model.asMap().get("progressMap")).isNotNull();
    }

    @Test
    void listMissions_buildsProgressMapFromTodayProgress() {
        UserMissionProgress progress = UserMissionProgress.builder()
                .user(user).mission(mission).currentCount(1).completed(true).date(LocalDate.now()).build();
        when(missionTrackingService.getUserProgressToday(user)).thenReturn(List.of(progress));
        when(dailyMissionService.findAll()).thenReturn(List.of(mission));
        when(dailyMissionService.findActiveMissions()).thenReturn(List.of(mission));

        Model model = new ExtendedModelMap();
        controller.listMissions(model, authentication);

        @SuppressWarnings("unchecked")
        java.util.Map<UUID, UserMissionProgress> progressMap =
                (java.util.Map<UUID, UserMissionProgress>) model.asMap().get("progressMap");
        assertThat(progressMap).containsKey(mission.getId());
        assertThat(progressMap.get(mission.getId())).isEqualTo(progress);
    }

    @Test
    void showCreateForm_returnsMissionCreateView() {
        Model model = new ExtendedModelMap();
        String view = controller.showCreateForm(model);

        assertThat(view).isEqualTo("mission/missionCreate");
        assertThat(model.asMap().get("mission")).isInstanceOf(DailyMission.class);
        assertThat(model.asMap().get("types")).isEqualTo(MissionType.values());
    }

    @Test
    void createMission_callsServiceAndRedirects() {
        String view = controller.createMission(mission);

        verify(dailyMissionService).create(mission);
        assertThat(view).isEqualTo("redirect:/daily-missions");
    }

    @Test
    void showEditForm_returnsMissionEditView() {
        UUID id = mission.getId();
        when(dailyMissionService.findById(id)).thenReturn(mission);

        Model model = new ExtendedModelMap();
        String view = controller.showEditForm(id, model);

        assertThat(view).isEqualTo("mission/missionEdit");
        assertThat(model.asMap().get("mission")).isEqualTo(mission);
        assertThat(model.asMap().get("types")).isEqualTo(MissionType.values());
    }

    @Test
    void updateMission_callsServiceAndRedirects() {
        UUID id = mission.getId();
        String view = controller.updateMission(id, mission);

        verify(dailyMissionService).update(id, mission);
        assertThat(view).isEqualTo("redirect:/daily-missions");
    }

    @Test
    void deleteMission_callsServiceAndRedirects() {
        UUID id = mission.getId();
        String view = controller.deleteMission(id);

        verify(dailyMissionService).delete(id);
        assertThat(view).isEqualTo("redirect:/daily-missions");
    }

    @Test
    void listMissions_setsIsAdminTrue_whenUserHasAdminRole() {
        GrantedAuthority adminRole = () -> "ROLE_ADMIN";
        when(authentication.getAuthorities()).thenAnswer(inv -> List.of(adminRole));
        when(missionTrackingService.getUserProgressToday(user)).thenReturn(Collections.emptyList());
        when(dailyMissionService.findAll()).thenReturn(Collections.emptyList());
        when(dailyMissionService.findActiveMissions()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        controller.listMissions(model, authentication);

        assertThat(model.asMap().get("isAdmin")).isEqualTo(true);
    }

    @Test
    void listMissions_findsUserByPhone_whenEmailNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.of(user));
        when(missionTrackingService.getUserProgressToday(user)).thenReturn(Collections.emptyList());
        when(dailyMissionService.findAll()).thenReturn(Collections.emptyList());
        when(dailyMissionService.findActiveMissions()).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.listMissions(model, authentication);

        assertThat(view).isEqualTo("mission/missionList");
    }

    @Test
    void listMissions_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.listMissions(new ExtendedModelMap(), authentication))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
