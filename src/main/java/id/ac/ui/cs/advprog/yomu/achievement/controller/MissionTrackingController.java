package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/missions/tracking")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MissionTrackingController {

    private final MissionTrackingService trackingService;
    private final UserRepository userRepository;

    // A helper method to get the logged-in User from the JWT Principal
    private User getAuthenticatedUser(Principal principal) {
        if (principal == null) throw new RuntimeException("Unauthorized");
        String identifier = principal.getName(); // This is the email or phone from JWT

        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    /**
     * GET /api/missions/tracking/today
     * Fetches the user's daily mission checklist.
     */
    @GetMapping("/today")
    public ResponseEntity<List<UserMissionProgress>> getTodayProgress(Principal principal) {
        User user = getAuthenticatedUser(principal);
        List<UserMissionProgress> progress = trackingService.getUserProgressToday(user);
        return ResponseEntity.ok(progress);
    }

    /**
     * POST /api/missions/tracking/trigger/{type}
     * Used by the frontend (or internal systems) to trigger an action.
     * Example: /api/missions/tracking/trigger/READING
     */
    @PostMapping("/trigger/{type}")
    public ResponseEntity<String> triggerAction(Principal principal, @PathVariable String type) {
        User user = getAuthenticatedUser(principal);
        try {
            MissionType missionType = MissionType.valueOf(type.toUpperCase());
            trackingService.incrementProgress(user, missionType);
            return ResponseEntity.ok("Progress updated successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid mission type");
        }
    }
}