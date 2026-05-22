package id.ac.ui.cs.advprog.yomu.achievement.repository;

import id.ac.ui.cs.advprog.yomu.achievement.enums.MissionType;
import id.ac.ui.cs.advprog.yomu.achievement.model.DailyMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface DailyMissionRepository extends JpaRepository<DailyMission, UUID> {

    List<DailyMission> findByType(MissionType type);

    List<DailyMission> findByActiveTrue();

    // Returns missions where active=true AND current time is within the optional activeFrom/activeTo window
    @Query("SELECT m FROM DailyMission m WHERE m.active = true " +
           "AND (m.activeFrom IS NULL OR m.activeFrom <= :now) " +
           "AND (m.activeTo IS NULL OR m.activeTo >= :now)")
    List<DailyMission> findCurrentlyActive(@Param("now") LocalDateTime now);
}