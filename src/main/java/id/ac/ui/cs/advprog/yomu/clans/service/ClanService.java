package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import java.util.List;
import java.util.UUID;

public interface ClanService {
    Clan createClan(String clanName, String clanBio, Long leaderId);
    Clan updateClan(UUID clanId, Long leaderId, String newName, String newBio);
    void requestToJoin(UUID clanId, Long userId);
    void approveMember(UUID clanId, Long leaderId, Long targetUserId);
    void rejectRequest(UUID clanId, Long leaderId, Long targetUserId);
    void inviteStudent(UUID clanId, Long leaderId, Long targetUserId);
    void acceptInvitation(UUID clanId, Long userId);
    void declineInvitation(UUID clanId, Long userId);
    void kickMember(UUID clanId, Long leaderId, Long targetUserId);
    void leaveClan(Long userId);
    void deleteClan(UUID clanId, Long leaderId);
    List<Clan> findAllClans();

    void updateMemberScoreMock(Long studentId, int newScore); // Temporary use

    void addPoints(Long studentId, int points);
}