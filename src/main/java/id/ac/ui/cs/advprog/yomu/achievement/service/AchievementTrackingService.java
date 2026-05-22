package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;

import java.util.List;
import java.util.UUID;

public interface AchievementTrackingService {
    void incrementProgress(User user, AchievementType actionType);
    List<UserAchievementProgress> getUserAchievements(User user);
    // Use case 4: only completed achievements
    List<UserAchievementProgress> getUnlockedAchievements(User user);
    // Use case 5: another student's publicly-visible achievements
    List<UserAchievementProgress> getPublicAchievements(Long userId);
    // Use case 6: toggle an achievement's profile visibility
    void setShowOnProfile(User user, UUID progressId, boolean show);
}