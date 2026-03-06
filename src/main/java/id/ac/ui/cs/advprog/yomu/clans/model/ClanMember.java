package id.ac.ui.cs.advprog.yomu.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clan_members")
@Getter @Setter
public class ClanMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clanmember_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    private Clan clanId;

    @Column(name = "member_id", nullable = false)
    private String studentId;

    private String role;

    private String status;

    @Column(name = "join_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}