package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomu.discussion.service.DiscussionForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DiscussionForumController {

    private final DiscussionForumService service;

    // TODO(auth): once auth is connected:
    //   - add `Authentication authentication` parameter to each protected method
    //   - replace `req.getAuthorId()` / `payload.get("authorId")` / query params
    //     with `authentication.getName()` (or resolved User.id)
    //   - add @PreAuthorize("hasRole('ADMIN')") to /admin/{id}
    //   - remove the userId/authorId fields from request DTOs

    @PostMapping
    public ResponseEntity<CommentResponse> post(@Valid @RequestBody CommentRequest req) {
        return new ResponseEntity<>(service.postComment(req), HttpStatus.CREATED);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<List<CommentResponse>> list(
            @PathVariable String materialId,
            @RequestParam(required = false) String currentUserId) {
        // TODO(auth): get currentUserId from Authentication instead of query param
        return ResponseEntity.ok(service.getCommentsByMaterial(materialId, currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> edit(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        // TODO(auth): replace payload.get("authorId") with authentication.getName()
        return ResponseEntity.ok(
                service.editComment(id, payload.get("content"), payload.get("authorId")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam String authorId) {
        // TODO(auth): replace @RequestParam authorId with authentication.getName()
        service.deleteComment(id, authorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactions")
    public ResponseEntity<CommentResponse> react(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequest req) {
        // TODO(auth): get userId from Authentication instead of req.getUserId()
        return ResponseEntity.ok(
                service.reactToComment(id, req.getReactionType(), req.getUserId()));
    }

    @DeleteMapping("/{id}/reactions")
    public ResponseEntity<Void> removeReaction(
            @PathVariable Long id,
            @RequestParam String userId) {
        // TODO(auth): replace @RequestParam userId with authentication.getName()
        service.removeReaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        // TODO(auth): add @PreAuthorize("hasRole('ADMIN')") here
        service.deleteCommentAsAdmin(id);
        return ResponseEntity.noContent().build();
    }
}