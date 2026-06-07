package id.ac.ui.cs.advprog.yomu.discussion.dto;

import id.ac.ui.cs.advprog.yomu.discussion.model.ReactionType;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ReactionRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_hasNoViolations() {
        ReactionRequest req = new ReactionRequest(ReactionType.LIKE);
        Set<ConstraintViolation<ReactionRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void nullReactionType_hasViolation() {
        ReactionRequest req = new ReactionRequest(null);
        Set<ConstraintViolation<ReactionRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Reaction type is required");
    }
}