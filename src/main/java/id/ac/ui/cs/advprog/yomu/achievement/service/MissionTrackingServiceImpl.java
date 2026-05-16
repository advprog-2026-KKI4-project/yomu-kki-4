package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionTrackingServiceImpl implements MissionTrackingService {

    private final UserMissionProgressRepository progressRepository;
    private final DailyMissionRepository dailyMissionRepository;

    @Override
    @Transactional
    public void incrementProgress(User user, MissionType actionType) {
        LocalDate today = LocalDate.now();

        // 1. Get active missions that match what the user just did
        List<DailyMission> relevantMissions = dailyMissionRepository.findByActiveTrue()
                .stream()
                .filter(m -> m.getType() == actionType)
                .toList();

        for (DailyMission mission : relevantMissions) {
            // 2. Fetch today's progress, or create a new record starting at 0
            UserMissionProgress progress = progressRepository
                    .findByUserAndMissionIdAndDate(user, mission.getId(), today)
                    .orElseGet(() -> UserMissionProgress.builder()
                            .user(user)
                            .mission(mission)
                            .currentCount(0)
                            .completed(false)
                            .date(today)
                            .build());

            // 3. Skip if already completed today
            if (progress.isCompleted()) {
                continue;
            }

            // 4. Increment the counter
            progress.setCurrentCount(progress.getCurrentCount() + 1);

            // 5. Check if they hit the target
            if (progress.getCurrentCount() >= mission.getTargetCount()) {
                progress.setCompleted(true);
                // NOTE FOR MILESTONE 3: Trigger reward logic here!
                // e.g., pointsService.addPoints(user, mission.getRewardPoints());
            }

            progressRepository.save(progress);
        }
    }

    @Override
    public List<UserMissionProgress> getUserProgressToday(User user) {
        return progressRepository.findByUserAndDate(user, LocalDate.now());
    }
}