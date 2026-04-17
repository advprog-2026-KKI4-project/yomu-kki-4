package id.ac.ui.cs.advprog.yomu.discussion.service;

import java.util.List;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;

public interface DiscussionForumService {

    DiscussionForum postComment(CommentRequest requestDTO);
    List<DiscussionForum> getCommentsByMaterial(String materialId);

    DiscussionForum editComment(Long id, String newContent, String authorId);
    void deleteComment(Long id, String authorId);
}