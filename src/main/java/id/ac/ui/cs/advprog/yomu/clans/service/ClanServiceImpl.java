package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.*;
import id.ac.ui.cs.advprog.yomu.clans.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClanServiceImpl implements ClanService {

    @Autowired
    private ClanRepository clanRepository;

    @Autowired
    private ClanMemberRepository memberRepository;

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
    public void requestToJoin(Long clanId, String studentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        ClanMember request = new ClanMember();
        request.setClanId(clan);
        request.setStudentId(studentId);
        request.setRole("MEMBER");
        request.setStatus("PENDING_REQUEST");
        memberRepository.save(request);
    }

    @Override
    public void approveMember(Long clanId, String leaderId, String targetId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");

        ClanMember member = memberRepository.findByClanAndStudentId(clan, targetId)
                .orElseThrow(() -> new RuntimeException("Membership request not found for this clan"));
        member.setStatus("ACCEPTED");
        memberRepository.save(member);
    }

    @Override
    public void inviteStudent(Long clanId, String leaderId, String targetStudentId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
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
    public void acceptInvitation(Long clanId, String studentId) {
        Clan clan = clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan not found"));

        ClanMember member = memberRepository.findByClanAndStudentId(clan, studentId)
                .orElseThrow(() -> new RuntimeException("Invitation not found for this clan"));

        if (!"PENDING_INVITE".equals(member.getStatus()))
            throw new RuntimeException("No valid invitation found");

        member.setStatus("ACCEPTED");
        memberRepository.save(member);
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
            throw new RuntimeException("Leaders cannot leave");
        memberRepository.delete(activeMember);
    }

    @Override
    @Transactional
    public void deleteClan(Long clanId, String leaderId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        if (!clan.getLeaderId().equals(leaderId))
            throw new RuntimeException("Unauthorized");
        clanRepository.delete(clan);
    }

    @Override
    public List<Clan> findAllClans() {
        return clanRepository.findAll();
    }

}