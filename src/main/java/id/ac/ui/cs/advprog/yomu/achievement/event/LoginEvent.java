package id.ac.ui.cs.advprog.yomu.achievement.event;

public class LoginEvent {

    private final String userIdentifier; // email or phone, matches auth.getName()

    public LoginEvent(String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }

    public String getUserIdentifier() { return userIdentifier; }
}
