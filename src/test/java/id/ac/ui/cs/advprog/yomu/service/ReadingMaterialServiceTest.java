package id.ac.ui.cs.advprog.yomu.service;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.repository.ReadingMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReadingMaterialServiceTest {

    @Mock
    private ReadingMaterialRepository repository;

    @InjectMocks
    private ReadingMaterialService service;

    private ReadingMaterial material;

    @BeforeEach
    void setUp() {
        material = new ReadingMaterial("Title", "Content", "Category");
    }

    @Test
    void testCreateMaterial() {
        when(repository.save(material)).thenReturn(material);
        ReadingMaterial created = service.createMaterial(material);
        assertEquals(material, created);
        verify(repository, times(1)).save(material);
    }

    @Test
    void testGetAllMaterials() {
        when(repository.findAll()).thenReturn(Arrays.asList(material));
        List<ReadingMaterial> list = service.getAllMaterials();
        assertEquals(1, list.size());
    }
}