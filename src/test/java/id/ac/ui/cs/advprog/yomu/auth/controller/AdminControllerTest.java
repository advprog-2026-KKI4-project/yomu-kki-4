package id.ac.ui.cs.advprog.yomu.auth.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminController adminController;

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user1 = User.builder().id(1L).email("user1@test.com").role("STUDENT").build();
        User user2 = User.builder().id(2L).email("user2@test.com").role("ADMIN").build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        ResponseEntity<List<User>> result = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(2, result.getBody().size());
    }

    @Test
    void toggleUserRole_shouldPromoteStudentToAdmin() {
        User student = User.builder().id(1L).email("user@test.com").role("STUDENT").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        ResponseEntity<Map<String, String>> result = adminController.toggleUserRole(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("User role updated successfully", result.getBody().get("message"));
        assertEquals("ADMIN", result.getBody().get("newRole"));
        verify(userRepository).save(student);
    }

    @Test
    void toggleUserRole_shouldDemoteAdminToStudent() {
        User admin = User.builder().id(2L).email("admin@test.com").role("ADMIN").build();
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        ResponseEntity<Map<String, String>> result = adminController.toggleUserRole(2L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("STUDENT", result.getBody().get("newRole"));
        verify(userRepository).save(admin);
    }

    @Test
    void toggleUserRole_shouldReturn404_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Map<String, String>> result = adminController.toggleUserRole(99L);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        verify(userRepository, never()).save(any());
    }
}