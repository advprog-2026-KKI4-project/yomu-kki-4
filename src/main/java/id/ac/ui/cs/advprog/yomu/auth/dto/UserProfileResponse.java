package id.ac.ui.cs.advprog.yomu.auth.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class UserProfileResponse {

    Long id;
    String username;
    String email;
    String phone;
    String role;
    String firstName;
    String lastName;
    String avatarUrl;
    String bio;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
