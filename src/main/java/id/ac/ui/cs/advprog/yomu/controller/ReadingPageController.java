package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.*;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import id.ac.ui.cs.advprog.yomu.repository.QuizAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/dashboard")
    public String dashboard(Model model, @RequestParam(required = false, defaultValue = "STUDENT") String role) {
        model.addAttribute("currentUri", "/dashboard");
        model.addAttribute("username", "Radithya");
        model.addAttribute("role", role);
        model.addAttribute("materials", service.getAll());
        return "reading/dashboard";
    }

    @GetMapping("/my-learning")
    public String myLearning(Model model, @RequestParam(required = false, defaultValue = "STUDENT") String role) {
        model.addAttribute("currentUri", "/my-learning");
        model.addAttribute("username", "Radithya");
        model.addAttribute("role", role);

        List<ReadingMaterial> completed = service.getAll().stream()
                .filter(m -> m.getProgress() == 100)
                .collect(Collectors.toList());

        model.addAttribute("materials", completed);
        return "reading/dashboard";
    }

    @GetMapping("/reading/{id}")
    public String readingPage(@PathVariable String id, Model model, @RequestParam(required = false, defaultValue = "STUDENT") String role) {
        model.addAttribute("material", service.getById(id));
        model.addAttribute("role", role);
        model.addAttribute("isReview", false);
        return "reading/reader";
    }

    @GetMapping("/quiz/{id}")
    public String quizPage(@PathVariable String id, Model model) {
        model.addAttribute("material", service.getById(id));
        model.addAttribute("userId", "user-123");
        return "reading/session";
    }

    @GetMapping("/quiz/result")
    public String resultPage(@RequestParam double score,
                             @RequestParam long duration,
                             @RequestParam double baseScore,
                             @RequestParam double bonus,
                             @RequestParam long remaining,
                             Model model) {
        model.addAttribute("score", score);
        model.addAttribute("duration", duration);
        model.addAttribute("baseScore", baseScore);
        model.addAttribute("bonus", bonus);
        model.addAttribute("remaining", remaining);
        return "reading/result";
    }

    @GetMapping("/review/{id}")
    public String reviewPage(@PathVariable String id, Model model, @RequestParam(required = false, defaultValue = "STUDENT") String role) {
        ReadingMaterial material = service.getById(id);
        QuizAttempt attempt = attemptRepo.findByUserIdAndMaterialId("user-123", id);

        model.addAttribute("material", material);
        model.addAttribute("attempt", attempt);
        model.addAttribute("score", attempt != null ? attempt.getScore() : 0);
        model.addAttribute("role", role);
        model.addAttribute("isReview", true);
        return "reading/reader";
    }

    // --- ADMIN ---

    @GetMapping("/admin/create")
    public String showCreateForm(Model model) {
        model.addAttribute("material", new ReadingMaterial());
        model.addAttribute("role", "ADMIN");
        return "admin/material-form";
    }

    @GetMapping("/admin/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        model.addAttribute("material", service.getById(id));
        model.addAttribute("role", "ADMIN");
        return "admin/material-form";
    }
}