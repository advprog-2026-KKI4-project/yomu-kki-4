package id.ac.ui.cs.advprog.yomu.auth.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Must be a valid email address")
    private String email;

    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @AssertTrue(message = "Either email or phone must be provided")
    public boolean hasLoginIdentifier() {
        return hasText(email) || hasText(phone);
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
