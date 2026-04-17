package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.repository.ClanMemberRepository;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/clans")
public class ClanViewController {

    @Autowired
    private ClanService clanService;

    @Autowired
    private ClanMemberRepository memberRepository;

    @GetMapping("/create-form")
    public String showCreateForm(@RequestParam String studentId, Model model) {
        model.addAttribute("studentId", studentId);
        return "clans/createClan";
    }

    @PostMapping("/create")
    public String processCreateClan(@RequestParam String name,
                                    @RequestParam String bio,
                                    @RequestParam String leaderId) {

        clanService.createClan(name, bio, leaderId);

        return "redirect:/clans/my-clan?studentId=" + leaderId;
    }

    @GetMapping("/my-clan")
    public String viewMyClan(@RequestParam String studentId, Model model) {
        if (studentId == null || studentId.isEmpty()) {
            return "redirect:/clans/discover"; // Or some default
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

        return "clans/myClanInfo";
    }

    @GetMapping("/discover")
    public String showClanListPage(@RequestParam String studentId, Model model) {
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
    public String leaveClan(@RequestParam String studentId) {
        clanService.leaveClan(studentId);
        return "redirect:/clans/my-clan?studentId=" + studentId;
    }

    @PostMapping("/delete")
    public String deleteClan(@RequestParam UUID clanId, @RequestParam String studentId) {
        clanService.deleteClan(clanId, studentId);
        return "redirect:/clans/my-clan?studentId=" + studentId;
    }

    @PostMapping("/join")
    public String requestToJoin(@RequestParam UUID clanId, @RequestParam String studentId) {
        clanService.requestToJoin(clanId, studentId);
        return "redirect:/clans/discover?studentId=" + studentId;
    }
}