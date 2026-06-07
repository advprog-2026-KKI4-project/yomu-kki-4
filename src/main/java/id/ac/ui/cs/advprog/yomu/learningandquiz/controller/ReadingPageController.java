package id.ac.ui.cs.advprog.yomu.learningandquiz.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.QuizAttempt;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.*;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import id.ac.ui.cs.advprog.yomu.learningandquiz.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReadingPageController {

    private final ReadingMaterialService service;
    private final QuizAttemptRepository attemptRepo;

    @Autowired
    public ReadingPageController(ReadingMaterialService service, QuizAttemptRepository attemptRepo) {
        this.service = service;
        this.attemptRepo = attemptRepo;
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return "STUDENT";
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .findFirst().orElse("STUDENT");
    }

    @GetMapping("/reading")
    public String dashboard(Model model) {
        model.addAttribute("currentUri", "/reading");
        model.addAttribute("username", getCurrentUserId());
        model.addAttribute("role", getCurrentUserRole());
        model.addAttribute("materials", service.getAll());
        return "reading/dashboard";
    }

    @GetMapping("/my-learning")
    public String myLearning(Model model) {
        model.addAttribute("currentUri", "/my-learning");
        model.addAttribute("username", getCurrentUserId());
        model.addAttribute("role", getCurrentUserRole());

        List<ReadingMaterial> inProgress = service.getAll().stream()
                .filter(m -> m.getProgress() > 0)
                .collect(Collectors.toList());

        model.addAttribute("materials", inProgress);
        return "reading/dashboard";
    }

    @GetMapping("/reading/{id}")
    public String readingPage(@PathVariable String id, Model model) {
        ReadingMaterial material = service.getById(id);

        if ("STUDENT".equals(getCurrentUserRole()) && material != null && material.getProgress() == 0) {
            service.completeReading(getCurrentUserId(), id);
        }

        model.addAttribute("material", material);
        model.addAttribute("role", getCurrentUserRole());
        model.addAttribute("isReview", false);
        model.addAttribute("currentUri", "/reading");
        return "reading/reader";
    }

    @GetMapping("/quiz/{id}")
    public String quizPage(@PathVariable String id, Model model) {
        model.addAttribute("material", service.getById(id));
        model.addAttribute("role", getCurrentUserRole());
        return "quiz/session";
    }

    @GetMapping("/quiz/result")
    public String resultPage(@RequestParam double score,
                             @RequestParam long duration,
                             @RequestParam double baseScore,
                             @RequestParam double bonus,
                             @RequestParam long remaining,
                             @RequestParam(required = false) String materialId,
                             Model model) {
        model.addAttribute("score", score);
        model.addAttribute("duration", duration);
        model.addAttribute("baseScore", baseScore);
        model.addAttribute("bonus", bonus);
        model.addAttribute("remaining", remaining);
        model.addAttribute("materialId", materialId);
        model.addAttribute("role", getCurrentUserRole());
        model.addAttribute("currentUri", "/reading");
        return "quiz/result";
    }

    @GetMapping("/review/{id}")
    public String reviewPage(@PathVariable String id, Model model) {
        ReadingMaterial material = service.getById(id);
        String userId = getCurrentUserId();
        QuizAttempt attempt = attemptRepo.findByUserIdAndMaterialId(userId, id);

        model.addAttribute("material", material);
        model.addAttribute("attempt", attempt);
        model.addAttribute("score", attempt != null ? attempt.getScore() : 0);
        model.addAttribute("role", getCurrentUserRole());
        model.addAttribute("isReview", true);
        model.addAttribute("currentUri", "/my-learning");

        return "reading/reader";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/create")
    public String showCreateForm(Model model) {
        model.addAttribute("material", new ReadingMaterial());
        model.addAttribute("role", "ADMIN");
        model.addAttribute("currentUri", "/reading");
        return "admin/material-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        ReadingMaterial material = service.getById(id);

        if (material == null) {
            return "redirect:/reading?error=Material+not+found";
        }

        model.addAttribute("material", material);
        model.addAttribute("role", "ADMIN");
        model.addAttribute("currentUri", "/reading");
        return "admin/material-form";
    }
}