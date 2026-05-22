package id.ac.ui.cs.advprog.yomu.learningandquiz.repository;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Repository
public class ReadingMaterialRepository {

    private final List<ReadingMaterial> materials = new ArrayList<>();

    public ReadingMaterial save(ReadingMaterial material) {
        if (material.getId() == null || material.getId().isEmpty()) {
            material.setId(UUID.randomUUID().toString());
        }
        materials.removeIf(existing -> Objects.equals(existing.getId(), material.getId()));
        materials.add(material);
        return material;
    }

    public List<ReadingMaterial> findAll() {
        return new ArrayList<>(materials);
    }

    public ReadingMaterial findById(String id) {
        return materials.stream()
                .filter(m -> Objects.equals(m.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public void deleteById(String id) {
        materials.removeIf(m -> Objects.equals(m.getId(), id));
    }
}