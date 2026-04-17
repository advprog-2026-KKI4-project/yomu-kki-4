package id.ac.ui.cs.advprog.yomu.clans.repository;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.Division;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClanRepository extends JpaRepository<Clan, UUID> {
    List<Clan> findByName(String name);
    Optional<Clan> findByLeaderId(String leaderId);
    List<Clan> findAllByOrderByTotalScoreDesc();
    List<Clan> findAllByDivisionOrderByTotalScoreDesc(Division division);
}