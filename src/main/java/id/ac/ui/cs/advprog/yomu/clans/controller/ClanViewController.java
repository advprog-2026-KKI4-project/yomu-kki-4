package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/clans")
public class ClanViewController {

    @Autowired
    private ClanService clanService;

    @Autowired
    private ClanRepository clanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClanMemberRepository memberRepository;

    private Long getAuthId(Principal principal) {
        Long id = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
        return id;
    }

    @GetMapping("/create-form")
    public String showCreateForm(Principal principal,
                                 Model model) {
        model.addAttribute("studentId", getAuthId(principal));
        return "clans/createClan";
    }

    @PostMapping("/create")
    public String processCreateClan(@RequestParam String name,
                                    @RequestParam String bio,
                                    Principal principal) {
        clanService.createClan(name, bio, getAuthId(principal));
        return "redirect:/clans/my-clan";
    }

    @GetMapping("/{clanId}/edit-form")
    public String showEditForm(@PathVariable UUID clanId,
                               Principal principal,
                               Model model) {
        Clan clan = clanRepository.findById(clanId).orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Clan not found"));
        model.addAttribute("clan", clan);
        model.addAttribute("studentId", getAuthId(principal));
        return "clans/editClan";
    }

    @PostMapping("/update")
    public String handleUpdate(@RequestParam UUID clanId,
                               Principal principal,
                               @RequestParam String name,
                               @RequestParam String bio) {
        clanService.updateClan(clanId, getAuthId(principal), name, bio);
        return "redirect:/clans/my-clan";
    }

    @GetMapping("/my-clan")
    public String viewMyClan(Principal principal,
                             Model model) {
        Long studentId = getAuthId(principal);
        if (studentId == null) {
            return "redirect:/clans/discover";
        }

        Optional<ClanMember> membership = memberRepository.findByStudentId(studentId)
                .stream()
                .filter(m -> "ACCEPTED".equals(m.getStatus()))
                .findFirst();

        if (membership.isPresent()) {
            model.addAttribute("hasClan", true);
            model.addAttribute("clan", membership.get().getClanId());
            model.addAttribute("myRole", membership.get().getRole());
        } else {
            model.addAttribute("hasClan", false);
        }

        model.addAttribute("currentUri", "/clans/my-clan");
        return "clans/myClanInfo";
    }

    @GetMapping("/discover")
    public String showClanListPage(Principal principal,
                                   Model model) {
        Long studentId = getAuthId(principal);
        model.addAttribute("clans", clanService.findAllClans());

        boolean inClan = memberRepository.findByStudentId(studentId)
                .stream().anyMatch(m -> "ACCEPTED".equals(m.getStatus()));
        model.addAttribute("hasClan", inClan);

        List<UUID> pendingClanIds = memberRepository.findByStudentId(studentId)
                .stream()
                .filter(m -> "PENDING_REQUEST".equals(m.getStatus()))
                .map(m -> m.getClanId().getId())
                .toList();
        model.addAttribute("pendingClanIds", pendingClanIds);

        return "clans/clanList";
    }

    @PostMapping("/leave")
    public String leaveClan(Principal principal) {
        clanService.leaveClan(getAuthId(principal));
        return "redirect:/clans/my-clan";
    }

    @PostMapping("/delete")
    public String deleteClan(@RequestParam UUID clanId,
                             Principal principal) {
        clanService.deleteClan(clanId, getAuthId(principal));
        return "redirect:/clans/my-clan";
    }

    @PostMapping("/join")
    public String requestToJoin(@RequestParam UUID clanId,
                                Principal principal) {
        clanService.requestToJoin(clanId, getAuthId(principal));
        return "redirect:/clans/discover";
    }
}