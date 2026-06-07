package id.ac.ui.cs.advprog.yomu.auth.functional;

import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    private WebDriver driver;
    private WebDriverWait wait;

    private String baseUrl;

    private static final String TEST_USERNAME = "seleniumuser";
    private static final String TEST_EMAIL = "selenium@yomu.test";
    private static final String TEST_PASSWORD = "password123";

    @BeforeAll
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        baseUrl = "http://localhost:" + port;
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Landing page loads and shows Log In button")
    void landingPageLoads() {
        driver.get(baseUrl + "/");

        WebElement loginLink = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("Log In")));
        assertThat(loginLink.isDisplayed()).isTrue();

        WebElement registerLink = driver.findElement(By.linkText("Register"));
        assertThat(registerLink.isDisplayed()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Login page renders form, inputs, and Google OAuth link")
    void loginPageRendersForm() {
        driver.get(baseUrl + "/login");

        WebElement emailInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("emailOrPhone")));
        assertThat(emailInput.isDisplayed()).isTrue();

        WebElement passwordInput = driver.findElement(By.id("password"));
        assertThat(passwordInput.isDisplayed()).isTrue();

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertThat(submitButton.isDisplayed()).isTrue();
        assertThat(submitButton.getText()).contains("Login");

        WebElement googleLink = driver.findElement(By.cssSelector("a[href*='oauth2/authorization/google']"));
        assertThat(googleLink.isDisplayed()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Register page renders form fields")
    void registerPageRendersForm() {
        driver.get(baseUrl + "/register");

        WebElement usernameInput = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.id("username")));
        assertThat(usernameInput.isDisplayed()).isTrue();

        assertThat(driver.findElement(By.id("email")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("phone")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.id("password")).isDisplayed()).isTrue();

        WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
        assertThat(submitButton.getText()).contains("Sign Up");
    }

    @Test
    @Order(4)
    @DisplayName("Valid login sets JWT cookie and redirects to dashboard")
    void validLoginRedirectsToDashboard() {
        // Pre-seed user via API
        RegisterRequest registerReq = new RegisterRequest();
        registerReq.setUsername(TEST_USERNAME);
        registerReq.setEmail(TEST_EMAIL);
        registerReq.setPassword(TEST_PASSWORD);
        authService.register(registerReq);

        driver.get(baseUrl + "/login");

        driver.findElement(By.id("emailOrPhone")).sendKeys(TEST_EMAIL);
        driver.findElement(By.id("password")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Wait for JS redirect to /dashboard
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // Verify JWT cookie was set
        Cookie jwtCookie = driver.manage().getCookieNamed("jwt");
        assertThat(jwtCookie).isNotNull();

        // Verify we're on the dashboard page
        WebElement usernameHeading = driver.findElement(By.cssSelector("h2"));
        assertThat(usernameHeading.getText()).contains(TEST_USERNAME);
    }
}