package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.repository.DailyMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyMissionServiceImpl implements DailyMissionService {

    private final DailyMissionRepository dailyMissionRepository;

    @Override
    public DailyMission create(DailyMission mission) {
        return dailyMissionRepository.save(mission);
    }

    @Override
    public List<DailyMission> findAll() {
        return dailyMissionRepository.findAll();
    }

    @Override
    public DailyMission findById(UUID id) {
        return dailyMissionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Daily Mission not found with id: " + id));
    }

    @Override
    public List<DailyMission> findByType(MissionType type) {
        return dailyMissionRepository.findByType(type);
    }

    @Override
    public List<DailyMission> findActiveMissions() {
        return dailyMissionRepository.findByActiveTrue();
    }

    @Override
    public DailyMission update(UUID id, DailyMission updated) {
        DailyMission existing = findById(id);

        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setType(updated.getType());
        existing.setTargetCount(updated.getTargetCount());
        existing.setRewardPoints(updated.getRewardPoints());
        existing.setActive(updated.isActive());

        return dailyMissionRepository.save(existing);
    }

    @Override
    public void delete(UUID id) {
        DailyMission existing = findById(id);
        dailyMissionRepository.delete(existing);
    }
}