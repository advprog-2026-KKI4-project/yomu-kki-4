package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import id.ac.ui.cs.advprog.yomu.achievement.service.DailyMissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {

    private final DailyMissionService dailyMissionService;

    // ===== LIST ALL =====
    @GetMapping
    public String listMissions(Model model) {
        model.addAttribute("missions", dailyMissionService.findAll());
        return "missionList";
    }

    // ===== CREATE FORM =====
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("mission", new DailyMission());
        model.addAttribute("types", MissionType.values());
        return "missionCreate";
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
        return "missionEdit";
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