package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/leaderboard")
public class LeaderboardViewController {

    @Autowired
    private LeaderboardService leaderboardService;

    @Autowired
    private ClanService clanService;

    @Autowired
    private UserRepository userRepository;

    private Long getAuthId(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }

    @GetMapping
    public String viewLeaderboard(@RequestParam(defaultValue = "global") String tab,
            Principal principal,
            Model model) {

        Long studentId = getAuthId(principal);
        Optional<ClanMember> membership = clanService.getAcceptedMembership(studentId);

        String userDivision = "BRONZE";
        if (membership.isPresent()) {
            userDivision = membership.get().getClan().getDivision().name();
        }

        List<Clan> clansToDisplay;

        if ("division".equalsIgnoreCase(tab)) {
            clansToDisplay = leaderboardService.getDivisionLeaderboard(userDivision);
            model.addAttribute("currentDivision", userDivision);
        } else {
            clansToDisplay = leaderboardService.getGlobalLeaderboard();
        }

        model.addAttribute("clans", clansToDisplay);
        model.addAttribute("activeTab", tab);

        return "clans/clanLeaderboard";
    }
}
