package id.ac.ui.cs.advprog.yomu.repository;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ReadingMaterialRepository {
    private final List<ReadingMaterial> materialData = new ArrayList<>();

    public ReadingMaterial save(ReadingMaterial material) {
        materialData.add(material);
        return material;
    }

    public List<ReadingMaterial> findAll() {
        return materialData;
    }

    public ReadingMaterial findById(String id) {
        for (ReadingMaterial material : materialData) {
            if (material.getId().equals(id)) {
                return material;
            }
        }
        return null;
    }

    public void deleteById(String id) {
        materialData.removeIf(material -> material.getId().equals(id));
    }
}