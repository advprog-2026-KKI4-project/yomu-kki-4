package id.ac.ui.cs.advprog.yomu.clans.repository;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClanRepository extends JpaRepository<Clan, Long> {
    List<Clan> findByName(String name);
    Optional<Clan> findByLeaderId(String leaderId);
}