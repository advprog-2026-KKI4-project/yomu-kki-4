package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.dto.StudentProfileResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AchievementTrackingService trackingService;

    @InjectMocks
    private StudentProfileController controller;

    private User user;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("alice")
                .firstName("Alice")
                .lastName("Smith")
                .avatarUrl("avatar.png")
                .bio("Avid reader")
                .email("alice@test.com")
                .password("password123")
                .build();

        achievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Bookworm")
                .description("Read 10 articles")
                .type(AchievementType.READING)
                .targetCount(10)
                .points(50)
                .badgeIcon("book.svg")
                .build();
    }

    @Test
    void getStudentProfile_returnsProfileWithUserInfo() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(trackingService.getPublicAchievements(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<StudentProfileResponse> response = controller.getStudentProfile(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        StudentProfileResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getUsername()).isEqualTo("alice");
        assertThat(body.getFirstName()).isEqualTo("Alice");
        assertThat(body.getLastName()).isEqualTo("Smith");
        assertThat(body.getAvatarUrl()).isEqualTo("avatar.png");
        assertThat(body.getBio()).isEqualTo("Avid reader");
    }

    @Test
    void getStudentProfile_returnsPublicAchievements() {
        LocalDateTime unlockedAt = LocalDateTime.now();
        UserAchievementProgress progress = UserAchievementProgress.builder()
                .user(user).achievement(achievement)
                .currentCount(10).unlocked(true).showOnProfile(true)
                .unlockedAt(unlockedAt)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(trackingService.getPublicAchievements(1L)).thenReturn(List.of(progress));

        ResponseEntity<StudentProfileResponse> response = controller.getStudentProfile(1L);

        List<StudentProfileResponse.PublicAchievement> achievements = response.getBody().getAchievements();
        assertThat(achievements).hasSize(1);
        StudentProfileResponse.PublicAchievement a = achievements.get(0);
        assertThat(a.getName()).isEqualTo("Bookworm");
        assertThat(a.getDescription()).isEqualTo("Read 10 articles");
        assertThat(a.getType()).isEqualTo("READING");
        assertThat(a.getPoints()).isEqualTo(50);
        assertThat(a.getBadgeIcon()).isEqualTo("book.svg");
        assertThat(a.getUnlockedAt()).isEqualTo(unlockedAt);
    }

    @Test
    void getStudentProfile_returnsEmptyAchievements_whenNonePublic() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(trackingService.getPublicAchievements(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<StudentProfileResponse> response = controller.getStudentProfile(1L);

        assertThat(response.getBody().getAchievements()).isEmpty();
    }

    @Test
    void getStudentProfile_throws_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getStudentProfile(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("99");
    }
}
