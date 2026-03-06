package id.ac.ui.cs.advprog.yomu.service;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.repository.ReadingMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadingMaterialService {

    private final ReadingMaterialRepository repository;

    @Autowired
    public ReadingMaterialService(ReadingMaterialRepository repository) {
        this.repository = repository;
    }

    public ReadingMaterial createMaterial(ReadingMaterial material) {
        return repository.save(material);
    }

    public List<ReadingMaterial> getAllMaterials() {
        return repository.findAll();
    }

    public ReadingMaterial getMaterialById(String id) {
        return repository.findById(id);
    }

    public void deleteMaterial(String id) {
        repository.deleteById(id);
    }
}