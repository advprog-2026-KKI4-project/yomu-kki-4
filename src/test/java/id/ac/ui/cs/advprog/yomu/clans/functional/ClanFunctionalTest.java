package id.ac.ui.cs.advprog.yomu.clans.functional;

import id.ac.ui.cs.advprog.yomu.auth.dto.AuthResponse;
import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import id.ac.ui.cs.advprog.yomu.clans.service.ClanService;
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
class ClanFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @Autowired
    private ClanService clanService;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    private static final String TEST_USERNAME = "clantester";
    private static final String TEST_EMAIL = "clantester@yomu.test";
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

        RegisterRequest req = new RegisterRequest();
        req.setUsername(TEST_USERNAME);
        req.setEmail(TEST_EMAIL);
        req.setPassword(TEST_PASSWORD);
        authService.register(req);

        // Pre-seed a clan for discover page
        clanService.createClan("Test Clan", "A clan for testing", authService.register(
                createReq("leader", "leader@yomu.test")).getUserId());
    }

    private RegisterRequest createReq(String username, String email) {
        RegisterRequest r = new RegisterRequest();
        r.setUsername(username);
        r.setEmail(email);
        r.setPassword(TEST_PASSWORD);
        return r;
    }

    @AfterAll
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login() {
        driver.manage().deleteAllCookies();
        driver.get(baseUrl + "/login");
        WebElement emailInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("emailOrPhone")));
        emailInput.sendKeys(TEST_EMAIL);
        driver.findElement(By.id("password")).sendKeys(TEST_PASSWORD);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    @Order(1)
    @DisplayName("Discover page loads and shows clans")
    void discoverPageShowsClans() {
        login();
        driver.get(baseUrl + "/clans/discover");

        WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header h2")));
        assertThat(heading.getText()).contains("Discover Clans");

        WebElement clanRow = driver.findElement(By.cssSelector(".clan-row"));
        assertThat(clanRow.isDisplayed()).isTrue();
    }

    @Test
    @Order(2)
    @DisplayName("Create clan form renders")
    void createClanFormRenders() {
        login();
        driver.get(baseUrl + "/clans/create-form");

        WebElement heading = driver.findElement(By.cssSelector(".form-container h1"));
        assertThat(heading.getText()).contains("Create Clan");

        assertThat(driver.findElement(By.cssSelector("input[name='name']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.cssSelector("textarea[name='bio']")).isDisplayed()).isTrue();
        assertThat(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Leaderboard page loads")
    void leaderboardPageLoads() {
        login();
        driver.get(baseUrl + "/leaderboard");

        WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header h2")));
        assertThat(heading.getText()).contains("Leaderboard");

        WebElement row = driver.findElement(By.cssSelector(".leaderboard-row"));
        assertThat(row.isDisplayed()).isTrue();
    }
}