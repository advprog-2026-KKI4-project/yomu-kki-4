package id.ac.ui.cs.advprog.yomu.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
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
    private String phone;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    @Size(min = 8)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private String role = "STUDENT";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
