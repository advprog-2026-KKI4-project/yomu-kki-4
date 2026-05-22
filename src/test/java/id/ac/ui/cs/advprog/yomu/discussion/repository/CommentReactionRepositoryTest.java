package id.ac.ui.cs.advprog.yomu.discussion.repository;

import id.ac.ui.cs.advprog.yomu.discussion.model.CommentReaction;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentReactionRepositoryTest {

    @Autowired private CommentReactionRepository reactionRepository;
    @Autowired private DiscussionForumRepository commentRepository;

    private Long commentId;

    @BeforeEach
    void setUp() {
        DiscussionForum comment = commentRepository.save(
                DiscussionForum.builder().content("test comment").materialId("mat-1").authorId(1L).build());
        commentId = comment.getId();
    }

    @Test
    void findByCommentIdAndUserId_exists() {
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(10L).reactionType(ReactionType.LIKE).build());
        Optional<CommentReaction> result = reactionRepository.findByCommentIdAndUserId(commentId, 10L);
        assertThat(result).isPresent();
        assertThat(result.get().getReactionType()).isEqualTo(ReactionType.LIKE);
    }

    @Test
    void findByCommentIdAndUserId_notFound() {
        assertThat(reactionRepository.findByCommentIdAndUserId(commentId, 999L)).isEmpty();
    }

    @Test
    void findByCommentId_returnsAll() {
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(1L).reactionType(ReactionType.LIKE).build());
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(2L).reactionType(ReactionType.UPVOTE).build());
        assertThat(reactionRepository.findByCommentId(commentId)).hasSize(2);
    }

    @Test
    void findByCommentIdIn_returnsReactions() {
        DiscussionForum c2 = commentRepository.save(DiscussionForum.builder().content("another").materialId("mat-1").authorId(2L).build());
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(1L).reactionType(ReactionType.LOVE).build());
        reactionRepository.save(CommentReaction.builder().commentId(c2.getId()).userId(1L).reactionType(ReactionType.WOW).build());
        assertThat(reactionRepository.findByCommentIdIn(List.of(commentId, c2.getId()))).hasSize(2);
    }

    @Test
    void deleteByCommentId_removesAll() {
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(1L).reactionType(ReactionType.LIKE).build());
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(2L).reactionType(ReactionType.SAD).build());
        reactionRepository.deleteByCommentId(commentId);
        assertThat(reactionRepository.findByCommentId(commentId)).isEmpty();
    }

    @Test
    void deleteByCommentIdAndUserId_removesOne() {
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(1L).reactionType(ReactionType.LIKE).build());
        reactionRepository.save(CommentReaction.builder().commentId(commentId).userId(2L).reactionType(ReactionType.ANGRY).build());
        reactionRepository.deleteByCommentIdAndUserId(commentId, 1L);
        assertThat(reactionRepository.findByCommentIdAndUserId(commentId, 1L)).isEmpty();
        assertThat(reactionRepository.findByCommentIdAndUserId(commentId, 2L)).isPresent();
    }
}
