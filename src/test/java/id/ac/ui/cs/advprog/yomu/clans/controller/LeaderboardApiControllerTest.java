package id.ac.ui.cs.advprog.yomu.clans.controller;

import id.ac.ui.cs.advprog.yomu.clans.model.Clan;
import id.ac.ui.cs.advprog.yomu.clans.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class LeaderboardApiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private LeaderboardService leaderboardService;

    private MockMvc mockMvc;
    private Clan sampleClan;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        sampleClan = new Clan();
        sampleClan.setId(UUID.randomUUID());
        sampleClan.setName("Bookworm Legends");
        sampleClan.setBio("The best readers in the application.");
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void getGlobalLeaderboard_success() throws Exception {
        when(leaderboardService.getGlobalLeaderboard()).thenReturn(List.of(sampleClan));

        mockMvc.perform(get("/api/leaderboard/global")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Bookworm Legends"));

        verify(leaderboardService).getGlobalLeaderboard();
    }

    @Test
    @WithMockUser(username = "student@yomu.id")
    void getDivisionLeaderboard_success() throws Exception {
        String testDivision = "GOLD";
        when(leaderboardService.getDivisionLeaderboard(testDivision)).thenReturn(List.of(sampleClan));

        mockMvc.perform(get("/api/leaderboard/division/{division}", testDivision)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Bookworm Legends"));

        verify(leaderboardService).getDivisionLeaderboard(testDivision);
    }
}