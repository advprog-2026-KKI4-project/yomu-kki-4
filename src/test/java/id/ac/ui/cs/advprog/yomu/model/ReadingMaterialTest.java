package id.ac.ui.cs.advprog.yomu.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReadingMaterialTest {
    private ReadingMaterial material;

    @BeforeEach
    void setUp() {
        material = new ReadingMaterial("Tech News", "AI is growing.", "Technology");
    }

    @Test
    void testReadingMaterialCreation() {
        assertNotNull(material.getId()); // ID should be auto-generated
        assertEquals("Tech News", material.getTitle());
        assertEquals("AI is growing.", material.getContent());
        assertEquals("Technology", material.getCategory());
    }

    @Test
    void testSetters() {
        material.setTitle("Sports Update");
        assertEquals("Sports Update", material.getTitle());
    }
}