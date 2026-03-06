package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import java.util.List;

public interface DiscussionForumService {

    DiscussionForum postComment(CommentRequest requestDTO);
    List<DiscussionForum> getCommentsByMaterial(String materialId);
}