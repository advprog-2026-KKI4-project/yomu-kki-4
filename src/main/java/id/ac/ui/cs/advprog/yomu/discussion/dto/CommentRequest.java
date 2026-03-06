package id.ac.ui.cs.advprog.yomu.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Content must be filled")
    @Size(max = 1000, message = "Content must not exceed 1000 characters")
    private String content;

    @NotBlank(message = "Material ID must be filled")
    private String materialId;

    @NotBlank(message = "Author ID must be filled")
    private String authorId;
    private Long parentCommentId;
}