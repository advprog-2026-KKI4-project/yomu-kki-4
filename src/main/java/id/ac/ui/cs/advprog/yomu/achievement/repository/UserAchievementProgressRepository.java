package id.ac.ui.cs.advprog.yomu.achievement.repository;

import id.ac.ui.cs.advprog.yomu.achievement.model.UserAchievementProgress;
import id.ac.ui.cs.advprog.yomu.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAchievementProgressRepository extends JpaRepository<UserAchievementProgress, UUID> {

    Optional<UserAchievementProgress> findByUserAndAchievementId(User user, UUID achievementId);

    List<UserAchievementProgress> findByUser(User user);

    // Use case 4: completed achievements only
    List<UserAchievementProgress> findByUserAndUnlockedTrue(User user);

    // Use case 5: another student's achievements visible on their profile
    List<UserAchievementProgress> findByUser_IdAndShowOnProfileTrueAndUnlockedTrue(Long userId);

    // Use case 6: find a specific progress record owned by a user
    Optional<UserAchievementProgress> findByIdAndUser(UUID id, User user);
}