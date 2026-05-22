package id.ac.ui.cs.advprog.yomu.achievement.event;

public class MissionCompletedEvent {

    private final Long userId;
    private final int rewardPoints;

    public MissionCompletedEvent(Long userId, int rewardPoints) {
        this.userId = userId;
        this.rewardPoints = rewardPoints;
    }

    public Long getUserId() { return userId; }
    public int getRewardPoints() { return rewardPoints; }
}
