package id.ac.ui.cs.advprog.yomu.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Email
    private String email;

    @Column(unique = true)
    private String googleId;

    @Column(unique = true)
    private String phone;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private String role = "STUDENT";

    private String firstName;

    private String lastName;

    private String avatarUrl;

    @Column(length = 1000)
    private String bio;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
