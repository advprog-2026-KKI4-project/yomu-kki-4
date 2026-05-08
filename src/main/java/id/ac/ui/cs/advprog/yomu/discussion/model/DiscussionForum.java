package id.ac.ui.cs.advprog.yomu.discussion.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "discussion_comments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DiscussionForum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "material_id", nullable = false)
    private String materialId;

    // TODO(auth): change type to Long and store User.id when auth is integrated
    @Column(name = "author_id", nullable = false)
    private String authorId;

    @Column(name = "parent_comment_id")
    private Long parentCommentId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}