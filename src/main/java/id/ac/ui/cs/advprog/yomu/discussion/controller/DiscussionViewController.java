package id.ac.ui.cs.advprog.yomu.discussion.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DiscussionViewController {

    @GetMapping("/discussion")
    public String discussionPage() {
        return "discussion";
    }
}