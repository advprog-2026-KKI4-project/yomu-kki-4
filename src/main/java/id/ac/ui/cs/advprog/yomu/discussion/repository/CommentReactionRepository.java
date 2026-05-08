package id.ac.ui.cs.advprog.yomu.discussion.repository;

import id.ac.ui.cs.advprog.yomu.discussion.model.CommentReaction;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByCommentIdAndUserId(Long commentId, Long userId);

    List<CommentReaction> findByCommentId(Long commentId);

    List<CommentReaction> findByCommentIdIn(List<Long> commentIds);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByCommentId(Long commentId);

    long countByCommentIdAndReactionType(Long commentId, ReactionType reactionType);
}