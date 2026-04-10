package id.ac.ui.cs.advprog.yomu.clans.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "clans")
@Getter @Setter
public class Clan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clan_id")
    private UUID id;

    @Column(name = "clan_name", unique = true, nullable = false)
    private String name;

    @Column(name = "clan_leader", nullable = false)
    private String leaderId;

    @Column(name= "clan_bio")
    private String bio;

    @Column(name = "clan_score")
    private Long totalScore = 0L;

    @Enumerated(EnumType.STRING)
    private Division division = Division.BRONZE;

    @Column(name = "multiplier")
    private Double activeMultiplier = 1.0;

    @Column(name = "previous_rank")
    private Integer previousRank;

    @OneToMany(mappedBy = "clanId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClanMember> members = new ArrayList<>();
}