package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DiscussionAdminController {

    private final ReadingMaterialService readingMaterialService;

    @GetMapping("/discussions")
    public String discussionIndex(Model model) {
        List<ReadingMaterial> materials = readingMaterialService.getAll();
        model.addAttribute("materials", materials);
        return "discussion/discussionAdmin";
    }
}