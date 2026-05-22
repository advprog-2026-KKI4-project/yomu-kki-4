package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.*;
import id.ac.ui.cs.advprog.yomu.clans.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
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
    public List<Clan> findAllClans() {
        return clanRepository.findAll();
    }

    @Override
    @Transactional
    public Clan createClan(String name, String bio, Long leaderId) {
        if (isUserInAnyClan(leaderId)) {
            throw new RuntimeException("You are already in a clan and cannot create a new one.");
        }

        Clan clan = new Clan();
        clan.setName(name);
        clan.setBio(bio);
        clan.setLeaderId(leaderId);
        clan = clanRepository.save(clan);

        ClanMember leader = new ClanMember();
        leader.setClan(clan);
        leader.setStudentId(leaderId);
        leader.setRole("LEADER");
        leader.setStatus("ACCEPTED");
        memberRepository.save(leader);

        clearOtherPendingRequestsAndInvites(leaderId, clan.getId());
        
        return clan;
    }

    @Override
    @Transactional
    public Clan updateClan(UUID clanId, Long leaderId, String newName, String newBio) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");

        clan.setName(newName);
        clan.setBio(newBio);

        return clanRepository.save(clan);
    }

    @Override
    @Transactional
    public void deleteClan(UUID clanId, Long leaderId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");
        clanRepository.delete(clan);
    }

    @Override
    @Transactional
    public void requestToJoin(UUID clanId, Long studentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        boolean alreadyRequested = memberRepository.findByStudentId(studentId).stream()
                .anyMatch(m -> m.getClan().getId().equals(clanId));

        if (alreadyRequested) {
            throw new RuntimeException("You already have a pending request or are a member of this clan.");
        }

        ClanMember request = new ClanMember();
        request.setClan(clan);
        request.setStudentId(studentId);
        request.setRole("MEMBER");
        request.setStatus("PENDING_REQUEST");
        memberRepository.save(request);
    }

    @Override
    @Transactional
    public void approveMember(UUID clanId, Long leaderId, Long targetId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanAndStudentId(clan, targetId)
                .orElseThrow(() -> new RuntimeException("Membership request not found for this clan"));

        if (!"PENDING_REQUEST".equals(member.getStatus())) {
            throw new RuntimeException("This student did not request to join, they cannot be approved.");
        }
        member.setStatus("ACCEPTED");
        memberRepository.save(member);
        leaderboardService.updateClanScore(clan);

        clearOtherPendingRequestsAndInvites(targetId, clanId);
    }

    @Override
    @Transactional
    public void rejectRequest(UUID clanId, Long leaderId, Long targetStudentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        if (!clan.getLeaderId().equals(leaderId)) throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanAndStudentId(clan, targetStudentId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PENDING_REQUEST".equals(member.getStatus())) {
            throw new RuntimeException("This is not a pending join request");
        }

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public void inviteStudent(UUID clanId, Long leaderId, Long targetStudentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Only leaders can invite");

        ClanMember invite = new ClanMember();
        invite.setClan(clan);
        invite.setStudentId(targetStudentId);
        invite.setRole("MEMBER");
        invite.setStatus("PENDING_INVITE");
        memberRepository.save(invite);
    }

    @Override
    @Transactional
    public void acceptInvitation(UUID clanId, Long studentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        ClanMember member = memberRepository.findByClanAndStudentId(clan, studentId)
                .orElseThrow(() -> new RuntimeException("Invitation not found for this clan"));

        if (!"PENDING_INVITE".equals(member.getStatus()))
            throw new RuntimeException("No valid invitation found");

        member.setStatus("ACCEPTED");
        memberRepository.save(member);
        leaderboardService.updateClanScore(clan);

        clearOtherPendingRequestsAndInvites(studentId, clanId);
    }

    @Override
    @Transactional
    public void declineInvitation(UUID clanId, Long studentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();

        ClanMember member = memberRepository.findByClanAndStudentId(clan, studentId)
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (!"PENDING_INVITE".equals(member.getStatus())) {
            throw new RuntimeException("You do not have a pending invitation to this clan");
        }

        memberRepository.delete(member);
    }

    @Override
    @Transactional
    public void kickMember(UUID clanId, Long leaderId, Long targetStudentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();

        if (!clan.getLeaderId().equals(leaderId)) throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanAndStudentId(clan, targetStudentId)
                .orElseThrow(() -> new RuntimeException("Member not found in this clan"));

        if ("LEADER".equals(member.getRole())) {
            throw new RuntimeException("Leaders cannot be kicked. They must delete the clan.");
        }

        memberRepository.delete(member);
        leaderboardService.updateClanScore(clan);
    }

    @Override
    @Transactional
    public void leaveClan(Long studentId) {
        List<ClanMember> memberships = memberRepository.findByStudentId(studentId);
        ClanMember activeMember = memberships.stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You are not currently in an active clan"));
        if ("LEADER".equals(activeMember.getRole()))
            throw new RuntimeException("Leaders cannot leave. Delete the clan instead.");

        Clan clan = activeMember.getClan();
        clan.getMembers().remove(activeMember);
        activeMember.setClan(null);
        memberRepository.delete(activeMember);
        leaderboardService.updateClanScore(clan);
        clanRepository.save(clan);
    }

    @Override
    public boolean isUserInAnyClan(Long studentId) {
        return getAcceptedMembership(studentId).isPresent();
    }

    @Override
    public Optional<ClanMember> getAcceptedMembership(Long studentId) {
        return memberRepository.findByStudentId(studentId).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst();
    }

    @Override
    public List<UUID> getPendingRequestClanIds(Long studentId) {
        return memberRepository.findByStudentId(studentId).stream()
                .filter(m -> "PENDING_REQUEST".equals(m.getStatus()))
                .map(m -> m.getClan().getId())
                .toList();
    }

    @Override
    public List<ClanMember> getPendingInvitations(Long studentId) {
        return memberRepository.findByStudentId(studentId).stream()
                .filter(m -> "PENDING_INVITE".equals(m.getStatus()))
                .toList();
    }

    private void clearOtherPendingRequestsAndInvites(Long studentId, UUID acceptedClanId) {
        List<ClanMember> allMemberships = memberRepository.findByStudentId(studentId);

        List<ClanMember> toDelete = allMemberships.stream()
                .filter(m -> !m.getClan().getId().equals(acceptedClanId))
                .toList();

        memberRepository.deleteAll(toDelete);
    }

    @Override
    @Transactional
    public void addPoints(Long studentId, int points) {
        memberRepository.findByStudentId(studentId).stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst()
                .ifPresent(member -> {
                    member.setLocalScore(member.getLocalScore() + points);
                    memberRepository.save(member);
                    leaderboardService.updateClanScore(member.getClan());
                });
    }

}