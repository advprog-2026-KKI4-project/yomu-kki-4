package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.service.DailyMissionService;
import id.ac.ui.cs.advprog.yomu.achievement.service.MissionTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;
    private final MissionTrackingService missionTrackingService;
    private final UserRepository userRepository;

    private User resolveUser(Authentication authentication) {
        String identifier = authentication.getName();
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    // ===== LIST ALL =====
    @GetMapping
    public String listMissions(Model model, Authentication authentication) {
        User user = resolveUser(authentication);

        java.util.Map<java.util.UUID, id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress> progressMap =
                missionTrackingService.getUserProgressToday(user).stream()
                        .collect(java.util.stream.Collectors.toMap(
                                p -> p.getMission().getId(), p -> p));

        model.addAttribute("missions", dailyMissionService.findAll());
        model.addAttribute("activeMissions", dailyMissionService.findActiveMissions());
        model.addAttribute("progressMap", progressMap);
        model.addAttribute("currentUri", "/daily-missions");
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);
        return "mission/missionList";
    }

    // ===== CREATE FORM =====
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("mission", new DailyMission());
        model.addAttribute("types", MissionType.values());
        return "mission/missionCreate";
    }

    // ===== CREATE SUBMIT =====
    @PostMapping("/create")
    public String createMission(@ModelAttribute DailyMission mission) {
        dailyMissionService.create(mission);
        return "redirect:/daily-missions";
    }

    // ===== EDIT FORM =====
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        model.addAttribute("mission", dailyMissionService.findById(id));
        model.addAttribute("types", MissionType.values());
        return "mission/missionEdit";
    }

    // ===== EDIT SUBMIT =====
    @PostMapping("/edit/{id}")
    public String updateMission(@PathVariable UUID id,
                                @ModelAttribute DailyMission mission) {
        dailyMissionService.update(id, mission);
        return "redirect:/daily-missions";
    }

    // ===== DELETE =====
    @PostMapping("/delete/{id}")
    public String deleteMission(@PathVariable UUID id) {
        dailyMissionService.delete(id);
        return "redirect:/daily-missions";
    }
}