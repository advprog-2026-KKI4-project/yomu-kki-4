package id.ac.ui.cs.advprog.yomu.clans.event;

import id.ac.ui.cs.advprog.yomu.achievement.event.MissionCompletedEvent;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClanEventListener {

    private final ClanService clanService;

    @EventListener
    public void onMissionCompleted(MissionCompletedEvent event) {
        if (event.getRewardPoints() <= 0) return;
        try {
            clanService.addPoints(event.getUserId(), event.getRewardPoints());
            log.debug("Added {} points to user {} from mission completion", event.getRewardPoints(), event.getUserId());
        } catch (Exception e) {
            log.warn("Could not add mission points for user {}: {}", event.getUserId(), e.getMessage());
        }
    }
}
