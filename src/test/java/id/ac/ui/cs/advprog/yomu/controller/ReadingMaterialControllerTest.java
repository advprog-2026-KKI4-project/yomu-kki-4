package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReadingMaterialControllerTest {

    @Autowired
    private ReadingMaterialController controller;

    @Autowired
    private ReadingMaterialService service;

    @Test
    void testGetAllMaterials() {
        // 1. Add real data via the real service
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Integration Test Material");
        service.add(mat);

        // 2. Call the controller
        ResponseEntity<List<ReadingMaterial>> response = controller.getAll();

        // 3. Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().stream()
                .anyMatch(m -> m.getTitle().equals("Integration Test Material")));
    }

    @Test
    void testGetById() {
        ReadingMaterial mat = new ReadingMaterial();
        mat.setTitle("Specific Math Material");
        ReadingMaterial saved = service.add(mat);

        ResponseEntity<ReadingMaterial> response = controller.getById(saved.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Specific Math Material", response.getBody().getTitle());
    }

    @Test
    void testDeleteMaterial() {
        ReadingMaterial mat = new ReadingMaterial();
        ReadingMaterial saved = service.add(mat);

        ResponseEntity<Void> response = controller.delete(saved.getId());

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(service.getById(saved.getId()));
    }
}