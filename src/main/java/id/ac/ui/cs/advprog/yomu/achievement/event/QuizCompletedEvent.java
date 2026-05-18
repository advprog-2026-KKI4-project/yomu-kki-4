package id.ac.ui.cs.advprog.yomu.achievement.event;

public class QuizCompletedEvent {

    private final String userIdentifier; // email or phone, matches auth.getName()
    private final double score;

    public QuizCompletedEvent(String userIdentifier, double score) {
        this.userIdentifier = userIdentifier;
        this.score = score;
    }

    public String getUserIdentifier() { return userIdentifier; }
    public double getScore() { return score; }
}
