package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClanService {
    List<Clan> findAllClans();
    Clan createClan(String clanName, String clanBio, Long leaderId);
    Clan updateClan(UUID clanId, Long leaderId, String newName, String newBio);
    void deleteClan(UUID clanId, Long leaderId);

    void requestToJoin(UUID clanId, Long userId);
    void approveMember(UUID clanId, Long leaderId, Long targetUserId);
    void rejectRequest(UUID clanId, Long leaderId, Long targetUserId);

    void inviteStudent(UUID clanId, Long leaderId, Long targetUserId);
    void acceptInvitation(UUID clanId, Long userId);
    void declineInvitation(UUID clanId, Long userId);

    void kickMember(UUID clanId, Long leaderId, Long targetUserId);
    void leaveClan(Long userId);

    boolean isUserInAnyClan(Long studentId);
    Optional<ClanMember> getAcceptedMembership(Long studentId);
    List<UUID> getPendingRequestClanIds(Long studentId);
}