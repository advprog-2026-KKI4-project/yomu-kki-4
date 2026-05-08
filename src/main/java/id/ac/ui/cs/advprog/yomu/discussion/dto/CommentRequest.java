package id.ac.ui.cs.advprog.yomu.discussion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotBlank(message = "Content must be filled")
    private String content;

    @NotBlank(message = "Material ID must be filled")
    private String materialId;

    private Long parentCommentId;
}