package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication; // Added
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DiscussionViewController {

    private final ReadingMaterialService readingMaterialService;

    @GetMapping("/discussion/{materialId}")
    public String discussionForMaterial(@PathVariable String materialId, Authentication authentication, Model model) {
        ReadingMaterial material = readingMaterialService.getById(materialId);
        model.addAttribute("materialId", materialId);
        model.addAttribute("materialTitle",
                material != null ? material.getTitle() : "Unknown material");
        model.addAttribute("materialCategory",
                material != null ? material.getCategory() : "");
        model.addAttribute("materialExists", material != null);
        model.addAttribute("currentUri", "/discussion");

        // Pass authentication state to Thymeleaf safely
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName());
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUsername", isAuthenticated ? authentication.getName() : null);

        return "discussion/discussion";
    }

    @GetMapping("/discussion")
    public String discussionIndex() {
        return "redirect:/reading";

    }

    @GetMapping("/admin/discussions")
    public String adminDiscussions(org.springframework.ui.Model model, org.springframework.security.core.Authentication authentication) {
        String username = (authentication != null && authentication.isAuthenticated()) ? authentication.getName() : "Admin";
        model.addAttribute("username", username);
        model.addAttribute("role", "ADMIN");
        model.addAttribute("currentUri", "/discussions");
        return "discussion/discussionAdmin";
    }
}