package id.ac.ui.cs.advprog.yomu.achievement.repository;

import id.ac.ui.cs.advprog.yomu.achievement.model.UserMissionProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMissionProgressRepository extends JpaRepository<UserMissionProgress, UUID> {

    // Get all mission progress for a user on a specific day (for their dashboard)
    List<UserMissionProgress> findByUserAndDate(User user, LocalDate date);

    // Find a specific mission's progress to update it
    Optional<UserMissionProgress> findByUserAndMissionIdAndDate(User user, UUID missionId, LocalDate date);
}