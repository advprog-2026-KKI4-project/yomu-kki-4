package id.ac.ui.cs.advprog.yomu.auth.service;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse register(RegisterRequest request) {
        // TODO: implement full registration logic (Milestone 1)
        // - Validate that at least one of email or phone is provided
        // - Check for duplicate email / phone
        // - Hash password
        // - Persist user
        // - Return JWT token
        return AuthResponse.builder()
                .message("Registration successful")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // TODO: implement full login logic (Milestone 1)
        // - Look up user by email or phone
        // - Verify hashed password
        // - Return JWT token
        return AuthResponse.builder()
                .message("Login successful")
                .build();
    }

    /** Determine whether the given credential looks like an email address. */
    private boolean isEmail(String emailOrPhone) {
        return emailOrPhone != null && emailOrPhone.contains("@");
    }
}
