package id.ac.ui.cs.advprog.yomu.auth.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@yomu.id")) {
            User admin = User.builder()
                    .email("admin@yomu.id")
                    .username("superadmin")
                    .password(passwordEncoder.encode("adminpassword"))
                    .role("ADMIN")
                    .firstName("Super")
                    .lastName("Admin")
                    .build();
            userRepository.save(admin);
        }
    }
}
