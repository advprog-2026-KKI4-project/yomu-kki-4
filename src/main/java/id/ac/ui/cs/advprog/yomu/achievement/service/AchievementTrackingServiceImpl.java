package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AchievementTrackingServiceImpl implements AchievementTrackingService {

    private final UserAchievementProgressRepository progressRepository;
    private final AchievementRepository achievementRepository;
    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    @Timed(value = "achievement.increment_progress", description = "Time taken to process achievement progress increment")
    public void incrementProgress(User user, AchievementType actionType) {
        // 1. Get all achievements of the triggered type (e.g., QUIZ)
        List<Achievement> relevantAchievements = achievementRepository.findByType(actionType);

        for (Achievement achievement : relevantAchievements) {
            // 2. Fetch the user's current progress, or start at 0 if they haven't started
            UserAchievementProgress progress = progressRepository
                    .findByUserAndAchievementId(user, achievement.getId())
                    .orElseGet(() -> UserAchievementProgress.builder()
                            .user(user)
                            .achievement(achievement)
                            .currentCount(0)
                            .unlocked(false)
                            .build());

            // 3. Skip if they already unlocked it
            if (progress.isUnlocked()) {
                continue;
            }

            // 4. Increment their progress
            progress.setCurrentCount(progress.getCurrentCount() + 1);

            // 5. Check if they hit the target to unlock it!
            if (progress.getCurrentCount() >= achievement.getTargetCount()) {
                progress.setUnlocked(true);
                progress.setUnlockedAt(LocalDateTime.now());

                Counter.builder("achievement.unlocked")
                        .tag("type", achievement.getType().name())
                        .description("Number of achievements unlocked by users")
                        .register(meterRegistry)
                        .increment();
            }

            Counter.builder("achievement.progress.incremented")
                    .tag("type", achievement.getType().name())
                    .description("Number of times achievement progress was incremented")
                    .register(meterRegistry)
                    .increment();

            progressRepository.save(progress);
        }
    }

    @Override
    public List<UserAchievementProgress> getUserAchievements(User user) {
        return progressRepository.findByUser(user);
    }

    @Override
    public List<UserAchievementProgress> getUnlockedAchievements(User user) {
        return progressRepository.findByUserAndUnlockedTrue(user);
    }

    @Override
    @Timed(value = "achievement.get_public", description = "Time taken to fetch public achievements for a student profile")
    public List<UserAchievementProgress> getPublicAchievements(Long userId) {
        return progressRepository.findByUser_IdAndShowOnProfileTrueAndUnlockedTrue(userId);
    }

    @Override
    @Transactional
    public void setShowOnProfile(User user, UUID progressId, boolean show) {
        UserAchievementProgress progress = progressRepository
                .findByIdAndUser(progressId, user)
                .orElseThrow(() -> new IllegalArgumentException("Achievement progress not found"));
        if (!progress.isUnlocked()) {
            throw new IllegalStateException("Cannot show a locked achievement on profile");
        }
        progress.setShowOnProfile(show);
        progressRepository.save(progress);
    }
}