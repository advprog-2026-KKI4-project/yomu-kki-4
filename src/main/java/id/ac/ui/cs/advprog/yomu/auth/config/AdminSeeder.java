package id.ac.ui.cs.advprog.yomu.auth.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("prod")
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminPassword = System.getenv("ADMIN_PASSWORD");
        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_PASSWORD environment variable is not set. " +
                    "Admin seeder requires a secure password to be provided via the ADMIN_PASSWORD environment variable."
            );
        }

        if (!userRepository.existsByEmail("admin@yomu.id")) {
            User admin = User.builder()
                    .email("admin@yomu.id")
                    .username("superadmin")
                    .password(passwordEncoder.encode(adminPassword))
                    .role("ADMIN")
                    .firstName("Super")
                    .lastName("Admin")
                    .build();
            userRepository.save(admin);
        }
    }
}
