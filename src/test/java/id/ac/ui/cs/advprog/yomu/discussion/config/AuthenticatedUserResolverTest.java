package id.ac.ui.cs.advprog.yomu.discussion.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserResolverTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticatedUserResolver resolver;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
    }


    @Test
    void requireUser_nullAuthentication_throwsAccessDenied() {
        assertThatThrownBy(() -> resolver.requireUser(null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Unauthorized");
    }


    @Test
    void requireUser_notAuthenticated_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(false);
        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void requireUser_anonymousUser_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");
        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    void requireUser_blankName_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("   ");
        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    void requireUser_nullName_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(null);
        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    void requireUser_emailIdentity_returnsUserFromEmail() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        User result = resolver.requireUser(authentication);

        assertThat(result).isSameAs(mockUser);
        verify(userRepository).findByEmail("user@example.com");
        verify(userRepository, never()).findByPhone(any());
    }

    @Test
    void requireUser_phoneIdentity_returnsUserFromPhone() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("081234567890");
        when(userRepository.findByPhone("081234567890")).thenReturn(Optional.of(mockUser));

        User result = resolver.requireUser(authentication);

        assertThat(result).isSameAs(mockUser);
        verify(userRepository).findByPhone("081234567890");
        verify(userRepository, never()).findByEmail(any());
    }


    @Test
    void requireUser_emailNotFound_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("ghost@example.com");
        when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void requireUser_phoneNotFound_throwsAccessDenied() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("081234567890");
        when(userRepository.findByPhone("081234567890")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> resolver.requireUser(authentication))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    void requireUserId_validAuthentication_returnsUserId() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));

        Long id = resolver.requireUserId(authentication);

        assertThat(id).isEqualTo(1L);
    }
}
