package id.ac.ui.cs.advprog.yomu.achievement.controller;

import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.achievement.service.AchievementTrackingService;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final AchievementTrackingService trackingService;
    private final ClanMemberRepository clanMemberRepository;

    private User resolveUser(Authentication authentication) {
        String identifier = authentication.getName();
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByPhone(identifier)
                        .orElseThrow(() -> new RuntimeException("User not found")));
    }

    @GetMapping
    public String myProfile(Model model, Authentication authentication) {
        User me = resolveUser(authentication);
        List<UserAchievementProgress> unlocked = trackingService.getUnlockedAchievements(me);
        ClanMember membership = clanMemberRepository.findByStudentId(me.getId()).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst().orElse(null);

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("profileUser", me);
        model.addAttribute("displayName", buildDisplayName(me));
        model.addAttribute("unlockedAchievements", unlocked);
        model.addAttribute("clanMembership", membership);
        model.addAttribute("isOwnProfile", true);
        model.addAttribute("isAdmin", isAdmin);
        return "achievement/profile";
    }

    @GetMapping("/{userId}")
    public String viewProfile(@PathVariable Long userId, Model model, Authentication authentication) {
        User profileUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<UserAchievementProgress> publicAchievements = trackingService.getPublicAchievements(userId);
        ClanMember membership = clanMemberRepository.findByStudentId(userId).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst().orElse(null);

        User me = resolveUser(authentication);
        boolean isOwnProfile = me.getId().equals(userId);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isOwnProfile) {
            return "redirect:/profile";
        }

        model.addAttribute("profileUser", profileUser);
        model.addAttribute("displayName", buildDisplayName(profileUser));
        model.addAttribute("unlockedAchievements", publicAchievements);
        model.addAttribute("clanMembership", membership);
        model.addAttribute("isOwnProfile", false);
        model.addAttribute("isAdmin", isAdmin);
        return "achievement/profile";
    }

    @GetMapping("/students")
    public String studentDirectory(Model model, Authentication authentication) {
        User me = resolveUser(authentication);
        List<User> students = userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .filter(u -> !u.getId().equals(me.getId()))
                .toList();
        model.addAttribute("students", students);
        model.addAttribute("currentUser", me);
        return "achievement/students";
    }

    private String buildDisplayName(User user) {
        if (user.getFirstName() != null && !user.getFirstName().isBlank()) {
            String last = user.getLastName() != null ? " " + user.getLastName() : "";
            return user.getFirstName() + last;
        }
        return user.getUsername();
    }
}
