package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.repository.AchievementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor  // Lombok generates constructor with all 'final' fields
public class AchievementServiceImpl implements AchievementService {

    // 'final' + @RequiredArgsConstructor = constructor injection (best practice)
    private final AchievementRepository achievementRepository;

    @Override
    public Achievement create(Achievement achievement) {
        return achievementRepository.save(achievement);
    }

    @Override
    public List<Achievement> findAll() {
        return achievementRepository.findAll();
    }

    @Override
    public Achievement findById(UUID id) {
        return achievementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Achievement not found with id: " + id));
    }

    @Override
    public List<Achievement> findByType(AchievementType type) {
        return achievementRepository.findByType(type);
    }

    @Override
    public Achievement update(UUID id, Achievement updated) {
        Achievement existing = findById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setType(updated.getType());
        existing.setTargetCount(updated.getTargetCount());
        existing.setPoints(updated.getPoints());
        existing.setBadgeIcon(updated.getBadgeIcon());

        return achievementRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        Achievement existing = findById(id);  // throws if not found
        achievementRepository.delete(existing);
    }
}