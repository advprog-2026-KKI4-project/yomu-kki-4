package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadingMaterialControllerTest {

    @Mock
    private ReadingMaterialService service;

    @InjectMocks
    private ReadingMaterialController controller;

    private ReadingMaterial material;

    @BeforeEach
    void setUp() {
        material = new ReadingMaterial("News", "Read this", "Sport");
    }

    @Test
    void testGetAllMaterials() {
        // Mock the service behavior
        when(service.getAllMaterials()).thenReturn(Arrays.asList(material));

        // Call the controller directly
        ResponseEntity<List<ReadingMaterial>> response = controller.getAllMaterials();

        // Verify the results
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("News", response.getBody().get(0).getTitle());
    }

    @Test
    void testAddMaterial() {
        // Mock the service behavior
        when(service.createMaterial(any(ReadingMaterial.class))).thenReturn(material);

        // Call the controller directly
        ResponseEntity<ReadingMaterial> response = controller.addMaterial(material);

        // Verify the results
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("News", response.getBody().getTitle());
    }
}