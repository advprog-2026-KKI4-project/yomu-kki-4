package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.discussion.config.AuthenticatedUserResolver;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import id.ac.ui.cs.advprog.yomu.discussion.service.DiscussionForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class DiscussionForumControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private DiscussionForumService service;

    @MockitoBean
    private AuthenticatedUserResolver authUserResolver;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
    }

    private CommentResponse sample() {
        return CommentResponse.builder().id(1L).content("hi").materialId("m1").authorId(1L).authorUsername("elision")
                .reactionCounts(Collections.emptyMap()).ownedByCurrentUser(true).build();
    }

    @Test
    @WithMockUser(username = "elision@yomu.id")
    void post_success() throws Exception {
        when(authUserResolver.requireUserId(any())).thenReturn(1L);
        when(service.postComment(any(), anyLong())).thenReturn(sample());

        mockMvc.perform(post("/api/discussions")
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"hi\",\"materialId\":\"m1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("hi"));
    }

    @Test
    @WithMockUser(username = "elision@yomu.id")
    void post_validationError() throws Exception {
        mockMvc.perform(post("/api/discussions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"\",\"materialId\":\"m1\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "elision@yomu.id")
    void list_success() throws Exception {
        User user = new User();
        user.setId(1L);
        when(authUserResolver.requireUser(any())).thenReturn(user);
        when(service.getCommentsByMaterial(anyString(), any())).thenReturn(List.of(sample()));

        mockMvc.perform(get("/api/discussions/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @WithAnonymousUser
    void list_anonymous() throws Exception {
        mockMvc.perform(get("/api/discussions/m1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "elision@yomu.id")
    void edit_success() throws Exception {
        when(authUserResolver.requireUserId(any())).thenReturn(1L);
        when(service.editComment(anyLong(), anyString(), anyLong())).thenReturn(sample());

        mockMvc.perform(put("/api/discussions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void delete_success() throws Exception {
        when(authUserResolver.requireUserId(any())).thenReturn(1L);

        mockMvc.perform(delete("/api/discussions/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deleteComment(1L, 1L);
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void react_success() throws Exception {
        when(authUserResolver.requireUserId(any())).thenReturn(1L);
        when(service.reactToComment(anyLong(), any(ReactionType.class), anyLong()))
                .thenReturn(sample());

        mockMvc.perform(post("/api/discussions/1/reactions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"reactionType\":\"LIKE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void removeReaction_success() throws Exception {
        when(authUserResolver.requireUserId(any())).thenReturn(1L);

        mockMvc.perform(delete("/api/discussions/1/reactions").with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).removeReaction(1L, 1L);
    }

    @Test
    @WithMockUser(username = "admin@yomu.id", roles = {"ADMIN"})
    void adminDelete_success() throws Exception {
        mockMvc.perform(delete("/api/discussions/admin/5").with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).deleteCommentAsAdmin(5L);
    }

    @Test
    @WithMockUser(username = "alice@yomu.id")
    void getCommentCounts_success() throws Exception {
        when(service.getCommentCountsByMaterial()).thenReturn(Map.of("m1", 3L, "m2", 1L));

        mockMvc.perform(get("/api/discussions/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.m1").value(3L));
    }
}