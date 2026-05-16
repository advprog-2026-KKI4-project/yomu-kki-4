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
     * Fetches all tracked achievements for the logged-in user to display on their profile.
     */
    @GetMapping("/mine")
    public ResponseEntity<List<UserAchievementProgress>> getMyAchievements(Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<UserAchievementProgress> progress = trackingService.getUserAchievements(user);
        return ResponseEntity.ok(progress);
    }
}