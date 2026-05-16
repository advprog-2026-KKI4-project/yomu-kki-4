package id.ac.ui.cs.advprog.yomu.discussion.dto;

import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ReactionRequest {

    @NotNull(message = "Reaction type is required")
    private ReactionType reactionType;
}