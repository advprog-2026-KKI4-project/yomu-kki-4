package id.ac.ui.cs.advprog.yomu.discussion.dto;

import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private String materialId;
    private Long authorId;
    private String authorUsername;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private Map<ReactionType, Long> reactionCounts;
    private ReactionType currentUserReaction; // null if not logged in or no reaction
}