package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.Clan;
import id.ac.ui.cs.advprog.yomu.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<String> requestToJoin(@PathVariable Long clanId,
                                                @RequestParam String studentId) {
        clanService.requestToJoin(clanId, studentId);
        return ResponseEntity.ok("Request sent to clan leader.");
    }

    @PostMapping("/{clanId}/approve")
    public ResponseEntity<String> approveMember(@PathVariable Long clanId,
                                                @RequestParam String leaderId,
                                                @RequestParam String targetStudentId) {
        clanService.approveMember(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Member approved.");
    }

    @PostMapping("/{clanId}/invite")
    public ResponseEntity<String> inviteMember(@PathVariable Long clanId,
                                               @RequestParam String leaderId,
                                               @RequestParam String targetStudentId) {
        clanService.inviteStudent(clanId, leaderId, targetStudentId);
        return ResponseEntity.ok("Invitation sent successfully.");
    }

    @PostMapping("/{clanId}/accept-invitation")
    public ResponseEntity<String> acceptInvitation(@PathVariable Long clanId,
                                                   @RequestParam String studentId) {
        clanService.acceptInvitation(clanId, studentId);
        return ResponseEntity.ok("Invitation accepted! You are now a member of the clan.");
    }

    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveClan(@RequestParam String studentId) {
        clanService.leaveClan(studentId);
        return ResponseEntity.ok("You have left the clan.");
    }

    @DeleteMapping("/{clanId}/delete")
    public ResponseEntity<String> deleteClan(@PathVariable Long clanId,
                                             @RequestParam String leaderId) {
        clanService.deleteClan(clanId, leaderId);
        return ResponseEntity.ok("Clan deleted successfully.");
    }
}