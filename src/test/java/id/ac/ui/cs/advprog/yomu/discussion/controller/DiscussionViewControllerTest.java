package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
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

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DiscussionViewControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ReadingMaterialService readingMaterialService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void discussionPage_materialExists() throws Exception {
        ReadingMaterial m = new ReadingMaterial();
        m.setTitle("Basic Java");
        m.setCategory("Code");
        when(readingMaterialService.getById("mat-1")).thenReturn(m);

        mockMvc.perform(get("/discussion/mat-1"))
                .andExpect(status().isOk())
                .andExpect(view().name("discussion/discussion"))
                .andExpect(model().attribute("materialTitle", "Basic Java"))
                .andExpect(model().attribute("materialExists", true));
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void discussionPage_materialMissing() throws Exception {
        when(readingMaterialService.getById("ghost")).thenReturn(null);

        mockMvc.perform(get("/discussion/ghost"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("materialExists", false))
                .andExpect(model().attribute("materialTitle", "Unknown material"));
    }

    @Test
    @WithAnonymousUser
    void discussionPage_anonymous() throws Exception {
        mockMvc.perform(get("/discussion/mat-1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void discussionIndex_redirects() throws Exception {
        mockMvc.perform(get("/discussion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reading"));
    }
}