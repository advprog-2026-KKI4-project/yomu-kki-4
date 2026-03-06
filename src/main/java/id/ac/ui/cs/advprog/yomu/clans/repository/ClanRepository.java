package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.Clan;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClanRepository extends JpaRepository<Clan, Long> {
    // check if a clan name is already taken during creation
    List<Clan> findByName(String name);

    // Used for quick authority checks in the Service layer
    Optional<Clan> findByLeaderId(String leaderId);
}