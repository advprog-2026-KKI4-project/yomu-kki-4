package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Register a new user with email or phone.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Authenticate an existing user with email or phone + password.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/profile
     * Retrieve authenticated user's profile details.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UserProfileResponse response = authService.getProfile(getAuthenticatedIdentity(authentication));
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/auth/profile
     * Update authenticated user's editable profile details.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request
    ) {
        UserProfileResponse response = authService.updateProfile(getAuthenticatedIdentity(authentication), request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/oauth2/google
     * Return OAuth2 authorization URL for Google sign-in.
     */
    @GetMapping("/oauth2/google")
    public ResponseEntity<Map<String, String>> getGoogleAuthorizationUrl() {
        return ResponseEntity.ok(Map.of("authorizationUrl", "/oauth2/authorization/google"));
    }

    private String getAuthenticatedIdentity(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }
        return authentication.getName();
    }
}
