package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.service.DiscussionForumService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public ResponseEntity<DiscussionForum> postComment(
            @Valid @RequestBody CommentRequest requestDTO,
            Principal principal) {

        // for testing purposes , at finishing i will change this code
        if (principal != null) {
            requestDTO.setAuthorId(principal.getName());
        } else {
            requestDTO.setAuthorId("Anonymous_Tester"); //for testing before the auth finished
        }

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
            @RequestBody Map<String, String> payload,
            Principal principal) {

        String content = payload.get("content");
        //use a dummy string so the service doesn't crash (for testing, before auth finished)
        String currentAuthor = (principal != null) ? principal.getName() : "Anonymous_Tester";

        DiscussionForum updated = service.editComment(id, content, currentAuthor);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Principal principal) {
        String currentAuthor = (principal != null) ? principal.getName() : "Anonymous_Tester";
        service.deleteComment(id, currentAuthor);
        return ResponseEntity.noContent().build();
    }

}