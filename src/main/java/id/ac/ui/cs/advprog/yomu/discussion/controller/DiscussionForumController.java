package id.ac.ui.cs.advprog.yomu.discussion.controller;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.discussion.config.AuthenticatedUserResolver;
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
@RequiredArgsConstructor
public class DiscussionForumController {

    private final DiscussionForumService service;
    private final AuthenticatedUserResolver authUserResolver;

    @PostMapping
    public ResponseEntity<CommentResponse> post(
            @Valid @RequestBody CommentRequest req,
            Authentication authentication) {
        Long authorId = authUserResolver.requireUserId(authentication);
        return new ResponseEntity<>(service.postComment(req, authorId), HttpStatus.CREATED);
    }

    @GetMapping("/{materialId}")
    public ResponseEntity<List<CommentResponse>> list(
            @PathVariable String materialId,
            Authentication authentication) {
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {
            try {
                User user = authUserResolver.requireUser(authentication);
                currentUserId = user.getId();
            } catch (Exception ignored) {
                // viewing without valid auth is acceptable
            }
        }
        return ResponseEntity.ok(service.getCommentsByMaterial(materialId, currentUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentResponse> edit(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        Long authorId = authUserResolver.requireUserId(authentication);
        String content = payload.get("content");
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content must be filled");
        }
        return ResponseEntity.ok(service.editComment(id, content, authorId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            Authentication authentication) {
        Long authorId = authUserResolver.requireUserId(authentication);
        service.deleteComment(id, authorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reactions")
    public ResponseEntity<CommentResponse> react(
            @PathVariable Long id,
            @Valid @RequestBody ReactionRequest req,
            Authentication authentication) {
        Long userId = authUserResolver.requireUserId(authentication);
        return ResponseEntity.ok(service.reactToComment(id, req.getReactionType(), userId));
    }

    @DeleteMapping("/{id}/reactions")
    public ResponseEntity<Void> removeReaction(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = authUserResolver.requireUserId(authentication);
        service.removeReaction(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> adminDelete(@PathVariable Long id) {
        service.deleteCommentAsAdmin(id);
        return ResponseEntity.noContent().build();
    }
}