package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentRequest;
import id.ac.ui.cs.advprog.yomu.discussion.dto.CommentResponse;
import id.ac.ui.cs.advprog.yomu.discussion.model.CommentReaction;
import id.ac.ui.cs.advprog.yomu.discussion.model.DiscussionForum;
import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import id.ac.ui.cs.advprog.yomu.discussion.repository.CommentReactionRepository;
import id.ac.ui.cs.advprog.yomu.discussion.repository.DiscussionForumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DiscussionForumServiceImpl implements DiscussionForumService {

    private final DiscussionForumRepository commentRepository;
    private final CommentReactionRepository reactionRepository;

    // TODO(auth): inject UserRepository here, then for each comment look up
    //             username from authorId (which will be a User.id)

    @Override
    @Transactional
    public CommentResponse postComment(CommentRequest req) {
        if (req.getParentCommentId() != null
                && !commentRepository.existsById(req.getParentCommentId())) {
            throw new IllegalArgumentException(
                    "Parent comment with ID " + req.getParentCommentId() + " does not exist");
        }

        DiscussionForum saved = commentRepository.save(DiscussionForum.builder()
                .content(req.getContent())
                .materialId(req.getMaterialId())
                .authorId(req.getAuthorId())
                .parentCommentId(req.getParentCommentId())
                .build());

        return toResponse(saved, Collections.emptyList(), req.getAuthorId());
    }

    @Override
    public List<CommentResponse> getCommentsByMaterial(String materialId, String currentUserId) {
        List<DiscussionForum> comments = commentRepository.findByMaterialIdOrderByCreatedAtAsc(materialId);
        if (comments.isEmpty()) return Collections.emptyList();

        List<Long> ids = comments.stream().map(DiscussionForum::getId).toList();
        Map<Long, List<CommentReaction>> reactionsByComment = new HashMap<>();
        for (CommentReaction r : reactionRepository.findByCommentIdIn(ids)) {
            reactionsByComment.computeIfAbsent(r.getCommentId(), k -> new ArrayList<>()).add(r);
        }

        return comments.stream()
                .map(c -> toResponse(c,
                        reactionsByComment.getOrDefault(c.getId(), Collections.emptyList()),
                        currentUserId))
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse editComment(Long id, String newContent, String authorId) {
        DiscussionForum c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!c.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }
        c.setContent(newContent);
        DiscussionForum saved = commentRepository.save(c);
        return toResponse(saved, reactionRepository.findByCommentId(id), authorId);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String authorId) {
        DiscussionForum c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!c.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }
        reactionRepository.deleteByCommentId(id);
        commentRepository.delete(c);
    }

    @Override
    @Transactional
    public void deleteCommentAsAdmin(Long id) {
        DiscussionForum c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        reactionRepository.deleteByCommentId(id);
        commentRepository.delete(c);
    }

    @Override
    @Transactional
    public CommentResponse reactToComment(Long commentId, ReactionType type, String userId) {
        DiscussionForum c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentReaction> existing =
                reactionRepository.findByCommentIdAndUserId(commentId, userId);

        if (existing.isPresent()) {
            CommentReaction r = existing.get();
            if (r.getReactionType() == type) {
                reactionRepository.delete(r); // toggle off
            } else {
                r.setReactionType(type);
                reactionRepository.save(r);
            }
        } else {
            reactionRepository.save(CommentReaction.builder()
                    .commentId(commentId).userId(userId).reactionType(type).build());
        }

        return toResponse(c, reactionRepository.findByCommentId(commentId), userId);
    }

    @Override
    @Transactional
    public void removeReaction(Long commentId, String userId) {
        reactionRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private CommentResponse toResponse(DiscussionForum c, List<CommentReaction> reactions,
                                       String currentUserId) {
        Map<ReactionType, Long> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType t : ReactionType.values()) counts.put(t, 0L);
        ReactionType mine = null;
        for (CommentReaction r : reactions) {
            counts.merge(r.getReactionType(), 1L, Long::sum);
            if (currentUserId != null && currentUserId.equals(r.getUserId())) {
                mine = r.getReactionType();
            }
        }

        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .materialId(c.getMaterialId())
                .authorId(c.getAuthorId())
                .authorUsername(c.getAuthorId()) // TODO(auth): replace with username from UserRepository
                .parentCommentId(c.getParentCommentId())
                .createdAt(c.getCreatedAt())
                .reactionCounts(counts)
                .currentUserReaction(mine)
                .build();
    }
}