package id.ac.ui.cs.advprog.yomu.achievement.event;

public class DiscussionPostEvent {

    private final Long userId;

    public DiscussionPostEvent(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() { return userId; }
}
