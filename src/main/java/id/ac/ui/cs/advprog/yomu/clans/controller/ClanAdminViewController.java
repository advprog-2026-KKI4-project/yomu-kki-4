package id.ac.ui.cs.advprog.yomu.clans.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/clans")
@PreAuthorize("hasRole('ADMIN')")
public class ClanAdminViewController {

    @GetMapping("/league-management")
    public String showLeagueManagement() {
        return "admin/league-management";
    }
}