package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clans")
public class ClanApiController {

    @Autowired
    private ClanService clanService;

    @Autowired
    private UserRepository userRepository;

    private Long getAuthId(Principal principal) {
        Long userId = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return userId;
    }

    @PostMapping("/create")
    public ResponseEntity<Clan> createClan(@RequestParam String name,
                                           @RequestParam String bio,
                                           Principal principal) {
        return ResponseEntity.ok(clanService.createClan(name, bio, getAuthId(principal)));
    }

    @PutMapping("/{clanId}/update")
    public ResponseEntity<Clan> updateClanApi(@PathVariable UUID clanId,
                                              Principal principal,
                                              @RequestParam String name,
                                              @RequestParam String bio) {
        Clan updatedClan = clanService.updateClan(clanId, getAuthId(principal), name, bio);
        return ResponseEntity.ok(updatedClan);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Clan>> getAllClans() {
        return ResponseEntity.ok(clanService.findAllClans());
    }

    @PostMapping("/{clanId}/request")
    public ResponseEntity<String> requestToJoin(@PathVariable UUID clanId,
                                                Principal principal) {
        clanService.requestToJoin(clanId, getAuthId(principal));
        return ResponseEntity.ok("Request sent to clan leader.");
    }

    @PostMapping("/{clanId}/approve/{targetStudentId}")
    public ResponseEntity<String> approveMember(@PathVariable UUID clanId,
                                                Principal principal,
                                                @PathVariable Long targetStudentId) {
        clanService.approveMember(clanId, getAuthId(principal), targetStudentId);
        return ResponseEntity.ok("Member approved.");
    }

    @PostMapping("/{clanId}/reject/{targetStudentId}")
    public ResponseEntity<String> rejectRequest(@PathVariable UUID clanId,
                                                Principal principal,
                                                @PathVariable Long targetStudentId) {
        clanService.rejectRequest(clanId, getAuthId(principal), targetStudentId);
        return ResponseEntity.ok("Join request rejected and removed.");
    }

    @PostMapping("/{clanId}/invite")
    public ResponseEntity<String> inviteMember(@PathVariable UUID clanId,
                                               Principal principal,
                                               @RequestParam Long targetStudentId) {
        clanService.inviteStudent(clanId, getAuthId(principal), targetStudentId);
        return ResponseEntity.ok("Invitation sent successfully.");
    }

    @PostMapping("/{clanId}/accept-invitation")
    public ResponseEntity<String> acceptInvitation(@PathVariable UUID clanId,
                                                   Principal principal) {
        clanService.acceptInvitation(clanId, getAuthId(principal));
        return ResponseEntity.ok("Invitation accepted! You are now a member of the clan.");
    }

    @PostMapping("/{clanId}/decline")
    public ResponseEntity<String> declineInvitation(@PathVariable UUID clanId,
                                                    Principal principal) {
        clanService.declineInvitation(clanId, getAuthId(principal));
        return ResponseEntity.ok("Invitation declined and removed.");
    }

    @PostMapping("/{clanId}/kick/{targetStudentId}")
    public ResponseEntity<?> kick(@PathVariable UUID clanId,
                                  Principal principal,
                                  @PathVariable Long targetStudentId) {
        clanService.kickMember(clanId, getAuthId(principal), targetStudentId);
        return ResponseEntity.ok("Member kicked successfully");
    }

    @DeleteMapping("/leave")
    public ResponseEntity<String> leaveClan(Principal principal) {
        clanService.leaveClan(getAuthId(principal));
        return ResponseEntity.ok("You have left the clan.");
    }

    @DeleteMapping("/{clanId}/delete")
    public ResponseEntity<String> deleteClan(@PathVariable UUID clanId,
                                             Principal principal) {
        clanService.deleteClan(clanId, getAuthId(principal));
        return ResponseEntity.ok("Clan deleted successfully.");
    }
}