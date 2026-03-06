package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;

import java.util.List;
import java.util.UUID;

public interface DailyMissionService {

    DailyMission create(DailyMission mission);

    List<DailyMission> findAll();

    DailyMission findById(UUID id);

    List<DailyMission> findByType(MissionType type);

    List<DailyMission> findActiveMissions();

    DailyMission update(UUID id, DailyMission mission);

    void delete(UUID id);
}