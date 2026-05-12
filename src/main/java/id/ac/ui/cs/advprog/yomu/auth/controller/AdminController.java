package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    @Getter
    public static class UserResponse {
        private final Long id;
        private final String email;
        private final String username;
        private final String firstName;
        private final String lastName;
        private final String role;

        public UserResponse(User user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.role = user.getRole();
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userRepository.findAll().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PostMapping("/toggle-role/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> toggleUserRole(@PathVariable Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = optionalUser.get();
        if ("ADMIN".equals(user.getRole())) {
            user.setRole("STUDENT");
        } else {
            user.setRole("ADMIN");
        }
        
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of(
            "message", "User role updated successfully",
            "newRole", user.getRole()
        ));
    }
}
