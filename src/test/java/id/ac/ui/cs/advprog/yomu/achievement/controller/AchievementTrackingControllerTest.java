package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AchievementTrackingControllerTest {

    @Mock
    private AchievementTrackingService trackingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementTrackingController controller;

    private User user;
    private Principal principal;
    private UserAchievementProgress progress;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("alice@test.com").username("alice").password("pass123").build();
        principal = () -> "alice@test.com";

        Achievement achievement = Achievement.builder()
                .id(UUID.randomUUID()).name("Bookworm").description("Read 10 articles")
                .type(AchievementType.READING).targetCount(10).points(50).build();
        progress = UserAchievementProgress.builder()
                .user(user).achievement(achievement).currentCount(5).unlocked(false).build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
    }

    @Test
    void getMyAchievements_returns200WithList() {
        when(trackingService.getUserAchievements(user)).thenReturn(List.of(progress));

        ResponseEntity<List<UserAchievementProgress>> response = controller.getMyAchievements(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(progress);
    }

    @Test
    void getMyAchievements_throwsWhenPrincipalIsNull() {
        assertThatThrownBy(() -> controller.getMyAchievements(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Unauthorized");
    }

    @Test
    void getMyUnlockedAchievements_returns200WithUnlockedOnly() {
        UserAchievementProgress unlocked = UserAchievementProgress.builder()
                .user(user).achievement(progress.getAchievement()).currentCount(10).unlocked(true).build();
        when(trackingService.getUnlockedAchievements(user)).thenReturn(List.of(unlocked));

        ResponseEntity<List<UserAchievementProgress>> response = controller.getMyUnlockedAchievements(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).isUnlocked()).isTrue();
    }

    @Test
    void getMyUnlockedAchievements_returnsEmptyList_whenNoneUnlocked() {
        when(trackingService.getUnlockedAchievements(user)).thenReturn(Collections.emptyList());

        ResponseEntity<List<UserAchievementProgress>> response = controller.getMyUnlockedAchievements(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getPublicAchievements_returns200ForAnyUserId() {
        when(trackingService.getPublicAchievements(42L)).thenReturn(List.of(progress));

        ResponseEntity<List<UserAchievementProgress>> response = controller.getPublicAchievements(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(progress);
    }

    @Test
    void setVisibility_returns204NoContent() {
        UUID progressId = UUID.randomUUID();
        Map<String, Boolean> body = Map.of("showOnProfile", true);

        ResponseEntity<Void> response = controller.setVisibility(progressId, body, principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(trackingService).setShowOnProfile(user, progressId, true);
    }

    @Test
    void setVisibility_passesFalseWhenShowOnProfileMissing() {
        UUID progressId = UUID.randomUUID();
        Map<String, Boolean> body = Map.of();

        controller.setVisibility(progressId, body, principal);

        verify(trackingService).setShowOnProfile(user, progressId, false);
    }

    @Test
    void getMyAchievements_findsUserByPhone_whenEmailNotFound() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.of(user));
        when(trackingService.getUserAchievements(user)).thenReturn(List.of(progress));

        ResponseEntity<List<UserAchievementProgress>> response = controller.getMyAchievements(principal);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(progress);
    }

    @Test
    void getMyAchievements_throwsWhenUserNotFoundByEither() {
        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("alice@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getMyAchievements(principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
