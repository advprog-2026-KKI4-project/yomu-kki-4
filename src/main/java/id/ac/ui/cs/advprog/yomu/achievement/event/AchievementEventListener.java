package id.ac.ui.cs.advprog.yomu.achievement.event;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AchievementEventListener {

    private final AchievementTrackingService achievementTrackingService;
    private final MissionTrackingService missionTrackingService;
    private final UserRepository userRepository;
    private final MeterRegistry meterRegistry;

    @EventListener
    @Timed(value = "achievement.event.quiz_completed", description = "Time taken to process a quiz completed event")
    public void onQuizCompleted(QuizCompletedEvent event) {
        User user = resolveUser(event.getUserIdentifier());
        if (user == null) {
            log.warn("Achievement trigger skipped: no user found for identifier '{}'", event.getUserIdentifier());
            incrementSkipCounter("QUIZ");
            return;
        }
        achievementTrackingService.incrementProgress(user, AchievementType.QUIZ);
        missionTrackingService.incrementProgress(user, MissionType.QUIZ);
    }

    @EventListener
    @Timed(value = "achievement.event.reading_completed", description = "Time taken to process a reading completed event")
    public void onReadingCompleted(ReadingCompletedEvent event) {
        User user = resolveUser(event.getUserIdentifier());
        if (user == null) {
            log.warn("Achievement trigger skipped: no user found for identifier '{}'", event.getUserIdentifier());
            incrementSkipCounter("READING");
            return;
        }
        achievementTrackingService.incrementProgress(user, AchievementType.READING);
        missionTrackingService.incrementProgress(user, MissionType.READING);
    }

    @EventListener
    @Timed(value = "achievement.event.discussion_post", description = "Time taken to process a discussion post event")
    public void onDiscussionPost(DiscussionPostEvent event) {
        User user = userRepository.findById(event.getUserId()).orElse(null);
        if (user == null) {
            log.warn("Achievement trigger skipped: no user found for id '{}'", event.getUserId());
            incrementSkipCounter("DISCUSSION");
            return;
        }
        achievementTrackingService.incrementProgress(user, AchievementType.DISCUSSION);
        missionTrackingService.incrementProgress(user, MissionType.DISCUSSION);
    }

    @EventListener
    @Timed(value = "achievement.event.login", description = "Time taken to process a login event")
    public void onLogin(LoginEvent event) {
        User user = resolveUser(event.getUserIdentifier());
        if (user == null) {
            log.warn("Achievement trigger skipped: no user found for identifier '{}'", event.getUserIdentifier());
            incrementSkipCounter("LOGIN");
            return;
        }
        achievementTrackingService.incrementProgress(user, AchievementType.LOGIN);
        missionTrackingService.incrementProgress(user, MissionType.LOGIN);
    }

    private User resolveUser(String identifier) {
        return userRepository.findByEmail(identifier)
                .or(() -> userRepository.findByPhone(identifier))
                .orElse(null);
    }

    private void incrementSkipCounter(String eventType) {
        Counter.builder("achievement.event.skipped")
                .tag("event_type", eventType)
                .description("Number of achievement events skipped due to unresolvable user")
                .register(meterRegistry)
                .increment();
    }
}
