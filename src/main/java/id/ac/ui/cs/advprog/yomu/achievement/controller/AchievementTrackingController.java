package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/achievements/tracking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AchievementTrackingController {

    private final AchievementTrackingService trackingService;
    private final UserRepository userRepository;

    // Helper to extract the logged-in user from the security context
    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        String identifier = principal.getName();
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * GET /api/achievements/tracking/mine
     * Fetches all tracked achievements for the logged-in user.
     */
    @GetMapping("/mine")
    public ResponseEntity<List<UserAchievementProgress>> getMyAchievements(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(trackingService.getUserAchievements(user));
    }

    /**
     * GET /api/achievements/tracking/mine/unlocked
     * Use case 4: returns only completed/unlocked achievements for the logged-in user.
     */
    @GetMapping("/mine/unlocked")
    public ResponseEntity<List<UserAchievementProgress>> getMyUnlockedAchievements(Principal principal) {
        User user = getAuthenticatedUser(principal);
        return ResponseEntity.ok(trackingService.getUnlockedAchievements(user));
    }

    /**
     * GET /api/achievements/tracking/user/{userId}
     * Use case 5: returns another student's publicly-visible unlocked achievements.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAchievementProgress>> getPublicAchievements(@PathVariable Long userId) {
        return ResponseEntity.ok(trackingService.getPublicAchievements(userId));
    }

    /**
     * PUT /api/achievements/tracking/{progressId}/visibility
     * Use case 6: set whether an unlocked achievement is shown on the user's public profile.
     * Body: { "showOnProfile": true/false }
     */
    @PutMapping("/{progressId}/visibility")
    public ResponseEntity<Void> setVisibility(@PathVariable UUID progressId,
                                              @RequestBody Map<String, Boolean> body,
                                              Principal principal) {
        User user = getAuthenticatedUser(principal);
        boolean show = Boolean.TRUE.equals(body.get("showOnProfile"));
        trackingService.setShowOnProfile(user, progressId, show);
        return ResponseEntity.noContent().build();
    }
}