package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementService;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/achievements")
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementTrackingService achievementTrackingService;
    private final UserRepository userRepository;

    private User resolveUser(Authentication authentication) {
        String identifier = authentication.getName();
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    // ===== MY PROGRESS =====
    // GET /achievements/progress
    @GetMapping("/progress")
    public String myProgress(Model model, Authentication authentication) {
        User user = resolveUser(authentication);

        java.util.Map<java.util.UUID, id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress> progressMap =
                achievementTrackingService.getUserAchievements(user).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                p -> p.getAchievement().getId(), p -> p));

        java.util.List<id.ac.ui.cs.advprog.yomu.achievement.model.Achievement> achievements =
                new java.util.ArrayList<>(achievementService.findAll());
        achievements.sort((a, b) -> {
            boolean aUnlocked = progressMap.containsKey(a.getId()) && progressMap.get(a.getId()).isUnlocked();
            boolean bUnlocked = progressMap.containsKey(b.getId()) && progressMap.get(b.getId()).isUnlocked();
            return Boolean.compare(aUnlocked, bUnlocked);
        });

        model.addAttribute("achievements", achievements);
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("currentUri", "/achievements/progress");
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "achievement/myProgress";
    }

    // ===== LIST ALL =====
    // GET /achievements
    @GetMapping
    public String listAchievements(Model model, Authentication authentication) {
        model.addAttribute("achievements", achievementService.findAll());
        model.addAttribute("currentUri", "/achievements");
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "achievement/achievementList";
    }

    // ===== CREATE FORM =====
    // GET /achievements/create
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("achievement", new Achievement());
        model.addAttribute("types", AchievementType.values());
        return "achievement/achievementCreate";
    }

    // ===== CREATE SUBMIT =====
    // POST /achievements/create
    @PostMapping("/create")
    public String createAchievement(@ModelAttribute Achievement achievement) {
        achievementService.create(achievement);
        return "redirect:/achievements/progress";  // redirect to list page after saving
    }

    // ===== EDIT FORM =====
    // GET /achievements/edit/{id}
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        model.addAttribute("achievement", achievementService.findById(id));
        model.addAttribute("types", AchievementType.values());
        return "achievement/achievementEdit";
    }

    // ===== EDIT SUBMIT =====
    // POST /achievements/edit/{id}
    @PostMapping("/edit/{id}")
    public String updateAchievement(@PathVariable UUID id,
                                    @ModelAttribute Achievement achievement) {
        achievementService.update(id, achievement);
        return "redirect:/achievements/progress";
    }

    // ===== DELETE =====
    // POST /achievements/delete/{id}
    @PostMapping("/delete/{id}")
    public String deleteAchievement(@PathVariable UUID id) {
        achievementService.delete(id);
        return "redirect:/achievements/progress";
    }
}