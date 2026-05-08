package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/leaderboard")
public class LeaderboardViewController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public String viewLeaderboard(Model model) {
        List<Clan> globalClans = leaderboardService.getGlobalLeaderboard();

        model.addAttribute("clans", globalClans);
        return "clans/clanLeaderboard";
    }
}
