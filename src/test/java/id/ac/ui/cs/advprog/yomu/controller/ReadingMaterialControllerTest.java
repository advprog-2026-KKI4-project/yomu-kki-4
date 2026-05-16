package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReadingMaterialController.class)
class ReadingMaterialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReadingMaterialService service;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAddMaterialAsAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/materials/add")
                        .flashAttr("material", new ReadingMaterial()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reading"));

        verify(service, times(1)).add(any(ReadingMaterial.class));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testAddMaterialAsStudentIsForbidden() throws Exception {
        mockMvc.perform(post("/api/admin/materials/add")
                        .flashAttr("material", new ReadingMaterial()))
                .andExpect(status().isForbidden());

        verify(service, never()).add(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteMaterialAsAdmin() throws Exception {
        mockMvc.perform(post("/api/admin/materials/delete/test-id"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reading"));

        verify(service, times(1)).delete("test-id");
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void testDeleteMaterialAsStudentIsForbidden() throws Exception {
        mockMvc.perform(post("/api/admin/materials/delete/test-id"))
                .andExpect(status().isForbidden());

        verify(service, never()).delete(any());
    }
}
