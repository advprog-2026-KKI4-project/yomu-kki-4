package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.model.ClanMember;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ClanViewControllerTest {

    @Mock
    private ClanService clanService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClanViewController controller;

    private User user;
    private Principal principal;
    private Clan clan;
    private ClanMember clanMember;
    private UUID clanId;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).email("student@yomu.id").username("student").build();

        principal = mock(Principal.class);
        when(principal.getName()).thenReturn("student@yomu.id");
        when(userRepository.findByEmail("student@yomu.id")).thenReturn(Optional.of(user));

        clanId = UUID.randomUUID();
        clan = new Clan();
        clan.setId(clanId);
        clan.setName("The Readers");
        clan.setBio("A clan for avid readers.");

        clanMember = new ClanMember();
        clanMember.setClan(clan);
        clanMember.setStudentId(1L);
        clanMember.setRole("LEADER");
    }

    @Test
    void showCreateForm_returnsCreateView() {
        String view = controller.showCreateForm();
        assertThat(view).isEqualTo("clans/createClan");
    }

    @Test
    void processCreateClan_callsServiceAndRedirects() {
        String view = controller.processCreateClan("The Readers", "A clan for avid readers.", principal);

        verify(clanService).createClan("The Readers", "A clan for avid readers.", 1L);
        assertThat(view).isEqualTo("redirect:/clans/my-clan");
    }

    @Test
    void showEditForm_returnsEditViewWithClan() {
        when(clanService.findAllClans()).thenReturn(List.of(clan));

        Model model = new ExtendedModelMap();
        String view = controller.showEditForm(clanId, model);

        assertThat(view).isEqualTo("clans/editClan");
        assertThat(model.asMap().get("clan")).isEqualTo(clan);
    }

    @Test
    void showEditForm_throwsNotFound_whenClanDoesNotExist() {
        when(clanService.findAllClans()).thenReturn(Collections.emptyList());
        Model model = new ExtendedModelMap();

        assertThatThrownBy(() -> controller.showEditForm(clanId, model))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Clan not found");
    }

    @Test
    void handleUpdate_callsServiceAndRedirects() {
        String view = controller.handleUpdate(clanId, principal, "New Name", "New Bio");

        verify(clanService).updateClan(clanId, 1L, "New Name", "New Bio");
        assertThat(view).isEqualTo("redirect:/clans/my-clan");
    }

    @Test
    void viewMyClan_withMembership_populatesModel() {
        when(clanService.getAcceptedMembership(1L)).thenReturn(Optional.of(clanMember));

        Model model = new ExtendedModelMap();
        String view = controller.viewMyClan(principal, model);

        assertThat(view).isEqualTo("clans/myClanInfo");
        assertThat(model.asMap().get("hasClan")).isEqualTo(true);
        assertThat(model.asMap().get("clan")).isEqualTo(clan);
        assertThat(model.asMap().get("myRole")).isEqualTo("LEADER");
        assertThat(model.asMap().get("currentUri")).isEqualTo("/clans/my-clan");
    }

    @Test
    void viewMyClan_noMembership_populatesModel() {
        when(clanService.getAcceptedMembership(1L)).thenReturn(Optional.empty());

        Model model = new ExtendedModelMap();
        String view = controller.viewMyClan(principal, model);

        assertThat(view).isEqualTo("clans/myClanInfo");
        assertThat(model.asMap().get("hasClan")).isEqualTo(false);
        assertThat(model.asMap().get("clan")).isNull();
        assertThat(model.asMap().get("currentUri")).isEqualTo("/clans/my-clan");
    }

    @Test
    void showClanListPage_populatesModelAndReturnsView() {
        when(clanService.findAllClans()).thenReturn(List.of(clan));
        when(clanService.isUserInAnyClan(1L)).thenReturn(false);
        when(clanService.getPendingRequestClanIds(1L)).thenReturn(List.of(clanId));
        when(clanService.getPendingInvitations(1L)).thenReturn(Collections.emptyList());

        Model model = new ExtendedModelMap();
        String view = controller.showClanListPage(principal, model);

        assertThat(view).isEqualTo("clans/clanList");
        assertThat(model.asMap().get("clans")).isEqualTo(List.of(clan));
        assertThat(model.asMap().get("hasClan")).isEqualTo(false);
        assertThat(model.asMap().get("pendingClanIds")).isEqualTo(List.of(clanId));
        assertThat(model.asMap().get("myInvitations")).isEqualTo(Collections.emptyList());
    }

    @Test
    void leaveClan_callsServiceAndRedirects() {
        String view = controller.leaveClan(principal);

        verify(clanService).leaveClan(1L);
        assertThat(view).isEqualTo("redirect:/clans/my-clan");
    }

    @Test
    void deleteClan_callsServiceAndRedirects() {
        String view = controller.deleteClan(clanId, principal);

        verify(clanService).deleteClan(clanId, 1L);
        assertThat(view).isEqualTo("redirect:/clans/my-clan");
    }

    @Test
    void requestToJoin_callsServiceAndRedirects() {
        String view = controller.requestToJoin(clanId, principal);

        verify(clanService).requestToJoin(clanId, 1L);
        assertThat(view).isEqualTo("redirect:/clans/discover");
    }

    @Test
    void getAuthId_throwsException_whenUserNotFound() {
        when(userRepository.findByEmail("student@yomu.id")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.leaveClan(principal))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}