package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clans")
public class ClanApiController {

    private final ClanService clanService;

    @Autowired
    public ClanApiController(ClanService clanService) {
        this.clanService = clanService;
    }

    @PostMapping("/create")
    public ResponseEntity<Clan> createClan(@RequestParam String name,
                                           @RequestParam String bio,
                                           @RequestParam String leaderId) {
        return ResponseEntity.ok(clanService.createClan(name, bio, leaderId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Clan>> getAllClans() {
        return ResponseEntity.ok(clanService.findAllClans());
    }

    @PostMapping("/{clanId}/request")
    public ResponseEntity<String> requestToJoin(@PathVariable UUID clanId,
                                                @RequestParam String studentId) {
        clanService.requestToJoin(clanId, studentId);
        return ResponseEntity.ok("Request sent to clan leader.");
    }

    @PostMapping("/{clanId}/approve")
    public ResponseEntity<String> approveMember(@PathVariable UUID clanId,
                                                @RequestParam String leaderId,
                                                @RequestParam String targetStudentId) {
        clanService.approveMember(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Member approved.");
    }

    @PostMapping("/{clanId}/reject/{targetStudentId}")
    public ResponseEntity<String> rejectRequest(@PathVariable UUID clanId,
                                                @RequestParam String leaderId,
                                                @PathVariable String targetStudentId) {
        clanService.rejectRequest(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Join request rejected and removed.");
    }

    @PostMapping("/{clanId}/invite")
    public ResponseEntity<String> inviteMember(@PathVariable UUID clanId,
                                               @RequestParam String leaderId,
                                               @RequestParam String targetStudentId) {
        clanService.inviteStudent(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Invitation sent successfully.");
    }

    @PostMapping("/{clanId}/accept-invitation")
    public ResponseEntity<String> acceptInvitation(@PathVariable UUID clanId,
                                                   @RequestParam String studentId) {
        clanService.acceptInvitation(clanId, studentId);
        return ResponseEntity.ok("Invitation accepted! You are now a member of the clan.");
    }

    @PostMapping("/{clanId}/decline")
    public ResponseEntity<String> declineInvitation(@PathVariable UUID clanId,
                                                    @RequestParam String studentId) {
        clanService.declineInvitation(clanId, studentId);
        return ResponseEntity.ok("Invitation declined and removed.");
    }

    @PostMapping("/{clanId}/kick/{targetStudentId}")
    public ResponseEntity<?> kick(@PathVariable UUID clanId,
                                  @RequestParam String leaderId,
                                  @PathVariable String targetStudentId) {
        clanService.kickMember(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Member kicked successfully");
    }

    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveClan(@RequestParam String studentId) {
        clanService.leaveClan(studentId);
        return ResponseEntity.ok("You have left the clan.");
    }

    @DeleteMapping("/{clanId}/delete")
    public ResponseEntity<String> deleteClan(@PathVariable UUID clanId,
                                             @RequestParam String leaderId) {
        clanService.deleteClan(clanId, leaderId);
        return ResponseEntity.ok("Clan deleted successfully.");
    }

    // Temporary Endpoint
    @PostMapping("/test/update-member-score")
    public ResponseEntity<String> updateMemberScore(@RequestParam String studentId,
                                                    @RequestParam int newScore) {

        clanService.updateMemberScoreMock(studentId, newScore);
        return ResponseEntity.ok("Score updated successfully.");
    }
}