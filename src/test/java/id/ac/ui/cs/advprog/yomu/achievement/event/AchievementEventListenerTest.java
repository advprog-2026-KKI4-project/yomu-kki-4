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

import static org.assertj.core.api.Assertions.assertThat;
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

        QuizCompletedEvent event = new QuizCompletedEvent("test@test.com", 85.0);
        assertThat(event.getScore()).isEqualTo(85.0);
        listener.onQuizCompleted(event);

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
    void onDiscussionPost_triggersBothServices() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        listener.onDiscussionPost(new DiscussionPostEvent(1L));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.DISCUSSION);
        verify(missionTrackingService).incrementProgress(user, MissionType.DISCUSSION);
    }

    @Test
    void onDiscussionPost_doesNothing_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        listener.onDiscussionPost(new DiscussionPostEvent(99L));

        verify(missionTrackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void onReadingCompleted_triggersServices_whenUserFoundByPhone() {
        when(userRepository.findByEmail("08123456789")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("08123456789")).thenReturn(Optional.of(user));

        listener.onReadingCompleted(new ReadingCompletedEvent("08123456789"));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.READING);
        verify(missionTrackingService).incrementProgress(user, MissionType.READING);
    }

    @Test
    void onReadingCompleted_doesNothing_whenUserNotFound() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("ghost@test.com")).thenReturn(Optional.empty());

        listener.onReadingCompleted(new ReadingCompletedEvent("ghost@test.com"));

        verify(achievementTrackingService, never()).incrementProgress(any(), any());
        verify(missionTrackingService, never()).incrementProgress(any(), any());
    }

    @Test
    void onLogin_triggersBothServices() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        listener.onLogin(new LoginEvent("test@test.com"));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.LOGIN);
        verify(missionTrackingService).incrementProgress(user, MissionType.LOGIN);
    }

    @Test
    void onLogin_triggersServices_whenUserFoundByPhone() {
        when(userRepository.findByEmail("08123456789")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("08123456789")).thenReturn(Optional.of(user));

        listener.onLogin(new LoginEvent("08123456789"));

        verify(achievementTrackingService).incrementProgress(user, AchievementType.LOGIN);
        verify(missionTrackingService).incrementProgress(user, MissionType.LOGIN);
    }

    @Test
    void onLogin_doesNothing_whenUserNotFound() {
        when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("ghost@test.com")).thenReturn(Optional.empty());

        listener.onLogin(new LoginEvent("ghost@test.com"));

        verify(achievementTrackingService, never()).incrementProgress(any(), any());
        verify(missionTrackingService, never()).incrementProgress(any(), any());
    }
}
