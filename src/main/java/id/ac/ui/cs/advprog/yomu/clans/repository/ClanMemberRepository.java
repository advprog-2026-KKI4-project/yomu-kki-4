package id.ac.ui.cs.advprog.yomu.clans.repository;

import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    List<ClanMember> findByClanId(Clan clanId);
    List<ClanMember> findByStudentId(String studentId);
    Optional<ClanMember> findByClanIdAndStudentId(Clan clanId, String studentId);
}