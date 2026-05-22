package id.ac.ui.cs.advprog.yomu.clans.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clan_members")
@Getter @Setter
public class ClanMember {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "clanmember_id", updatable = false, nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    @JsonIgnoreProperties("members")
    private Clan clan;

    @Column(name = "member_id", nullable = false)
    private Long studentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", insertable = false, updatable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User student;

    private String role;

    private String status;

    @Column(name = "local_score")
    private int localScore = 0;

    @Column(name = "join_at")
    private LocalDateTime joinedAt = LocalDateTime.now();
}