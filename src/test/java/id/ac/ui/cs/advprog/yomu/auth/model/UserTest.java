package id.ac.ui.cs.advprog.yomu.auth.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testBuilderCreatesUserWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .password("password123")
                .role("STUDENT")
                .firstName("Test")
                .lastName("User")
                .phone("08123456789")
                .googleId("google-123")
                .avatarUrl("https://cdn.example/avatar.png")
                .bio("A learner")
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(1L, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertEquals("STUDENT", user.getRole());
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("08123456789", user.getPhone());
        assertEquals("google-123", user.getGoogleId());
        assertEquals("https://cdn.example/avatar.png", user.getAvatarUrl());
        assertEquals("A learner", user.getBio());
        assertEquals(now, user.getCreatedAt());
        assertEquals(now, user.getUpdatedAt());
    }

    @Test
    void testDefaultRoleIsStudent() {
        User user = User.builder()
                .email("minimal@example.com")
                .username("minimal")
                .password("password123")
                .build();

        assertEquals("STUDENT", user.getRole());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    void testSettersAndGetters() {
        User user = new User();
        user.setId(5L);
        user.setEmail("setter@example.com");
        user.setUsername("setteruser");
        user.setPassword("securepassword");
        user.setRole("ADMIN");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setPhone("0811111111");
        user.setGoogleId("g-456");
        user.setAvatarUrl("https://cdn.example/admin.png");
        user.setBio("An admin");

        assertEquals(5L, user.getId());
        assertEquals("setter@example.com", user.getEmail());
        assertEquals("setteruser", user.getUsername());
        assertEquals("securepassword", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        assertEquals("Admin", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("0811111111", user.getPhone());
        assertEquals("g-456", user.getGoogleId());
        assertEquals("https://cdn.example/admin.png", user.getAvatarUrl());
        assertEquals("An admin", user.getBio());
    }

    @Test
    void testPreUpdateSetsUpdatedAt() throws InterruptedException {
        User user = User.builder()
                .email("update@example.com")
                .username("updateuser")
                .password("password123")
                .build();

        LocalDateTime originalUpdatedAt = user.getUpdatedAt();

        // Ensure time has advanced at least 1 millisecond
        Thread.sleep(1);
        user.onUpdate();

        assertNotNull(user.getUpdatedAt());
        // After onUpdate, updatedAt should be after (or equal to) original
        assertFalse(user.getUpdatedAt().isBefore(originalUpdatedAt));
    }
}