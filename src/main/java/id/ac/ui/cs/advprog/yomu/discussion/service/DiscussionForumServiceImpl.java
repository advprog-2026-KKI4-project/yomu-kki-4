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
import id.ac.ui.cs.advprog.yomu.service.ReadingMaterialService;
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
    private final ReadingMaterialService readingMaterialService;

    @Override
    @Transactional
    public CommentResponse postComment(CommentRequest req, Long authorId) {
        if (readingMaterialService.getById(req.getMaterialId()) == null) {
            throw new IllegalArgumentException(
                    "Material with ID " + req.getMaterialId() + " does not exist");
        }

        if (req.getParentCommentId() != null) {
            DiscussionForum parent = commentRepository.findById(req.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Parent comment with ID " + req.getParentCommentId() + " does not exist"));
            if (!Objects.equals(parent.getMaterialId(), req.getMaterialId())) {
                throw new IllegalArgumentException("Parent comment must belong to the same material");
            }
        }

        DiscussionForum saved = commentRepository.save(DiscussionForum.builder()
                .content(req.getContent())
                .materialId(req.getMaterialId())
                .authorId(authorId)
                .parentCommentId(req.getParentCommentId())
                .build());

        Map<Long, String> usernames = resolveUsernames(List.of(authorId));
        return toResponse(saved, Collections.emptyList(), authorId, usernames);
    }

    @Override
    public List<CommentResponse> getCommentsByMaterial(String materialId, Long currentUserId) {
        List<DiscussionForum> comments = commentRepository.findByMaterialIdOrderByCreatedAtAsc(materialId);
        if (comments.isEmpty()) return Collections.emptyList();

        List<Long> commentIds = comments.stream().map(DiscussionForum::getId).toList();
        Map<Long, List<CommentReaction>> reactionsByComment = new HashMap<>();
        for (CommentReaction r : reactionRepository.findByCommentIdIn(commentIds)) {
            reactionsByComment.computeIfAbsent(r.getCommentId(), k -> new ArrayList<>()).add(r);
        }

        List<Long> authorIds = comments.stream().map(DiscussionForum::getAuthorId).distinct().toList();
        Map<Long, String> usernames = resolveUsernames(authorIds);

        return comments.stream()
                .map(c -> toResponse(c,
                        reactionsByComment.getOrDefault(c.getId(), Collections.emptyList()),
                        currentUserId,
                        usernames))
                .toList();
    }

    @Override
    @Transactional
    public CommentResponse editComment(Long id, String newContent, Long authorId) {
        DiscussionForum c = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        if (!c.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }
        c.setContent(newContent);
        DiscussionForum saved = commentRepository.save(c);
        Map<Long, String> usernames = resolveUsernames(List.of(saved.getAuthorId()));
        return toResponse(saved, reactionRepository.findByCommentId(id), authorId, usernames);
    }

    @Override
    @Transactional
    public void deleteComment(Long id, Long authorId) {
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
    public CommentResponse reactToComment(Long commentId, ReactionType type, Long userId) {
        DiscussionForum c = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        Optional<CommentReaction> existing =
                reactionRepository.findByCommentIdAndUserId(commentId, userId);

        if (existing.isPresent()) {
            CommentReaction r = existing.get();
            if (r.getReactionType() == type) {
                reactionRepository.delete(r);
            } else {
                r.setReactionType(type);
                reactionRepository.save(r);
            }
        } else {
            reactionRepository.save(CommentReaction.builder()
                    .commentId(commentId).userId(userId).reactionType(type).build());
        }

        Map<Long, String> usernames = resolveUsernames(List.of(c.getAuthorId()));
        return toResponse(c, reactionRepository.findByCommentId(commentId), userId, usernames);
    }

    @Override
    @Transactional
    public void removeReaction(Long commentId, Long userId) {
        reactionRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    private Map<Long, String> resolveUsernames(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyMap();
        return userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(User::getId, User::getUsername));
    }

    private CommentResponse toResponse(DiscussionForum c, List<CommentReaction> reactions,
                                       Long currentUserId, Map<Long, String> usernames) {
        Map<ReactionType, Long> counts = new EnumMap<>(ReactionType.class);
        for (ReactionType t : ReactionType.values()) counts.put(t, 0L);
        ReactionType mine = null;
        for (CommentReaction r : reactions) {
            counts.merge(r.getReactionType(), 1L, Long::sum);
            if (currentUserId != null && currentUserId.equals(r.getUserId())) {
                mine = r.getReactionType();
            }
        }

        String username = usernames.getOrDefault(c.getAuthorId(), "Unknown user");

        return CommentResponse.builder()
                .id(c.getId())
                .content(c.getContent())
                .materialId(c.getMaterialId())
                .authorId(c.getAuthorId())
                .authorUsername(username)
                .parentCommentId(c.getParentCommentId())
                .createdAt(c.getCreatedAt())
                .reactionCounts(counts)
                .currentUserReaction(mine)
                .ownedByCurrentUser(currentUserId != null && currentUserId.equals(c.getAuthorId()))
                .build();
    }
}