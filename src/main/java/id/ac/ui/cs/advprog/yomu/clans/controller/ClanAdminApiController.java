package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/clans")
public class ClanAdminApiController {

    @Autowired
    private LeaderboardService leaderboardService;

    @PostMapping("/end-season")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> endSeason() {
        leaderboardService.endCurrentSeason();
        return ResponseEntity.ok("Season ended successfully. Scores reset and divisions updated.");
    }
}