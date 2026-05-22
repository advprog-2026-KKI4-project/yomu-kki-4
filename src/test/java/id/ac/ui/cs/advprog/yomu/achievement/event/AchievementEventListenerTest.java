package id.ac.ui.cs.advprog.yomu.achievement.event;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AchievementEventListenerTest {

    @Mock
    private AchievementTrackingService achievementTrackingService;

    @Mock
    private MissionTrackingService missionTrackingService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AchievementEventListener listener;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("test@test.com").username("test").password("password123").build();
    }

    @Test
    void onQuizCompleted_triggersServices_whenUserFoundByEmail() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        listener.onQuizCompleted(new QuizCompletedEvent("test@test.com", 85.0));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.QUIZ);
        verify(missionTrackingService).incrementProgress(user, MissionType.QUIZ);
    }

    @Test
    void onQuizCompleted_triggersServices_whenUserFoundByPhone() {
        when(userRepository.findByEmail("08123456789")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("08123456789")).thenReturn(Optional.of(user));

        listener.onQuizCompleted(new QuizCompletedEvent("08123456789", 90.0));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.QUIZ);
        verify(missionTrackingService).incrementProgress(user, MissionType.QUIZ);
    }

    @Test
    void onQuizCompleted_doesNothing_whenUserNotFound() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("ghost@test.com")).thenReturn(Optional.empty());

        listener.onQuizCompleted(new QuizCompletedEvent("ghost@test.com", 70.0));

        verify(achievementTrackingService, never()).incrementProgress(any(), any());
        verify(missionTrackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void onReadingCompleted_triggersReadingServices() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        listener.onReadingCompleted(new ReadingCompletedEvent("test@test.com"));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.READING);
        verify(missionTrackingService).incrementProgress(user, MissionType.READING);
    }

    @Test
    void onDiscussionPost_triggersMissionOnly() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        listener.onDiscussionPost(new DiscussionPostEvent(1L));

        verify(missionTrackingService).incrementProgress(user, MissionType.DISCUSSION);
        verify(achievementTrackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void onDiscussionPost_doesNothing_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        listener.onDiscussionPost(new DiscussionPostEvent(99L));

        verify(missionTrackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void onLogin_triggersLoginMission() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        listener.onLogin(new LoginEvent("test@test.com"));

        verify(missionTrackingService).incrementProgress(user, MissionType.LOGIN);
        verify(achievementTrackingService, never()).incrementProgress(any(), any());
    }
}
