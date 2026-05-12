package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;

import java.util.List;
import java.util.UUID;

public interface AchievementService {

    Achievement create(Achievement achievement);

    List<Achievement> findAll();

    Achievement findById(UUID id);

    List<Achievement> findByType(AchievementType type);

    Achievement update(UUID id, Achievement achievement);

    void delete(UUID id);
}