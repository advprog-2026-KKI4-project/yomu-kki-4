package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MissionTrackingServiceImplTest {

    @Mock
    private UserMissionProgressRepository progressRepository;

    @Mock
    private DailyMissionRepository dailyMissionRepository;

    @InjectMocks
    private MissionTrackingServiceImpl service;

    private User user;
    private DailyMission mission;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").username("test").password("password123").build();

        mission = DailyMission.builder()
                .id(UUID.randomUUID())
                .name("Daily Quiz")
                .description("Complete 3 quizzes today")
                .type(MissionType.QUIZ)
                .targetCount(3)
                .rewardPoints(20)
                .active(true)
                .build();
    }

    @Test
    void incrementProgress_createsNewRecord_whenNoExistingProgressToday() {
        when(dailyMissionRepository.findCurrentlyActive(any(LocalDateTime.class))).thenReturn(List.of(mission));
        when(progressRepository.findByUserAndMissionIdAndDate(eq(user), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.empty());

        service.incrementProgress(user, MissionType.QUIZ);

        ArgumentCaptor<UserMissionProgress> captor = ArgumentCaptor.forClass(UserMissionProgress.class);
        verify(progressRepository).save(captor.capture());

        UserMissionProgress saved = captor.getValue();
        assertThat(saved.getCurrentCount()).isEqualTo(1);
        assertThat(saved.isCompleted()).isFalse();
        assertThat(saved.getDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void incrementProgress_marksMissionCompleted_whenTargetReached() {
        UserMissionProgress existing = UserMissionProgress.builder()
                .user(user).mission(mission).currentCount(2).completed(false).date(LocalDate.now()).build();

        when(dailyMissionRepository.findCurrentlyActive(any(LocalDateTime.class))).thenReturn(List.of(mission));
        when(progressRepository.findByUserAndMissionIdAndDate(eq(user), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));

        service.incrementProgress(user, MissionType.QUIZ);

        assertThat(existing.getCurrentCount()).isEqualTo(3);
        assertThat(existing.isCompleted()).isTrue();
    }

    @Test
    void incrementProgress_skipsAlreadyCompletedMission() {
        UserMissionProgress existing = UserMissionProgress.builder()
                .user(user).mission(mission).currentCount(3).completed(true).date(LocalDate.now()).build();

        when(dailyMissionRepository.findCurrentlyActive(any(LocalDateTime.class))).thenReturn(List.of(mission));
        when(progressRepository.findByUserAndMissionIdAndDate(eq(user), eq(mission.getId()), any(LocalDate.class)))
                .thenReturn(Optional.of(existing));

        service.incrementProgress(user, MissionType.QUIZ);

        verify(progressRepository, never()).save(any());
    }

    @Test
    void incrementProgress_skipsMission_whenTypeDoesNotMatch() {
        when(dailyMissionRepository.findCurrentlyActive(any(LocalDateTime.class))).thenReturn(List.of(mission));

        service.incrementProgress(user, MissionType.READING);

        verify(progressRepository, never()).save(any());
    }

    @Test
    void getUserProgressToday_delegatesToRepository() {
        List<UserMissionProgress> expected = List.of(
                UserMissionProgress.builder().user(user).mission(mission).currentCount(1).completed(true).date(LocalDate.now()).build()
        );
        when(progressRepository.findByUserAndDate(user, LocalDate.now())).thenReturn(expected);

        List<UserMissionProgress> result = service.getUserProgressToday(user);

        assertThat(result).isEqualTo(expected);
    }
}
