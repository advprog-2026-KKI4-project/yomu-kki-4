package id.ac.ui.cs.advprog.yomu.learningandquiz.functional;

import id.ac.ui.cs.advprog.yomu.auth.dto.RegisterRequest;
import id.ac.ui.cs.advprog.yomu.auth.service.AuthService;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.Question;
import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LearningQuizFunctionalTest {

    @LocalServerPort
    private int port;

    @Autowired
    private AuthService authService;

    @Autowired
    private ReadingMaterialService materialService;

    private WebDriver driver;
    private WebDriverWait wait;
    private String baseUrl;

    private static final String TEST_USERNAME = "learntester";
    private static final String TEST_EMAIL = "learntester@yomu.test";
    private static final String TEST_PASSWORD = "password123";

    private String materialId;

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

        // Pre-seed a reading material with questions for quiz tests
        ReadingMaterial material = new ReadingMaterial();
        material.setTitle("Information Literacy Basics");
        material.setCategory("Literacy");
        material.setContent("This is a test reading material about information literacy fundamentals.");
        material.setTimeLimit(120);

        Question q1 = new Question();
        q1.setQuestionText("What is information literacy?");
        q1.setOptions(List.of("The ability to read", "The ability to find, evaluate, and use information", "The ability to type fast", "The ability to memorize facts"));
        q1.setCorrectOptionIndex(1);
        material.addQuestion(q1);

        Question q2 = new Question();
        q2.setQuestionText("Why is source evaluation important?");
        q2.setOptions(List.of("It is not important", "To avoid misinformation", "To save time shopping", "To impress friends"));
        q2.setCorrectOptionIndex(1);
        material.addQuestion(q2);

        ReadingMaterial saved = materialService.add(material);
        materialId = saved.getId();
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
    @DisplayName("Learning library page loads and shows materials")
    void learningLibraryPageLoads() {
        login();
        driver.get(baseUrl + "/reading");

        WebElement heading = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header h2")));
        assertThat(heading.getText()).contains("Learning Library");

        java.util.List<WebElement> cards = driver.findElements(By.cssSelector(".card"));
        assertThat(cards.size()).isGreaterThanOrEqualTo(1);

        // Verify our pre-seeded material appears
        java.util.List<WebElement> titles = driver.findElements(By.cssSelector(".material-title"));
        java.util.List<String> titleTexts = titles.stream().map(WebElement::getText).toList();
        assertThat(titleTexts).contains("Information Literacy Basics");
    }

    @Test
    @Order(2)
    @DisplayName("Reader page loads with material content and Start Quiz button")
    void readerPageLoads() {
        login();
        driver.get(baseUrl + "/reading/" + materialId);

        WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".reader-title")));
        assertThat(title.getText()).isEqualTo("Information Literacy Basics");

        WebElement content = driver.findElement(By.cssSelector(".reading-text"));
        assertThat(content.isDisplayed()).isTrue();

        WebElement startQuizBtn = driver.findElement(By.linkText("Start Quiz"));
        assertThat(startQuizBtn.isDisplayed()).isTrue();
    }

    @Test
    @Order(3)
    @DisplayName("Quiz session page renders with questions and submit button")
    void quizSessionPageLoads() {
        login();
        driver.get(baseUrl + "/quiz/" + materialId);

        WebElement quizTitle = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".quiz-title")));
        assertThat(quizTitle.getText()).contains("Information Literacy Basics");

        java.util.List<WebElement> questionBlocks = driver.findElements(By.cssSelector(".question-block"));
        assertThat(questionBlocks).isNotEmpty();

        java.util.List<WebElement> radioInputs = driver.findElements(By.cssSelector("input[type='radio']"));
        assertThat(radioInputs).isNotEmpty();

        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#submit-btn")));
    }
}
