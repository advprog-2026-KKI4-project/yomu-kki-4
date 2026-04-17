package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReadingMaterialRepository {
    private final List<ReadingMaterial> materialData = new ArrayList<>();

    public ReadingMaterial save(ReadingMaterial material) {
        materialData.removeIf(m -> m.getId().equals(material.getId()));
        materialData.add(material);
        return material;
    }

    public List<ReadingMaterial> findAll() {
        return new ArrayList<>(materialData);
    }

    public ReadingMaterial findById(String id) {
        return materialData.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void deleteById(String id) {
        materialData.removeIf(m -> m.getId().equals(id));
    }
}