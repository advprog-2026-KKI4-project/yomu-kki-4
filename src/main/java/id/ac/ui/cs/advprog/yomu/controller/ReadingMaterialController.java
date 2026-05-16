package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.Question;
import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ReadingMaterialController {

    private final ReadingMaterialService service;

    @Autowired
    public ReadingMaterialController(ReadingMaterialService service) {
        this.service = service;
    }

    @PostMapping("/materials/{id}/submit")
    public String submitQuiz(@PathVariable String id,
                             @RequestParam long duration,
                             @RequestParam Map<String, String> allParams) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth != null ? auth.getName() : "anonymous";

        try {
            List<Integer> answers = allParams.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("answers["))
                    .sorted(Comparator.comparingInt(e ->
                        Integer.parseInt(e.getKey().replaceAll("\\D+", ""))))
                    .map(entry -> Integer.parseInt(entry.getValue()))
                    .collect(Collectors.toList());

            ReadingMaterial material = service.getById(id);
            List<Question> questions = material.getQuestions();

            int correctCount = 0;
            for (int i = 0; i < Math.min(questions.size(), answers.size()); i++) {
                if (questions.get(i).getCorrectOptionIndex() == answers.get(i)) {
                    correctCount++;
                }
            }

            double baseScore = ((double) correctCount / questions.size()) * 100;
            double finalScore = service.submitQuiz(userId, id, answers, duration);

            double timeLimit = material.getTimeLimit();
            double remaining = Math.max(0, timeLimit - duration);
            double bonus = (remaining / timeLimit) * 10.0;

            return String.format("redirect:/quiz/result?score=%.1f&duration=%d&baseScore=%.0f&bonus=%.1f&remaining=%d",
                    finalScore, duration, baseScore, bonus, (long)remaining);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "redirect:/reading?error=" + e.getMessage();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/materials/add")
    public String add(@ModelAttribute ReadingMaterial material) {
        service.add(material);
        return "redirect:/reading";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/materials/edit/{id}")
    public String update(@PathVariable String id, @ModelAttribute ReadingMaterial material) {
        ReadingMaterial existing = service.getById(id);
        if (existing != null) {
            existing.setTitle(material.getTitle());
            existing.setCategory(material.getCategory());
            existing.setContent(material.getContent());
            existing.setTimeLimit(material.getTimeLimit());
            if (material.getQuestions() != null && !material.getQuestions().isEmpty()) {
                existing.setQuestions(material.getQuestions());
            }
            service.add(existing);
        }
        return "redirect:/reading";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/materials/delete/{id}")
    public String delete(@PathVariable String id) {
        service.delete(id);
        return "redirect:/reading";
    }
}
