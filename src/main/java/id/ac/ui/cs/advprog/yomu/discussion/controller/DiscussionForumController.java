package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.service.DiscussionForumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin(origins = "*")
public class DiscussionForumController {

    private final DiscussionForumService service;

    @Autowired
    public DiscussionForumController(DiscussionForumService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DiscussionForum> postComment(@Valid @RequestBody CommentRequest requestDTO) {
        DiscussionForum savedComment = service.postComment(requestDTO);
        return new ResponseEntity<>(savedComment, HttpStatus.CREATED);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<List<DiscussionForum>> getComments(@PathVariable String materialId) {
        List<DiscussionForum> comments = service.getCommentsByMaterial(materialId);
        return ResponseEntity.ok(comments);
    }
    @PutMapping("/{id}")
    public ResponseEntity<DiscussionForum> editComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String content = payload.get("content");
        String authorId = payload.get("authorId");

        if(content == null || content.trim().isEmpty() || authorId == null) {
            return ResponseEntity.badRequest().build();
        }

        DiscussionForum updated = service.editComment(id, content, authorId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @RequestParam String authorId) {
        service.deleteComment(id, authorId);
        return ResponseEntity.noContent().build();
    }

}