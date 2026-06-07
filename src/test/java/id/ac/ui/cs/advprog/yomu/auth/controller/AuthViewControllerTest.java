package id.ac.ui.cs.advprog.yomu.auth.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AuthViewControllerTest {

    @Autowired
    private AuthViewController controller;

    @Test
    void contextLoads() {
        assertNotNull(controller);
    }

    @Test
    void loginPage_shouldReturnLoginView() {
        assertEquals("auth/login", controller.loginPage());
    }

    @Test
    void registerPage_shouldReturnRegisterView() {
        assertEquals("auth/register", controller.registerPage());
    }
}
