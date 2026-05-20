package id.ac.ui.cs.advprog.yomu.learningandquiz.model;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

public class QuizAttempt {
    private String id;
    private String userId;
    private String materialId;
    private double score;
    private long durationInSeconds;
    private LocalDateTime completedAt;
    private List<Integer> answers;

    public QuizAttempt() {
        this.id = UUID.randomUUID().toString();
        this.completedAt = LocalDateTime.now();
    }


    public QuizAttempt(String userId, String materialId, double score, long duration, List<Integer> answers) {
        this();
        this.userId = userId;
        this.materialId = materialId;
        this.score = score;
        this.durationInSeconds = duration;
        this.answers = answers;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMaterialId() { return materialId; }
    public void setMaterialId(String materialId) { this.materialId = materialId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public long getDurationInSeconds() { return durationInSeconds; }
    public void setDurationInSeconds(long durationInSeconds) { this.durationInSeconds = durationInSeconds; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public List<Integer> getAnswers() { return answers; }
    public void setAnswers(List<Integer> answers) { this.answers = answers; }
}