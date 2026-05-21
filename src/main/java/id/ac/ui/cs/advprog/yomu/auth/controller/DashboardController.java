package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboardPage(Authentication authentication, Model model) {
        String username = "Learner";
        String role = "STUDENT";

        if (authentication != null && authentication.isAuthenticated()) {
            String identifier = authentication.getName();
            User user = userRepository.findByEmail(identifier)
                    .orElseGet(() -> userRepository.findByPhone(identifier).orElse(null));
            if (user != null && user.getUsername() != null) {
                username = user.getUsername();
            }
            role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(a -> a.startsWith("ROLE_"))
                    .map(a -> a.substring(5))
                    .findFirst().orElse("STUDENT");
        }

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        return "dashboard";
    }
}