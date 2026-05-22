package id.ac.ui.cs.advprog.yomu.achievement.event;

public class ReadingCompletedEvent {

    private final String userIdentifier; // email or phone, matches auth.getName()

    public ReadingCompletedEvent(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() { return userIdentifier; }
}
