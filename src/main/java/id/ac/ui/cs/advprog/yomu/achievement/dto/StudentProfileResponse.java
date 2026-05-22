package id.ac.ui.cs.advprog.yomu.achievement.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
public class StudentProfileResponse {

    Long id;
    String username;
    String firstName;
    String lastName;
    String avatarUrl;
    String bio;
    List<PublicAchievement> achievements;

    @Value
    @Builder
    public static class PublicAchievement {
        String name;
        String description;
        String type;
        int points;
        String badgeIcon;
        LocalDateTime unlockedAt;
    }
}
