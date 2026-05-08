package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.dto.ReactionRequest;
import id.ac.ui.cs.advprog.yomu.discussion.service.DiscussionForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/discussions")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DiscussionForumController {

    private final DiscussionForumService service;

    @PostMapping
    public ResponseEntity<CommentResponse> postComment(
            @Valid @RequestBody CommentRequest requestDTO,
            Authentication authentication) {
        CommentResponse saved = service.postComment(requestDTO, requireIdentity(authentication));
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable String materialId,
            Authentication authentication) {
        String identity = (authentication != null && authentication.isAuthenticated())
                ? authentication.getName() : null;
        return ResponseEntity.ok(service.getCommentsByMaterial(materialId, identity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> editComment(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        String content = payload.get("content");
        return ResponseEntity.ok(service.editComment(id, content, requireIdentity(authentication)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id, Authentication authentication) {
        service.deleteComment(id, requireIdentity(authentication));
        return ResponseEntity.noContent().build();
    }

    // ---- Reactions ----

    @PostMapping("/{id}/reactions")
    public ResponseEntity<CommentResponse> react(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequest req,
            Authentication authentication) {
        return ResponseEntity.ok(
                service.reactToComment(id, req.getReactionType(), requireIdentity(authentication)));
    }

    @DeleteMapping("/{id}/reactions")
    public ResponseEntity<Void> removeReaction(@PathVariable Long id, Authentication authentication) {
        service.removeReaction(id, requireIdentity(authentication));
        return ResponseEntity.noContent().build();
    }

    // ---- Admin moderation ----

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        service.deleteCommentAsAdmin(id);
        return ResponseEntity.noContent().build();
    }

    private String requireIdentity(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return authentication.getName();
    }
}