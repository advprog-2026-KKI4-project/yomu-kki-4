package id.ac.ui.cs.advprog.yomu.discussion.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Comment {
    private String content;
    private String materialId;
    private String authorId;
    private Long parentCommentId;
}

