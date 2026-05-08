package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;

import java.util.List;

public interface DiscussionForumService {

    CommentResponse postComment(CommentRequest requestDTO, String identity);

    List<CommentResponse> getCommentsByMaterial(String materialId, String identityOrNull);

    CommentResponse editComment(Long id, String newContent, String identity);

    void deleteComment(Long id, String identity);

    void deleteCommentAsAdmin(Long id);

    CommentResponse reactToComment(Long commentId, ReactionType reactionType, String identity);

    void removeReaction(Long commentId, String identity);
}