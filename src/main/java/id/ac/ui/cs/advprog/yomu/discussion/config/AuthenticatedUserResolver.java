package id.ac.ui.cs.advprog.yomu.discussion.config;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticatedUserResolver {

    private final UserRepository userRepository;

    public User requireUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthorized");
        }
        String identity = authentication.getName();
        if (identity == null || identity.isBlank() || "anonymousUser".equals(identity)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if (identity.contains("@")) {
            return userRepository.findByEmail(identity)
                    .orElseThrow(() -> new AccessDeniedException("Unauthorized"));
        }
        return userRepository.findByPhone(identity)
                .orElseThrow(() -> new AccessDeniedException("Unauthorized"));
    }

    public Long requireUserId(Authentication authentication) {
        return requireUser(authentication).getId();
    }
}