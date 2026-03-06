package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReadingMaterialController {

    private final ReadingMaterialService service;

    @Autowired
    public ReadingMaterialController(ReadingMaterialService service) {
        this.service = service;
    }

    @GetMapping("/materials")
    public ResponseEntity<List<ReadingMaterial>> getAllMaterials() {
        return new ResponseEntity<>(service.getAllMaterials(), HttpStatus.OK);
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<ReadingMaterial> getMaterialById(@PathVariable String id) {
        ReadingMaterial material = service.getMaterialById(id);
        return material != null ?
                new ResponseEntity<>(material, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin/materials")
    public ResponseEntity<ReadingMaterial> addMaterial(@RequestBody ReadingMaterial material) {
        ReadingMaterial createdMaterial = service.createMaterial(material);
        return new ResponseEntity<>(createdMaterial, HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/materials/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable String id) {
        service.deleteMaterial(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}