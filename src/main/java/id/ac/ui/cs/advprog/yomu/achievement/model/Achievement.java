package id.ac.ui.cs.advprog.yomu.achievement.model;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "achievements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    // UUID is preferred over auto-increment for distributed systems
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The display name, e.g., "Bookworm"
    @Column(nullable = false)
    private String name;

    // Explains what user needs to do, e.g., "Read 10 articles"
    @Column(nullable = false)
    private String description;

    // Category of this achievement
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type;

    // How many "things" the user needs to reach to unlock this
    // e.g., 10 for "Read 10 articles"
    @Column(nullable = false)
    @Builder.Default
    private int targetCount = 0;

    // Points awarded when the achievement is unlocked
    @Column(nullable = false)
    @Builder.Default
    private int points = 0;

    // URL or name of the badge icon
    private String badgeIcon;

    // Non-negative validation helpers
    public void setTargetCount(int targetCount) {
        if (targetCount < 0) {
            throw new IllegalArgumentException("targetCount must be non-negative");
        }
        this.targetCount = targetCount;
    }

    public void setPoints(int points) {
        if (points < 0) {
            throw new IllegalArgumentException("points must be non-negative");
        }
        this.points = points;
    }
}
