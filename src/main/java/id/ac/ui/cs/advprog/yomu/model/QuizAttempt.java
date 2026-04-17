package id.ac.ui.cs.advprog.yomu.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuizAttempt {
    private final String id;
    private final String userId;
    private final String materialId;
    private final double score;
    private final long durationInSeconds;
    private final LocalDateTime completedAt;

    public QuizAttempt(String userId, String materialId, double score, long durationInSeconds) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.materialId = materialId;
        this.score = score;
        this.durationInSeconds = durationInSeconds;
        this.completedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getMaterialId() { return materialId; }
    public double getScore() { return score; }
    public long getDuration() { return durationInSeconds; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}