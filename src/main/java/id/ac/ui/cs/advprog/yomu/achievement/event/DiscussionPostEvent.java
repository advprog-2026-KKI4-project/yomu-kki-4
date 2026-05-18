package id.ac.ui.cs.advprog.yomu.achievement.event;

public class DiscussionPostEvent {

    private final String userIdentifier; // email or phone, matches authorId in DiscussionForum

    public DiscussionPostEvent(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() { return userIdentifier; }
}
