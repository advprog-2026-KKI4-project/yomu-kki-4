package id.ac.ui.cs.advprog.yomu.auth.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminSeeder adminSeeder;

    @Test
    void run_shouldCreateAdmin_whenAdminDoesNotExist() {
        when(userRepository.existsByEmail("admin@yomu.id")).thenReturn(false);
        when(passwordEncoder.encode("adminpassword")).thenReturn("encoded-admin-password");

        adminSeeder.run();

        verify(userRepository).save(argThat(user ->
                "admin@yomu.id".equals(user.getEmail()) &&
                "superadmin".equals(user.getUsername()) &&
                "encoded-admin-password".equals(user.getPassword()) &&
                "ADMIN".equals(user.getRole()) &&
                "Super".equals(user.getFirstName()) &&
                "Admin".equals(user.getLastName())
        ));
    }

    @Test
    void run_shouldNotCreateAdmin_whenAdminAlreadyExists() {
        when(userRepository.existsByEmail("admin@yomu.id")).thenReturn(true);

        adminSeeder.run();

        verify(userRepository, never()).save(any());
    }
}