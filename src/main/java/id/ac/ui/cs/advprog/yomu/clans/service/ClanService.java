package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import java.util.List;
import java.util.UUID;

public interface ClanService {
    Clan createClan(String clanName, String clanBio, String leaderId);
    void requestToJoin(UUID clanId, String studentId);
    void approveMember(UUID clanId, String leaderId, String targetStudentId);
    void rejectRequest(UUID clanId, String leaderId, String targetStudentId);
    void inviteStudent(UUID clanId, String leaderId, String targetStudentId);
    void acceptInvitation(UUID clanId, String studentId);
    void declineInvitation(UUID clanId, String studentId);
    void kickMember(UUID clanId, String leaderId, String targetStudentId);
    void leaveClan(String studentId);
    void deleteClan(UUID clanId, String leaderId);
    List<Clan> findAllClans();

    void updateMemberScoreMock(String studentId, int newScore); // Temporary use
}