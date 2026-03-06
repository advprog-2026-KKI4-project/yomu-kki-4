package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClanMemberRepository extends JpaRepository<ClanMember, Long> {
    List<ClanMember> findByClanId(Clan clan);
    List<ClanMember> findByStudentId(String studentId);
    Optional<ClanMember> findByClanAndStudentId(Clan clan, String studentId);
}