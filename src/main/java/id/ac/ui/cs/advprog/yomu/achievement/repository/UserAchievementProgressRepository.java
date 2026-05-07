package id.ac.ui.cs.advprog.yomu.achievement.repository;

import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementProgressRepository extends JpaRepository<UserAchievementProgress, UUID> {

    // This allows us to quickly find a user's progress on a specific achievement
    Optional<UserAchievementProgress> findByUserAndAchievementId(User user, UUID achievementId);
}