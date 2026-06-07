package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DashboardController dashboardController;

    @Test
    void dashboardPage_shouldResolveUserByEmail() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        when(authentication.getAuthorities()).thenAnswer(inv -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));

        User user = User.builder().id(1L).email("user@example.com").username("testuser").role("STUDENT").build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Model model = new ExtendedModelMap();
        String viewName = dashboardController.dashboardPage(authentication, model);

        assertEquals("dashboard", viewName);
        assertEquals("testuser", model.getAttribute("username"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }

    @Test
    void dashboardPage_shouldFallbackToPhoneWhenEmailNotFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("08123456789");
        when(authentication.getAuthorities()).thenAnswer(inv -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER")));

        when(userRepository.findByEmail("08123456789")).thenReturn(Optional.empty());
        User phoneUser = User.builder().id(2L).phone("08123456789").username("phoneuser").role("TEACHER").build();
        when(userRepository.findByPhone("08123456789")).thenReturn(Optional.of(phoneUser));

        Model model = new ExtendedModelMap();
        String viewName = dashboardController.dashboardPage(authentication, model);

        assertEquals("dashboard", viewName);
        assertEquals("phoneuser", model.getAttribute("username"));
        assertEquals("TEACHER", model.getAttribute("role"));
    }

    @Test
    void dashboardPage_shouldUseDefaultsWhenNotAuthenticated() {
        Model model = new ExtendedModelMap();
        String viewName = dashboardController.dashboardPage(null, model);

        assertEquals("dashboard", viewName);
        assertEquals("Learner", model.getAttribute("username"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }

    @Test
    void dashboardPage_shouldUseDefaultsWhenAuthenticatedButUserNotFound() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("ghost@example.com");
        when(authentication.getAuthorities()).thenAnswer(inv -> Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT")));

        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhone("ghost@example.com")).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String viewName = dashboardController.dashboardPage(authentication, model);

        assertEquals("dashboard", viewName);
        assertEquals("Learner", model.getAttribute("username"));
        assertEquals("STUDENT", model.getAttribute("role"));
    }
}