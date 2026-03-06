package id.ac.ui.cs.advprog.yomu.achievement.model;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "daily_missions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyMission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // e.g., "Daily Reader"
    @Column(nullable = false)
    private String name;

    // e.g., "Read at least 1 article today"
    @Column(nullable = false)
    private String description;

    // Category of this mission
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MissionType type;

    // How many actions needed to complete, e.g., 1 for "Read 1 article"
    @Column(nullable = false)
    private int targetCount;

    // Points rewarded upon daily completion
    @Column(nullable = false)
    private int rewardPoints;

    // Whether this mission is currently active/available
    @Column(nullable = false)
    private boolean active;
}