package id.ac.ui.cs.advprog.yomu.auth.service;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UserProfileResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface AuthService {

    /**
     * Register a new user with email or phone credentials.
     *
     * @param request registration payload
     * @return token and user info on success
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate an existing user by email or phone + password.
     *
     * @param request login payload
     * @return token and user info on success
     */
    AuthResponse login(LoginRequest request);

    /**
     * Handle Google OAuth login by creating/updating a local user profile and issuing a JWT.
     *
     * @param oauth2User Google account details resolved by Spring Security
     * @return token and user info on success
     */
    AuthResponse loginWithGoogle(OAuth2User oauth2User);

    /**
     * Retrieve the authenticated user's profile by the JWT subject identity.
     *
     * @param identity email or phone value from token subject
     * @return current profile fields
     */
    UserProfileResponse getProfile(String identity);

    /**
     * Update editable profile fields for the authenticated user.
     *
     * @param identity email or phone value from token subject
     * @param request profile update payload
     * @return updated profile fields
     */
    UserProfileResponse updateProfile(String identity, UpdateProfileRequest request);
}
