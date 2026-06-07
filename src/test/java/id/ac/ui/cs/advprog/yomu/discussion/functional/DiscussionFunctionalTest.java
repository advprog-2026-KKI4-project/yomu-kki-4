package id.ac.ui.cs.advprog.yomu.discussion.functional;

import id.ac.ui.cs.advprog.yomu.learningandquiz.model.ReadingMaterial;
import id.ac.ui.cs.advprog.yomu.learningandquiz.service.ReadingMaterialService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DiscussionFunctionalTest {

    @LocalServerPort
    private int port;

    @MockitoBean
    private ReadingMaterialService readingMaterialService;

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(
                "--headless=new",
                "--disable-gpu",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*",
                "--window-size=1280,800"
        );
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    private void loginAsAdmin() {
        driver.get("http://localhost:" + port + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("emailOrPhone")));
        driver.findElement(By.id("emailOrPhone")).sendKeys("admin@yomu.id");
        driver.findElement(By.id("password")).sendKeys("adminpassword");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/dashboard"));
    }

    @Test
    public void testSingleDiscussionRoomLoads() {
        loginAsAdmin();

        ReadingMaterial mockMaterial = new ReadingMaterial();
        mockMaterial.setId("mat-1");
        mockMaterial.setTitle("Java Basics");
        mockMaterial.setCategory("Programming");
        when(readingMaterialService.getById("mat-1")).thenReturn(mockMaterial);

        driver.get("http://localhost:" + port + "/discussion/mat-1");

        WebElement titleElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(), 'Java Basics')]")));
        assertTrue(titleElement.isDisplayed(),
                "Expected material title 'Java Basics' to be visible on discussion page");
        assertTrue(driver.getPageSource().contains("Java Basics"),
                "Expected page source to contain material title");
    }

    @Test
    public void testAnonymousUserCannotAccessDiscussion() {
        driver.get("http://localhost:" + port + "/discussion/mat-1");

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Expected anonymous user to be redirected to /login but was at: " + driver.getCurrentUrl());
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}