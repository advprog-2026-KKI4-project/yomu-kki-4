package id.ac.ui.cs.advprog.yomu.learningandquiz.controller;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReadingMaterialAdminControllerTest {

    private ReadingMaterialController controller;
    private ReadingMaterialService service;

    @BeforeEach
    void setUp() {
        service = mock(ReadingMaterialService.class);
        controller = new ReadingMaterialController(service);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "admin@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testAddMaterialSuccess() {
        ReadingMaterial material = new ReadingMaterial();
        String view = controller.add(material);

        assertEquals("redirect:/reading", view);
        verify(service, times(1)).add(material);
    }

    @Test
    void testUpdateExistingMaterialSuccess() {
        String materialId = "java-001";
        ReadingMaterial existingMaterial = new ReadingMaterial();
        existingMaterial.setId(materialId);
        existingMaterial.setTitle("Old Title");

        ReadingMaterial updatedForm = new ReadingMaterial();
        updatedForm.setTitle("New Title");
        updatedForm.setCategory("Code");
        updatedForm.setContent("Updated content body");
        updatedForm.setTimeLimit(120);
        updatedForm.setQuestions(new ArrayList<>());

        when(service.getById(materialId)).thenReturn(existingMaterial);

        String view = controller.update(materialId, updatedForm);

        assertEquals("redirect:/reading", view);
        verify(service, times(1)).add(existingMaterial);
        assertEquals("New Title", existingMaterial.getTitle());
        assertEquals("Code", existingMaterial.getCategory());
    }

    @Test
    void testUpdateMaterialNotFound() {
        String materialId = "ghost-id";
        ReadingMaterial updatedForm = new ReadingMaterial();

        when(service.getById(materialId)).thenReturn(null);

        String view = controller.update(materialId, updatedForm);

        assertEquals("redirect:/reading", view);
        verify(service, never()).add(any());
    }

    @Test
    void testDeleteMaterialSuccess() {
        String materialId = "math-101";
        String view = controller.delete(materialId);

        assertEquals("redirect:/reading", view);
        verify(service, times(1)).delete(materialId);
    }
}