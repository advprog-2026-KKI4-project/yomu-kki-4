package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;

import java.util.List;

public interface DiscussionForumService {
    CommentResponse postComment(CommentRequest req);

    List<CommentResponse> getCommentsByMaterial(String materialId, String currentUserId);

    CommentResponse editComment(Long id, String newContent, String authorId);

    void deleteComment(Long id, String authorId);

    void deleteCommentAsAdmin(Long id);

    CommentResponse reactToComment(Long commentId, ReactionType reactionType, String userId);

    void removeReaction(Long commentId, String userId);
}