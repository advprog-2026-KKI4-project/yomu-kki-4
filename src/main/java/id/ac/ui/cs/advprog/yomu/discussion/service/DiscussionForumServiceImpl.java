package id.ac.ui.cs.advprog.yomu.discussion.service;

import id.ac.ui.cs.advprog.yomu.auth.model.User;
import id.ac.ui.cs.advprog.yomu.auth.repository.UserRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscussionForumServiceImpl implements DiscussionForumService {

    private final DiscussionForumRepository commentRepository;
    private final CommentReactionRepository reactionRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse postComment(CommentRequest requestDTO, String identity) {
        User author = resolveUser(identity);

        if (requestDTO.getParentCommentId() != null
                && !commentRepository.existsById(requestDTO.getParentCommentId())) {
            throw new IllegalArgumentException(
                    "Parent comment with ID " + requestDTO.getParentCommentId() + " does not exist"
            );
        }

        DiscussionForum comment = DiscussionForum.builder()
                .content(requestDTO.getContent())
                .materialId(requestDTO.getMaterialId())
                .authorId(author.getId())
                .parentCommentId(requestDTO.getParentCommentId())
                .build();

        DiscussionForum saved = commentRepository.save(comment);
        return toResponse(saved, author.getUsername(), Collections.emptyList(), author.getId());
    }

    @Override
    public List<CommentResponse> getCommentsByMaterial(String materialId, String identityOrNull) {
        List<DiscussionForum> comments = commentRepository.findByMaterialIdOrderByCreatedAtAsc(materialId);
        if (comments.isEmpty()) return Collections.emptyList();

        // Bulk-load authors
        Set<Long> authorIds = comments.stream()
                .map(DiscussionForum::getAuthorId)
                .collect(Collectors.toSet());
        Map<Long, String> usernamesById = userRepository.findAllById(authorIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));

        // Bulk-load reactions
        List<Long> commentIds = comments.stream().map(DiscussionForum::getId).toList();
        Map<Long, List<CommentReaction>> reactionsByComment = reactionRepository
                .findByCommentIdIn(commentIds).stream()
                .collect(Collectors.groupingBy(CommentReaction::getCommentId));

        Long currentUserId = identityOrNull == null ? null : tryResolveUserId(identityOrNull);

        return comments.stream()
                .map(c -> toResponse(
                        c,
                        usernamesById.getOrDefault(c.getAuthorId(), "[deleted user]"),
                        reactionsByComment.getOrDefault(c.getId(), Collections.emptyList()),
                        currentUserId))
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse editComment(Long id, String newContent, String identity) {
        User user = resolveUser(identity);
        DiscussionForum comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        comment.setContent(newContent);
        DiscussionForum saved = commentRepository.save(comment);

        List<CommentReaction> reactions = reactionRepository.findByCommentId(saved.getId());
        return toResponse(saved, user.getUsername(), reactions, user.getId());
    }

    @Override
    @Transactional
    public void deleteComment(Long id, String identity) {
        User user = resolveUser(identity);
        DiscussionForum comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }

        reactionRepository.deleteByCommentId(id);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void deleteCommentAsAdmin(Long id) {
        DiscussionForum comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        reactionRepository.deleteByCommentId(id);
        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public CommentResponse reactToComment(Long commentId, ReactionType reactionType, String identity) {
        User user = resolveUser(identity);
        DiscussionForum comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentReaction> existing =
                reactionRepository.findByCommentIdAndUserId(commentId, user.getId());

        if (existing.isPresent()) {
            CommentReaction r = existing.get();
            if (r.getReactionType() == reactionType) {
                // same reaction = toggle off
                reactionRepository.delete(r);
            } else {
                r.setReactionType(reactionType);
                reactionRepository.save(r);
            }
        } else {
            reactionRepository.save(CommentReaction.builder()
                    .commentId(commentId)
                    .userId(user.getId())
                    .reactionType(reactionType)
                    .build());
        }

        String authorUsername = userRepository.findById(comment.getAuthorId())
                .map(User::getUsername).orElse("[deleted user]");
        List<CommentReaction> reactions = reactionRepository.findByCommentId(commentId);
        return toResponse(comment, authorUsername, reactions, user.getId());
    }

    @Override
    @Transactional
    public void removeReaction(Long commentId, String identity) {
        User user = resolveUser(identity);
        reactionRepository.deleteByCommentIdAndUserId(commentId, user.getId());
    }

    // -------- helpers --------

    private User resolveUser(String identity) {
        if (identity == null || identity.isBlank()) {
            throw new IllegalArgumentException("Unauthorized");
        }
        if (identity.contains("@")) {
            return userRepository.findByEmail(identity)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
        }
        return userRepository.findByPhone(identity)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Long tryResolveUserId(String identity) {
        try {
            return resolveUser(identity).getId();
        } catch (Exception e) {
            return null;
        }
    }

    private CommentResponse toResponse(DiscussionForum c, String authorUsername,
                                       List<CommentReaction> reactions, Long currentUserId) {
        Map<ReactionType, Long> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType type : ReactionType.values()) counts.put(type, 0L);
        ReactionType currentUserReaction = null;
        for (CommentReaction r : reactions) {
            counts.merge(r.getReactionType(), 1L, Long::sum);
            if (currentUserId != null && r.getUserId().equals(currentUserId)) {
                currentUserReaction = r.getReactionType();
            }
        }

        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .materialId(c.getMaterialId())
                .authorId(c.getAuthorId())
                .authorUsername(authorUsername)
                .parentCommentId(c.getParentCommentId())
                .createdAt(c.getCreatedAt())
                .reactionCounts(counts)
                .currentUserReaction(currentUserReaction)
                .build();
    }
}