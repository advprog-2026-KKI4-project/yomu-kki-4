package id.ac.ui.cs.advprog.yomu.auth.service;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;

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
}
