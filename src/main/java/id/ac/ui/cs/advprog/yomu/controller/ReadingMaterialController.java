package id.ac.ui.cs.advprog.yomu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/materials")
public class ReadingMaterialController {

    @GetMapping
    public ResponseEntity<List<Map<String, String>>> getAllMaterials() {
        List<Map<String, String>> dummyMaterials = List.of(
                Map.of("id", "1", "title", "Introduction to Padel", "category", "Sport"),
                Map.of("id", "2", "title", "PostgreSQL Triggers Guide", "category", "Technology")
        );

        return ResponseEntity.ok(dummyMaterials);
    }
}