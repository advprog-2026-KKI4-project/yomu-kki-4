package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DiscussionViewController {

    private final ReadingMaterialService readingMaterialService;

    @GetMapping("/discussion/{materialId}")
    public String discussionForMaterial(@PathVariable String materialId, Model model) {
        ReadingMaterial material = readingMaterialService.getById(materialId);
        model.addAttribute("materialId", materialId);
        model.addAttribute("materialTitle",
                material != null ? material.getTitle() : "Unknown material");
        model.addAttribute("materialCategory",
                material != null ? material.getCategory() : "");
        model.addAttribute("materialExists", material != null);
        model.addAttribute("currentUri", "/discussion");
        return "discussion/discussion";
    }

    @GetMapping("/discussion")
    public String discussionIndex() {
        return "redirect:/reading";
    }

    @GetMapping("/admin/discussions")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminModeration() {
        return "discussion/adminModeration";
    }
}