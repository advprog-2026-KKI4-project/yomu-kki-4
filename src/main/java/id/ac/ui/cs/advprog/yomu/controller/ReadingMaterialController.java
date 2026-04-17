package id.ac.ui.cs.advprog.yomu.controller;

import id.ac.ui.cs.advprog.yomu.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReadingMaterialController {

    private final ReadingMaterialService service;

    @Autowired
    public ReadingMaterialController(ReadingMaterialService service) {
        this.service = service;
    }

    @GetMapping("/materials")
    public ResponseEntity<List<ReadingMaterial>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }

    @GetMapping("/materials/{id}")
    public ResponseEntity<ReadingMaterial> getById(@PathVariable String id) {
        ReadingMaterial material = service.getById(id);
        return material != null ?
                new ResponseEntity<>(material, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/materials/{id}/submit")
    public ResponseEntity<?> submitQuiz(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestParam long duration,
            @RequestBody List<Integer> answers) {
        try {
            double score = service.submitQuiz(userId, id, answers, duration);
            return new ResponseEntity<>(Map.of("score", score), HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", "Quiz submission failed"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/admin/materials")
    public ResponseEntity<ReadingMaterial> add(@RequestBody ReadingMaterial material) {
        return new ResponseEntity<>(service.add(material), HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/materials/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}