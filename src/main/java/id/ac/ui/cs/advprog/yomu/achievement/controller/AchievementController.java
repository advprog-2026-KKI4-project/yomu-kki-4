package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.enums.AchievementType;
import id.ac.ui.cs.advprog.yomu.achievement.model.Achievement;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/achievements")
public class AchievementController {

    private final AchievementService achievementService;

    public AchievementController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    // ===== LIST ALL =====
    // GET /achievements
    @GetMapping
    public String listAchievements(Model model) {
        model.addAttribute("achievements", achievementService.findAll());
        return "achievement/achievementList";  // renders templates/achievement/achievementList.html
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
        return "redirect:/achievements";  // redirect to list page after saving
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
        return "redirect:/achievements";
    }

    // ===== DELETE =====
    // POST /achievements/delete/{id}
    @PostMapping("/delete/{id}")
    public String deleteAchievement(@PathVariable UUID id) {
        achievementService.delete(id);
        return "redirect:/achievements";
    }
}