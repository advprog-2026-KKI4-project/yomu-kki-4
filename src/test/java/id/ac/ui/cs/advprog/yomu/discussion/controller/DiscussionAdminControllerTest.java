package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DiscussionAdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private ReadingMaterialService readingMaterialService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDiscussionIndex() throws Exception {
        ReadingMaterial mockMaterial = new ReadingMaterial();
        when(readingMaterialService.getAll()).thenReturn(List.of(mockMaterial));

        mockMvc.perform(get("/discussions"))
                .andExpect(status().isOk())
                .andExpect(view().name("discussion/discussionAdmin"))
                .andExpect(model().attributeExists("materials"))
                .andExpect(model().attribute("currentUri", "/discussions"));
    }
}