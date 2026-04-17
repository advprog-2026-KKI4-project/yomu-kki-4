package id.ac.ui.cs.advprog.yomu.clans.service;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import java.util.List;

public interface ClanService {
    Clan createClan(String clanName, String clanBio, String leaderId);
    void requestToJoin(Long clanId, String studentId);
    void approveMember(Long clanId, String leaderId, String targetStudentId);
    void inviteStudent(Long clanId, String leaderId, String targetStudentId);
    void acceptInvitation(Long clanId, String studentId);
    void leaveClan(String studentId);
    void deleteClan(Long clanId, String leaderId);
    List<Clan> findAllClans();
}