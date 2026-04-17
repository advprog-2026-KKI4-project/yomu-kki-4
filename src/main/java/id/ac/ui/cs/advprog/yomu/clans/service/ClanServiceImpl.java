package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.*;
import id.ac.ui.cs.advprog.yomu.clans.repository.*;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
public class ClanServiceImpl implements ClanService {

    @Autowired
    private ClanRepository clanRepository;

    @Autowired
    private ClanMemberRepository memberRepository;

    @Autowired
    private LeaderboardService leaderboardService;

    @Override
    @Transactional
    public Clan createClan(String name, String bio, String leaderId) {
        Clan clan = new Clan();
        clan.setName(name);
        clan.setBio(bio);
        clan.setLeaderId(leaderId);
        clan = clanRepository.save(clan);

        ClanMember leader = new ClanMember();
        leader.setClanId(clan);
        leader.setStudentId(leaderId);
        leader.setRole("LEADER");
        leader.setStatus("ACCEPTED");
        memberRepository.save(leader);
        return clan;
    }

    @Override
    public void requestToJoin(UUID clanId, String studentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        boolean alreadyRequested = memberRepository.findByStudentId(studentId).stream()
                .anyMatch(m -> m.getClanId().getId().equals(clanId));

        if (alreadyRequested) {
            throw new RuntimeException("You already have a pending request or are a member of this clan.");
        }

        ClanMember request = new ClanMember();
        request.setClanId(clan);
        request.setStudentId(studentId);
        request.setRole("MEMBER");
        request.setStatus("PENDING_REQUEST");
        memberRepository.save(request);
    }

    @Override
    public void approveMember(UUID clanId, String leaderId, String targetId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanIdAndStudentId(clan, targetId)
                .orElseThrow(() -> new RuntimeException("Membership request not found for this clan"));

        if (!"PENDING_REQUEST".equals(member.getStatus())) {
            throw new RuntimeException("This student did not request to join; they cannot be approved.");
        }
        member.setStatus("ACCEPTED");
        memberRepository.save(member);
        leaderboardService.updateClanScore(clan);
    }

    @Override
    @Transactional
    public void rejectRequest(UUID clanId, String leaderId, String targetStudentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        if (!clan.getLeaderId().equals(leaderId)) throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanIdAndStudentId(clan, targetStudentId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PENDING_REQUEST".equals(member.getStatus())) {
            throw new RuntimeException("This is not a pending join request");
        }

        memberRepository.delete(member);
    }

    @Override
    public void inviteStudent(UUID clanId, String leaderId, String targetStudentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Only leaders can invite");

        ClanMember invite = new ClanMember();
        invite.setClanId(clan);
        invite.setStudentId(targetStudentId);
        invite.setRole("MEMBER");
        invite.setStatus("PENDING_INVITE");
        memberRepository.save(invite);
    }

    @Override
    public void acceptInvitation(UUID clanId, String studentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        ClanMember member = memberRepository.findByClanIdAndStudentId(clan, studentId)
                .orElseThrow(() -> new RuntimeException("Invitation not found for this clan"));

        if (!"PENDING_INVITE".equals(member.getStatus()))
            throw new RuntimeException("No valid invitation found");

        member.setStatus("ACCEPTED");
        memberRepository.save(member);
        leaderboardService.updateClanScore(clan);
    }

    @Override
    @Transactional
    public void declineInvitation(UUID clanId, String studentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();

        ClanMember member = memberRepository.findByClanIdAndStudentId(clan, studentId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (!"PENDING_INVITE".equals(member.getStatus())) {
            throw new RuntimeException("You do not have a pending invitation to this clan");
        }

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public void kickMember(UUID clanId, String leaderId, String targetStudentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();

        if (!clan.getLeaderId().equals(leaderId)) throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanIdAndStudentId(clan, targetStudentId)
                .orElseThrow(() -> new RuntimeException("Member not found in this clan"));

        if ("LEADER".equals(member.getRole())) {
            throw new RuntimeException("Leaders cannot be kicked. They must delete the clan.");
        }

        memberRepository.delete(member);
        leaderboardService.updateClanScore(clan);
    }

    @Override
    @Transactional
    public void leaveClan(String studentId) {
        List<ClanMember> memberships = memberRepository.findByStudentId(studentId);
        ClanMember activeMember = memberships.stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You are not currently in an active clan"));
        if ("LEADER".equals(activeMember.getRole()))
            throw new RuntimeException("Leaders cannot leave. Delete the clan instead.");

        Clan clan = activeMember.getClanId();
        clan.getMembers().remove(activeMember);
        activeMember.setClanId(null);
        memberRepository.delete(activeMember);
        leaderboardService.updateClanScore(clan);
        clanRepository.save(clan);
    }

    @Override
    @Transactional
    public void deleteClan(UUID clanId, String leaderId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");
        clanRepository.delete(clan);
    }

    @Override
    public List<Clan> findAllClans() {
        return clanRepository.findAll();
    }

    // Temporary Function
    @Override
    @Transactional
    public void updateMemberScoreMock(String studentId, int newScore) {
        ClanMember member = memberRepository.findByStudentId(studentId)
                .stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Active member not found"));

        member.setLocalScore(newScore);
        memberRepository.save(member);

        leaderboardService.updateClanScore(member.getClanId());
    }
}