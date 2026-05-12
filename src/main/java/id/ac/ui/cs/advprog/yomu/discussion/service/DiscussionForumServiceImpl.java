package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.repository.DiscussionForumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussionForumServiceImpl implements DiscussionForumService {

    private final DiscussionForumRepository repository;

    @Autowired
    public DiscussionForumServiceImpl(DiscussionForumRepository repository) {
        this.repository = repository;
    }

    @Override
    public DiscussionForum postComment(CommentRequest requestDTO) {
        // Validate parent comment exists if parentCommentId is provided
        if (requestDTO.getParentCommentId() != null) {
            DiscussionForum parentComment = repository.findById(requestDTO.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Parent comment with ID " + requestDTO.getParentCommentId() + " does not exist"
                    ));
            // Verify the parent comment belongs to the same materialId
            if (!parentComment.getMaterialId().equals(requestDTO.getMaterialId())) {
                throw new IllegalArgumentException(
                        "Parent comment does not belong to the same material"
                );
            }
        }

        DiscussionForum comment = DiscussionForum.builder()
                .content(requestDTO.getContent())
                .materialId(requestDTO.getMaterialId())
                .authorId(requestDTO.getAuthorId())
                .parentCommentId(requestDTO.getParentCommentId())
                .build();

        return repository.save(comment);
    }

    @Override
    public List<DiscussionForum> getCommentsByMaterial(String materialId) {
        return repository.findByMaterialIdOrderByCreatedAtAsc(materialId);
    }

    @Override
    public DiscussionForum editComment(Long id, String newContent, String authorId) {
        DiscussionForum comment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        // Validate newContent before saving
        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("Comment content cannot be empty");
        }

        comment.setContent(newContent);
        return repository.save(comment);
    }

    @Override
    public void deleteComment(Long id, String authorId) {
        DiscussionForum comment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        repository.delete(comment);
    }

}
