package id.ac.ui.cs.advprog.yomu.discussion.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CommentReactionTest {

    @Test
    void onCreate_setsCreatedAt() {
        CommentReaction reaction = new CommentReaction();
        assertThat(reaction.getCreatedAt()).isNull();

        reaction.onCreate();

        assertThat(reaction.getCreatedAt()).isNotNull();
    }
}