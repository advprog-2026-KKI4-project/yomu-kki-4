package id.ac.ui.cs.advprog.yomu.discussion.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DiscussionForumTest {

    @Test
    void onCreate_setsCreatedAt() {
        DiscussionForum forum = new DiscussionForum();
        assertThat(forum.getCreatedAt()).isNull();

        forum.onCreate();

        assertThat(forum.getCreatedAt()).isNotNull();
    }
}