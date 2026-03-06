package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clans")
public class ClanViewController {

    @Autowired
    private ClanService clanService;

    @GetMapping("/list")
    public String showClanListPage(Model model) {
        model.addAttribute("clans", clanService.findAllClans());
        return "clans-list";
    }
}