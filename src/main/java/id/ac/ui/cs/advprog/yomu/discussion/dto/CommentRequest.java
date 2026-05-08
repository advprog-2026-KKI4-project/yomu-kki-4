package id.ac.ui.cs.advprog.yomu.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Content must be filled")
    private String content;

    @NotBlank(message = "Material ID must be filled")
    private String materialId;

    // TODO(auth): remove this field. Resolve from Authentication.getName() in controller.
    @NotBlank(message = "Author ID must be filled")
    private String authorId;

    private Long parentCommentId;
}