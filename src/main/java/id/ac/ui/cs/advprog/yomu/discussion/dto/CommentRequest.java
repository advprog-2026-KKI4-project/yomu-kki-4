package id.ac.ui.cs.advprog.yomu.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Content must be filled")
    private String content;

    @NotBlank(message = "Material ID must be filled")
    private String materialId;

    private Long parentCommentId;
}