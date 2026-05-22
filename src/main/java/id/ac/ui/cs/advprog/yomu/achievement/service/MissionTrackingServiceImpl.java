package id.ac.ui.cs.advprog.yomu.achievement.service;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.event.MissionCompletedEvent;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.achievement.repository.DailyMissionRepository;
import id.ac.ui.cs.advprog.yomu.achievement.repository.UserMissionProgressRepository;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionTrackingServiceImpl implements MissionTrackingService {

    private final UserMissionProgressRepository progressRepository;
    private final DailyMissionRepository dailyMissionRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MeterRegistry meterRegistry;

    @Override
    @Transactional
    @Timed(value = "mission.increment_progress", description = "Time taken to process daily mission progress increment")
    public void incrementProgress(User user, MissionType actionType) {
        LocalDate today = LocalDate.now();

        // 1. Get currently active missions (respects active flag + activeFrom/activeTo window)
        List<DailyMission> relevantMissions = dailyMissionRepository.findCurrentlyActive(LocalDateTime.now())
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
                eventPublisher.publishEvent(new MissionCompletedEvent(user.getId(), mission.getRewardPoints()));

                Counter.builder("mission.completed")
                        .tag("type", mission.getType().name())
                        .description("Number of daily missions completed by users")
                        .register(meterRegistry)
                        .increment();
            }

            Counter.builder("mission.progress.incremented")
                    .tag("type", mission.getType().name())
                    .description("Number of times mission progress was incremented")
                    .register(meterRegistry)
                    .increment();

            progressRepository.save(progress);
        }
    }

    @Override
    @Timed(value = "mission.get_user_progress", description = "Time taken to retrieve today's mission progress for a user")
    public List<UserMissionProgress> getUserProgressToday(User user) {
        return progressRepository.findByUserAndDate(user, LocalDate.now());
    }

    @Override
    public long getCompletedMissionCountForUsers(List<Long> userIds, LocalDate date) {
        if (userIds == null || userIds.isEmpty())
            return 0;
        return progressRepository.countUsersWithCompletedMissions(userIds, date);
    }
}