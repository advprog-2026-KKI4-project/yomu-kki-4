package id.ac.ui.cs.advprog.yomu.auth.service;

import id.ac.ui.cs.advprog.yomu.auth.config.JwtUtil;
import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.LoginRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UpdateProfileRequest;
import id.ac.ui.cs.advprog.yomu.auth.dto.UserProfileResponse;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmailOrPhone("test@example.com");
        loginRequest.setPassword("password123");

        savedUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .role("STUDENT")
                .build();
    }

    @Test
    void testRegisterReturnsSuccessMessage() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(savedUser)).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterDuplicateEmailThrowsException() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.register(registerRequest));

        assertEquals("Email is already registered", exception.getMessage());
    }

    @Test
    void testLoginReturnsSuccessMessage() {
        when(userRepository.findByEmail(loginRequest.getEmailOrPhone())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), savedUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(savedUser)).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void testLoginWithInvalidPasswordThrowsException() {
        when(userRepository.findByEmail(loginRequest.getEmailOrPhone())).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches(loginRequest.getPassword(), savedUser.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.login(loginRequest));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testGetProfileReturnsUserProfile() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));

        UserProfileResponse response = authService.getProfile("test@example.com");

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
    }

    @Test
    void testUpdateProfileUpdatesEditableFields() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Test");
        request.setLastName("User");
        request.setBio("Reader and learner");
        request.setAvatarUrl("https://cdn.example/avatar.png");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = authService.updateProfile("test@example.com", request);

        assertNotNull(response);
        assertEquals("Test", response.getFirstName());
        assertEquals("User", response.getLastName());
        assertEquals("Reader and learner", response.getBio());
        assertEquals("https://cdn.example/avatar.png", response.getAvatarUrl());
    }

    @Test
    void testLoginWithGoogleCreatesUserAndReturnsToken() {
        OAuth2User oauth2User = org.mockito.Mockito.mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("google@example.com");
        when(oauth2User.getAttribute("sub")).thenReturn("google-sub");
        when(oauth2User.getAttribute("given_name")).thenReturn("Google");
        when(oauth2User.getAttribute("family_name")).thenReturn("User");
        when(oauth2User.getAttribute("name")).thenReturn("Google User");
        when(oauth2User.getAttribute("picture")).thenReturn("https://cdn.example/google.png");

        when(userRepository.findByGoogleId("google-sub")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("generated-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(99L);
            return user;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("oauth-jwt");

        AuthResponse response = authService.loginWithGoogle(oauth2User);

        assertNotNull(response);
        assertEquals("Google login successful", response.getMessage());
        assertEquals("oauth-jwt", response.getToken());
        assertEquals(99L, response.getUserId());
        assertEquals("google", response.getUsername());
        assertEquals("STUDENT", response.getRole());

        verify(userRepository).findByGoogleId(eq("google-sub"));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testLoginWithGoogleWithoutEmailThrowsException() {
        OAuth2User oauth2User = org.mockito.Mockito.mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.loginWithGoogle(oauth2User));

        assertEquals("Google account email is required", exception.getMessage());
    }

    @Test
    void testLoginWithPhoneIdentifier() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("08123456789");
        request.setPassword("password123");

        User phoneUser = User.builder()
                .id(2L)
                .username("phoneuser")
                .phone("08123456789")
                .password("encodedPassword")
                .role("STUDENT")
                .build();

        when(userRepository.findByPhone("08123456789")).thenReturn(Optional.of(phoneUser));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(phoneUser)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void testLoginThrowsWhenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmailOrPhone("ghost@example.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(request));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void testLoginWithGoogleExistingGoogleUser() {
        OAuth2User oauth2User = org.mockito.Mockito.mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("google@example.com");
        when(oauth2User.getAttribute("sub")).thenReturn("google-sub");
        when(oauth2User.getAttribute("given_name")).thenReturn(null);
        when(oauth2User.getAttribute("family_name")).thenReturn(null);
        when(oauth2User.getAttribute("name")).thenReturn(null);
        when(oauth2User.getAttribute("picture")).thenReturn(null);

        User existingUser = User.builder()
                .id(50L)
                .email("google@example.com")
                .googleId("google-sub")
                .username("existing")
                .password("has-password")
                .role("STUDENT")
                .firstName("Existing")
                .build();

        when(userRepository.findByGoogleId("google-sub")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("returning-jwt");

        AuthResponse response = authService.loginWithGoogle(oauth2User);

        assertNotNull(response);
        assertEquals("Google login successful", response.getMessage());
        assertEquals("returning-jwt", response.getToken());
        assertEquals(50L, response.getUserId());
    }

    @Test
    void testRegisterWithPhone() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("phoneuser");
        request.setPhone("08123456789");
        request.setPassword("password123");

        User phoneSavedUser = User.builder()
                .id(3L)
                .username("phoneuser")
                .phone("08123456789")
                .password("encodedPassword")
                .role("STUDENT")
                .build();

        when(userRepository.existsByPhone("08123456789")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(phoneSavedUser);
        when(jwtUtil.generateToken(phoneSavedUser)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("Registration successful", response.getMessage());
    }

    @Test
    void testRegisterDuplicatePhoneThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("phoneuser");
        request.setPhone("08123456789");
        request.setPassword("password123");

        when(userRepository.existsByPhone("08123456789")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(request));

        assertEquals("Phone is already registered", exception.getMessage());
    }

    @Test
    void testLoginWithGoogleNullRoleDefaultToStudent() {
        OAuth2User oauth2User = org.mockito.Mockito.mock(OAuth2User.class);
        when(oauth2User.getAttribute("email")).thenReturn("new@example.com");
        when(oauth2User.getAttribute("sub")).thenReturn(null);
        when(oauth2User.getAttribute("given_name")).thenReturn(null);
        when(oauth2User.getAttribute("family_name")).thenReturn(null);
        when(oauth2User.getAttribute("name")).thenReturn(null);
        when(oauth2User.getAttribute("picture")).thenReturn(null);

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("generated-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(60L);
            return user;
        });
        when(jwtUtil.generateToken(any(User.class))).thenReturn("new-jwt");

        AuthResponse response = authService.loginWithGoogle(oauth2User);

        assertNotNull(response);
        assertEquals("Google login successful", response.getMessage());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void testUpdateProfileNormalizesEmptyStringsToNull() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("  ");
        request.setBio("");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = authService.updateProfile("test@example.com", request);

        assertNotNull(response);
    }
}
