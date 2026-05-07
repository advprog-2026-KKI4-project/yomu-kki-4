package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.AchievementRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserAchievementProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AchievementTrackingServiceImpl implements AchievementTrackingService {

    private final UserAchievementProgressRepository progressRepository;
    private final AchievementRepository achievementRepository;

    @Override
    @Transactional
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

                // TODO: Trigger reward logic here!
                // e.g., pointsService.addPoints(user, achievement.getPoints());
            }

            progressRepository.save(progress);
        }
    }

    @Override
    public List<UserAchievementProgress> getUserAchievements(User user) {
        // Returns all their tracked achievements to display on a profile page later
        return progressRepository.findAll().stream()
                .filter(p -> p.getUser().getId().equals(user.getId()))
                .toList();
    }
}