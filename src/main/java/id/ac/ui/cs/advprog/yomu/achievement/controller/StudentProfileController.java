package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.dto.StudentProfileResponse;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StudentProfileController {

    private final UserRepository userRepository;
    private final AchievementTrackingService trackingService;

    /**
     * GET /api/users/{userId}/profile
     * Returns a student's public profile — basic info + unlocked achievements they chose to display.
     * Used by Clan, League, and Social modules to render member profile cards.
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<StudentProfileResponse.PublicAchievement> achievements =
                trackingService.getPublicAchievements(userId).stream()
                        .map(this::toPublicAchievement)
                        .toList();

        StudentProfileResponse profile = StudentProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .achievements(achievements)
                .build();

        return ResponseEntity.ok(profile);
    }

    private StudentProfileResponse.PublicAchievement toPublicAchievement(UserAchievementProgress p) {
        return StudentProfileResponse.PublicAchievement.builder()
                .name(p.getAchievement().getName())
                .description(p.getAchievement().getDescription())
                .type(p.getAchievement().getType().name())
                .points(p.getAchievement().getPoints())
                .badgeIcon(p.getAchievement().getBadgeIcon())
                .unlockedAt(p.getUnlockedAt())
                .build();
    }
}
