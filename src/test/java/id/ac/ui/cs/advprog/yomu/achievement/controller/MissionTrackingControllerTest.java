package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MissionTrackingControllerTest {

    @Mock
    private MissionTrackingService trackingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MissionTrackingController controller;

    private User user;
    private Principal principal;
    private UserMissionProgress missionProgress;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("alice@test.com").username("alice").password("pass123").build();
        principal = () -> "alice@test.com";

        DailyMission mission = DailyMission.builder()
                .id(UUID.randomUUID()).name("Daily Reader").description("Read 1 article")
                .type(MissionType.READING).targetCount(1).rewardPoints(10).active(true).build();
        missionProgress = UserMissionProgress.builder()
                .user(user).mission(mission).currentCount(0).completed(false).date(LocalDate.of(2026, 1, 1)).build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
    }

    @Test
    void getTodayProgress_returns200WithList() {
        when(trackingService.getUserProgressToday(user)).thenReturn(List.of(missionProgress));

        ResponseEntity<List<UserMissionProgress>> response = controller.getTodayProgress(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(missionProgress);
    }

    @Test
    void getTodayProgress_throwsWhenPrincipalIsNull() {
        assertThatThrownBy(() -> controller.getTodayProgress(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void triggerAction_withValidType_returns200() {
        ResponseEntity<String> response = controller.triggerAction(principal, "READING");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("Progress updated");
        verify(trackingService).incrementProgress(user, MissionType.READING);
    }

    @Test
    void triggerAction_withLowercaseType_isCaseInsensitive() {
        ResponseEntity<String> response = controller.triggerAction(principal, "quiz");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(trackingService).incrementProgress(user, MissionType.QUIZ);
    }

    @Test
    void triggerAction_withInvalidType_returns400() {
        ResponseEntity<String> response = controller.triggerAction(principal, "INVALID_TYPE");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Invalid mission type");
        verify(trackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void getTodayProgress_findsUserByPhone_whenEmailNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.of(user));
        when(trackingService.getUserProgressToday(user)).thenReturn(List.of(missionProgress));

        ResponseEntity<List<UserMissionProgress>> response = controller.getTodayProgress(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(missionProgress);
    }

    @Test
    void getTodayProgress_throwsWhenUserNotFoundByEither() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getTodayProgress(principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
