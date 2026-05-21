package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;

import java.util.List;
import java.util.Map;

public interface DiscussionForumService {

    CommentResponse postComment(CommentRequest req, Long authorId);

    List<CommentResponse> getCommentsByMaterial(String materialId, Long currentUserId);

    List<CommentResponse> getAllComments(Long currentUserId);

    CommentResponse editComment(Long id, String newContent, Long authorId);

    void deleteComment(Long id, Long authorId);

    void deleteCommentAsAdmin(Long id);

    CommentResponse reactToComment(Long commentId, ReactionType reactionType, Long userId);

    void removeReaction(Long commentId, Long userId);

    Map<String, Long> getCommentCountsByMaterial();
}