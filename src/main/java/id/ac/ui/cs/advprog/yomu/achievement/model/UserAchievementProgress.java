package id.ac.ui.cs.advprog.yomu.achievement.model;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

// temp, for testing
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_achievement_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAchievementProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Tell Jackson to completely ignore the User when turning this into JSON
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Tell Jackson to ignore Hibernate's invisible lazy-loading proxy variables
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement;

    // How far along they are (e.g., 3 out of 5 quizzes passed)
    @Column(nullable = false)
    private int currentCount;

    // Have they finished it completely?
    @Column(nullable = false)
    private boolean unlocked;

    // When did they unlock it?
    private LocalDateTime unlockedAt;
}