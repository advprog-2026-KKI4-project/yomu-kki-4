package id.ac.ui.cs.advprog.yomu.achievement.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("selenium")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AchievementSeleniumTest extends SeleniumTestBase {

    private static final String ADMIN_EMAIL = "selenium-ach-admin@test.com";
    private static final String STUDENT_EMAIL = "selenium-ach-student@test.com";
    private static final String PASSWORD = "Password123";

    @BeforeEach
    void setupUsers() {
        registerUser(ADMIN_EMAIL, "selAchAdmin", PASSWORD);
        makeAdmin(ADMIN_EMAIL);
        registerUser(STUDENT_EMAIL, "selAchStudent", PASSWORD);
    }

    @Test
    @Order(1)
    void unauthenticatedUserIsRedirectedToLogin() {
        driver.get(baseUrl + "/achievements/progress");
        wait(10).until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @Order(2)
    void progressPageLoadsForStudent() {
        loginAs(STUDENT_EMAIL, PASSWORD);
        driver.get(baseUrl + "/achievements/progress");
        wait(10).until(ExpectedConditions.titleContains("Progress"));
        assertThat(driver.getTitle()).isEqualTo("My Progress - Yomu");
    }

    @Test
    @Order(3)
    void listPageShowsNewAchievementButtonForAdmin() {
        loginAs(ADMIN_EMAIL, PASSWORD);
        driver.get(baseUrl + "/achievements");
        wait(10).until(ExpectedConditions.titleContains("Achievements"));
        WebElement newBtn = wait(10).until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("New Achievement")));
        assertThat(newBtn.isDisplayed()).isTrue();
    }

    @Test
    @Order(4)
    void adminCanCreateAchievement() {
        String uniqueName = "Selenium Badge " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        driver.get(baseUrl + "/achievements/create");
        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys(uniqueName);
        driver.findElement(By.id("description")).sendKeys("Awarded for quiz mastery");
        new Select(driver.findElement(By.id("type"))).selectByValue("QUIZ");
        driver.findElement(By.id("targetCount")).sendKeys("5");
        driver.findElement(By.id("points")).sendKeys("25");
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        wait(10).until(ExpectedConditions.urlContains("/achievements/progress"));
        assertThat(driver.getPageSource()).contains(uniqueName);
    }

    @Test
    @Order(5)
    void adminCanEditAchievement() {
        String originalName = "Edit Target " + UUID.randomUUID().toString().substring(0, 8);
        String updatedName  = "Edited Name " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        createAchievementViaUI(originalName, "READING", "3", "15");

        By editLink = By.xpath(
                "//span[@class='name-text' and text()='" + originalName + "']" +
                "/ancestor::tr//a[contains(@class,'btn-row-edit')]");
        wait(10).until(ExpectedConditions.elementToBeClickable(editLink)).click();

        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys(updatedName);
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        wait(10).until(ExpectedConditions.urlContains("/achievements/progress"));
        assertThat(driver.getPageSource()).contains(updatedName);
        assertThat(driver.getPageSource()).doesNotContain(originalName);
    }

    @Test
    @Order(6)
    void adminCanDeleteAchievement() {
        String uniqueName = "Delete Target " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        createAchievementViaUI(uniqueName, "LOGIN", "1", "5");

        By deleteBtn = By.xpath(
                "//span[@class='name-text' and text()='" + uniqueName + "']" +
                "/ancestor::tr//button[contains(@class,'btn-row-delete')]");
        wait(10).until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();

        Alert alert = wait(10).until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait(10).until(ExpectedConditions.urlContains("/achievements/progress"));
        assertThat(driver.getPageSource()).doesNotContain(uniqueName);
    }

    private void createAchievementViaUI(String name, String type, String target, String points) {
        driver.get(baseUrl + "/achievements/create");
        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("description")).sendKeys("Selenium test achievement");
        new Select(driver.findElement(By.id("type"))).selectByValue(type);
        driver.findElement(By.id("targetCount")).sendKeys(target);
        driver.findElement(By.id("points")).sendKeys(points);
        driver.findElement(By.cssSelector("button.btn-primary")).click();
        wait(10).until(ExpectedConditions.urlContains("/achievements/progress"));
    }
}
