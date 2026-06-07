package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class ClanApiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ClanService clanService;

    @MockitoBean
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private User testUser;
    private Clan testClan;
    private UUID clanId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        // Initialize shared test data
        testUser = User.builder()
                .id(1L)
                .email("student@yomu.id")
                .username("student")
                .password("password")
                .build();

        clanId = UUID.randomUUID();

        testClan = new Clan();
        testClan.setId(clanId);
        testClan.setName("Yomu Warriors");
        testClan.setBio("We read everything.");
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void createClan_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(clanService.createClan(anyString(), anyString(), anyLong())).thenReturn(testClan);

        mockMvc.perform(post("/api/clans/create")
                        .with(csrf())
                        .param("name", "Yomu Warriors")
                        .param("bio", "We read everything."))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yomu Warriors"));

        verify(clanService).createClan("Yomu Warriors", "We read everything.", 1L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void updateClanApi_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(clanService.updateClan(eq(clanId), anyLong(), anyString(), anyString())).thenReturn(testClan);

        mockMvc.perform(put("/api/clans/{clanId}/update", clanId)
                        .with(csrf())
                        .param("name", "Yomu Warriors")
                        .param("bio", "Updated bio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yomu Warriors"));

        verify(clanService).updateClan(clanId, 1L, "Yomu Warriors", "Updated bio");
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void getAllClans_success() throws Exception {
        when(clanService.findAllClans()).thenReturn(List.of(testClan));

        mockMvc.perform(get("/api/clans/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Yomu Warriors"));
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void requestToJoin_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/clans/{clanId}/request", clanId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Request sent to clan leader."));

        verify(clanService).requestToJoin(clanId, 1L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void approveMember_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        Long targetStudentId = 2L;

        mockMvc.perform(post("/api/clans/{clanId}/approve/{targetStudentId}", clanId, targetStudentId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Member approved."));

        verify(clanService).approveMember(clanId, 1L, targetStudentId);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void rejectRequest_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        Long targetStudentId = 2L;

        mockMvc.perform(post("/api/clans/{clanId}/reject/{targetStudentId}", clanId, targetStudentId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Join request rejected and removed."));

        verify(clanService).rejectRequest(clanId, 1L, targetStudentId);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void inviteMember_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/clans/{clanId}/invite", clanId)
                        .with(csrf())
                        .param("targetStudentId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation sent successfully."));

        verify(clanService).inviteStudent(clanId, 1L, 2L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void acceptInvitation_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/clans/{clanId}/accept-invitation", clanId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation accepted! You are now a member of the clan."));

        verify(clanService).acceptInvitation(clanId, 1L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void declineInvitation_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(post("/api/clans/{clanId}/decline", clanId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Invitation declined and removed."));

        verify(clanService).declineInvitation(clanId, 1L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void kickMember_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        Long targetStudentId = 2L;

        mockMvc.perform(post("/api/clans/{clanId}/kick/{targetStudentId}", clanId, targetStudentId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Member kicked successfully"));

        verify(clanService).kickMember(clanId, 1L, targetStudentId);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void leaveClan_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/api/clans/leave")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("You have left the clan."));

        verify(clanService).leaveClan(1L);
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void deleteClan_success() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        mockMvc.perform(delete("/api/clans/{clanId}/delete", clanId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Clan deleted successfully."));

        verify(clanService).deleteClan(clanId, 1L);
    }

    @Test
    @WithAnonymousUser
    void unauthenticatedRequest_redirects() throws Exception {
        mockMvc.perform(post("/api/clans/create")
                        .with(csrf())
                        .param("name", "Test")
                        .param("bio", "Test"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "notfound@yomu.id")
    void getAuthId_throwsException_whenUserNotFound() throws Exception {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/clans/create")
                        .with(csrf())
                        .param("name", "Test")
                        .param("bio", "Test"));
    }
}