package id.ac.ui.cs.advprog.yomu.discussion.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class DiscussionAdminControllerTest {

    @Autowired
    private DiscussionAdminController controller;

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }
}