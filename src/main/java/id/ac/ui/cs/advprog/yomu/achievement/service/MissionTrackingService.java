package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;

import java.util.List;

public interface MissionTrackingService {
    void incrementProgress(User user, MissionType actionType);
    List<UserMissionProgress> getUserProgressToday(User user);
}