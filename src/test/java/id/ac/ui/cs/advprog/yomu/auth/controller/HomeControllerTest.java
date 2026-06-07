package id.ac.ui.cs.advprog.yomu.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class HomeControllerTest {

    @Autowired
    private HomeController controller;

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    void landingPage_shouldReturnIndexView() {
        assertEquals("index", controller.landingPage());
    }
}