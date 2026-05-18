package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementTrackingServiceImplTest {

    @Mock
    private UserAchievementProgressRepository progressRepository;

    @Mock
    private AchievementRepository achievementRepository;

    @InjectMocks
    private AchievementTrackingServiceImpl service;

    private User user;
    private Achievement achievement;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").username("test").password("password123").build();

        achievement = Achievement.builder()
                .id(UUID.randomUUID())
                .name("Quiz Novice")
                .description("Complete 3 quizzes")
                .type(AchievementType.QUIZ)
                .targetCount(3)
                .points(50)
                .build();
    }

    @Test
    void incrementProgress_createsNewRecord_whenNoExistingProgress() {
        when(achievementRepository.findByType(AchievementType.QUIZ)).thenReturn(List.of(achievement));
        when(progressRepository.findByUserAndAchievementId(user, achievement.getId()))
                .thenReturn(Optional.empty());

        service.incrementProgress(user, AchievementType.QUIZ);

        ArgumentCaptor<UserAchievementProgress> captor = ArgumentCaptor.forClass(UserAchievementProgress.class);
        verify(progressRepository).save(captor.capture());

        UserAchievementProgress saved = captor.getValue();
        assertThat(saved.getCurrentCount()).isEqualTo(1);
        assertThat(saved.isUnlocked()).isFalse();
    }

    @Test
    void incrementProgress_incrementsExistingCount() {
        UserAchievementProgress existing = UserAchievementProgress.builder()
                .user(user).achievement(achievement).currentCount(1).unlocked(false).build();

        when(achievementRepository.findByType(AchievementType.QUIZ)).thenReturn(List.of(achievement));
        when(progressRepository.findByUserAndAchievementId(user, achievement.getId()))
                .thenReturn(Optional.of(existing));

        service.incrementProgress(user, AchievementType.QUIZ);

        assertThat(existing.getCurrentCount()).isEqualTo(2);
        verify(progressRepository).save(existing);
    }

    @Test
    void incrementProgress_unlocksAchievement_whenTargetReached() {
        UserAchievementProgress existing = UserAchievementProgress.builder()
                .user(user).achievement(achievement).currentCount(2).unlocked(false).build();

        when(achievementRepository.findByType(AchievementType.QUIZ)).thenReturn(List.of(achievement));
        when(progressRepository.findByUserAndAchievementId(user, achievement.getId()))
                .thenReturn(Optional.of(existing));

        service.incrementProgress(user, AchievementType.QUIZ);

        assertThat(existing.getCurrentCount()).isEqualTo(3);
        assertThat(existing.isUnlocked()).isTrue();
        assertThat(existing.getUnlockedAt()).isNotNull();
    }

    @Test
    void incrementProgress_skipsAlreadyUnlockedAchievement() {
        UserAchievementProgress existing = UserAchievementProgress.builder()
                .user(user).achievement(achievement).currentCount(3).unlocked(true).build();

        when(achievementRepository.findByType(AchievementType.QUIZ)).thenReturn(List.of(achievement));
        when(progressRepository.findByUserAndAchievementId(user, achievement.getId()))
                .thenReturn(Optional.of(existing));

        service.incrementProgress(user, AchievementType.QUIZ);

        verify(progressRepository, never()).save(any());
    }

    @Test
    void getUserAchievements_delegatesToRepository() {
        List<UserAchievementProgress> expected = List.of(
                UserAchievementProgress.builder().user(user).achievement(achievement).currentCount(1).unlocked(false).build()
        );
        when(progressRepository.findByUser(user)).thenReturn(expected);

        List<UserAchievementProgress> result = service.getUserAchievements(user);

        assertThat(result).isEqualTo(expected);
        verify(progressRepository).findByUser(user);
    }
}
