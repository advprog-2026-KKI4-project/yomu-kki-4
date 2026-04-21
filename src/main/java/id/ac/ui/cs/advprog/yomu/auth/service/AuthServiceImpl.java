package id.ac.ui.cs.advprog.yomu.auth.service;

import id.ac.ui.cs.advprog.yomu.auth.config.JwtUtil;
import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (hasText(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        if (hasText(request.getPhone()) && userRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Phone is already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("STUDENT")
                .build();

        user = userRepository.save(user);
        return buildAuthResponse("Registration successful", user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = findUserByIdentifier(request.getEmailOrPhone());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return buildAuthResponse("Login successful", user);
    }

    @Override
    @Transactional
    public AuthResponse loginWithGoogle(OAuth2User oauth2User) {
        String email = oauth2User.getAttribute("email");
        if (!hasText(email)) {
            throw new RuntimeException("Google account email is required");
        }

        String googleId = oauth2User.getAttribute("sub");
        User user = resolveGoogleUser(googleId, email);
        populateGoogleProfile(user, oauth2User);

        if (!hasText(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }

        if (!hasText(user.getRole())) {
            user.setRole("STUDENT");
        }

        user = userRepository.save(user);
        return buildAuthResponse("Google login successful", user);
    }

    @Override
    public UserProfileResponse getProfile(String identity) {
        return toProfileResponse(findUserByIdentifier(identity));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String identity, UpdateProfileRequest request) {
        User user = findUserByIdentifier(identity);

        if (hasText(request.getUsername())) {
            user.setUsername(request.getUsername().trim());
        }

        if (request.getFirstName() != null) {
            user.setFirstName(normalizeNullable(request.getFirstName()));
        }

        if (request.getLastName() != null) {
            user.setLastName(normalizeNullable(request.getLastName()));
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(normalizeNullable(request.getAvatarUrl()));
        }

        if (request.getBio() != null) {
            user.setBio(normalizeNullable(request.getBio()));
        }

        return toProfileResponse(userRepository.save(user));
    }

    private AuthResponse buildAuthResponse(String message, User user) {
        return AuthResponse.builder()
                .userId(user.getId())
                .message(message)
                .token(jwtUtil.generateToken(user))
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    private User resolveGoogleUser(String googleId, String email) {
        if (hasText(googleId)) {
            User byGoogleId = userRepository.findByGoogleId(googleId).orElse(null);
            if (byGoogleId != null) {
                return byGoogleId;
            }
        }

        return userRepository.findByEmail(email)
                .orElseGet(() -> User.builder()
                        .email(email)
                        .username(defaultUsernameFromEmail(email))
                        .role("STUDENT")
                        .build());
    }

    private void populateGoogleProfile(User user, OAuth2User oauth2User) {
        String googleId = oauth2User.getAttribute("sub");
        if (hasText(googleId)) {
            user.setGoogleId(googleId);
        }

        String email = oauth2User.getAttribute("email");
        if (!hasText(user.getEmail()) && hasText(email)) {
            user.setEmail(email);
        }

        if (!hasText(user.getUsername())) {
            user.setUsername(defaultUsernameFromEmail(email));
        }

        String givenName = oauth2User.getAttribute("given_name");
        String familyName = oauth2User.getAttribute("family_name");
        String fullName = oauth2User.getAttribute("name");
        String picture = oauth2User.getAttribute("picture");

        if (!hasText(user.getFirstName())) {
            if (hasText(givenName)) {
                user.setFirstName(givenName);
            } else if (hasText(fullName)) {
                user.setFirstName(fullName);
            }
        }

        if (!hasText(user.getLastName()) && hasText(familyName)) {
            user.setLastName(familyName);
        }

        if (!hasText(user.getAvatarUrl()) && hasText(picture)) {
            user.setAvatarUrl(picture);
        }
    }

    private UserProfileResponse toProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private User findUserByIdentifier(String identifier) {
        if (!hasText(identifier)) {
            throw new RuntimeException("Unauthorized");
        }

        if (isEmail(identifier)) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        }

        return userRepository.findByPhone(identifier)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
    }

    private String defaultUsernameFromEmail(String email) {
        if (!hasText(email)) {
            return "google-user";
        }

        int atIndex = email.indexOf('@');
        if (atIndex <= 0) {
            return email;
        }

        return email.substring(0, atIndex);
    }

    private String normalizeNullable(String value) {
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private boolean isEmail(String emailOrPhone) {
        return emailOrPhone != null && emailOrPhone.contains("@");
    }
}
