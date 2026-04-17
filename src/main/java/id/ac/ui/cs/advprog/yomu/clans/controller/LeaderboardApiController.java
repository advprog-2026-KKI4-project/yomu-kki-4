package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardApiController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/global")
    public ResponseEntity<List<Clan>> getGlobalLeaderboard() {
        List<Clan> leaderboard = leaderboardService.getGlobalLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    @GetMapping("/division/{division}")
    public ResponseEntity<List<Clan>> getDivisionLeaderboard(@PathVariable String division) {
        List<Clan> leaderboard = leaderboardService.getDivisionLeaderboard(division);
        return ResponseEntity.ok(leaderboard);
    }
}