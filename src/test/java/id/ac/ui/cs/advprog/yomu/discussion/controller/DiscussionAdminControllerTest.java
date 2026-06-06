package id.ac.ui.cs.advprog.yomu.discussion.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DiscussionAdminControllerTest {

    @Test
    void discussionIndex_redirectsToReading() {
        DiscussionAdminController controller = new DiscussionAdminController();
        String viewName = controller.discussionIndex();
        assertThat(viewName).isEqualTo("redirect:/reading");
    }
}