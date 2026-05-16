package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;

import java.util.List;

public interface AchievementTrackingService {
    void incrementProgress(User user, AchievementType actionType);
    List<UserAchievementProgress> getUserAchievements(User user);
}