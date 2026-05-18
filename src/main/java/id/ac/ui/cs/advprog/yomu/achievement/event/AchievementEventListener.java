package id.ac.ui.cs.advprog.yomu.achievement.event;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
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

    @EventListener
    public void onQuizCompleted(QuizCompletedEvent event) {
        User user = userRepository.findByEmail(event.getUserIdentifier())
                .or(() -> userRepository.findByPhone(event.getUserIdentifier()))
                .orElse(null);

        if (user == null) {
            log.warn("Achievement trigger skipped: no user found for identifier '{}'", event.getUserIdentifier());
            return;
        }

        achievementTrackingService.incrementProgress(user, AchievementType.QUIZ);
        missionTrackingService.incrementProgress(user, MissionType.QUIZ);
    }
}