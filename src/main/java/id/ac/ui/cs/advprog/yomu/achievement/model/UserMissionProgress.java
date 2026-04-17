package id.ac.ui.cs.advprog.yomu.achievement.model;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "user_mission_progress")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMissionProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Direct relationship to Adra's User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_id", nullable = false)
    private DailyMission mission;

    @Column(nullable = false)
    private int currentCount;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private LocalDate date;
}