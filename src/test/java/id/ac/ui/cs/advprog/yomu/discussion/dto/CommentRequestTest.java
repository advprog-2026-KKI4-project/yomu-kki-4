package id.ac.ui.cs.advprog.yomu.discussion.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CommentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRequest_hasNoViolations() {
        CommentRequest req = new CommentRequest("Hello", "mat-1", null);
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(req);
        assertThat(violations).isEmpty();
    }

    @Test
    void blankContent_hasViolation() {
        CommentRequest req = new CommentRequest("", "mat-1", null);
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Content must be filled");
    }

    @Test
    void blankMaterialId_hasViolation() {
        CommentRequest req = new CommentRequest("Hello", "", null);
        Set<ConstraintViolation<CommentRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Material ID must be filled");
    }
}