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
class DailyMissionSeleniumTest extends SeleniumTestBase {

    private static final String ADMIN_EMAIL = "selenium-mis-admin@test.com";
    private static final String STUDENT_EMAIL = "selenium-mis-student@test.com";
    private static final String PASSWORD = "Password123";

    @BeforeEach
    void setupUsers() {
        registerUser(ADMIN_EMAIL, "selMisAdmin", PASSWORD);
        makeAdmin(ADMIN_EMAIL);
        registerUser(STUDENT_EMAIL, "selMisStudent", PASSWORD);
    }

    @Test
    @Order(1)
    void unauthenticatedUserIsRedirectedToLogin() {
        driver.get(baseUrl + "/daily-missions");
        wait(10).until(ExpectedConditions.urlContains("/login"));
        assertThat(driver.getCurrentUrl()).contains("/login");
    }

    @Test
    @Order(2)
    void missionListPageLoadsForStudent() {
        loginAs(STUDENT_EMAIL, PASSWORD);
        driver.get(baseUrl + "/daily-missions");
        wait(10).until(ExpectedConditions.titleContains("Daily Missions"));
        assertThat(driver.getTitle()).isEqualTo("Daily Missions - Yomu");
    }

    @Test
    @Order(3)
    void listPageShowsNewMissionButtonForAdmin() {
        loginAs(ADMIN_EMAIL, PASSWORD);
        driver.get(baseUrl + "/daily-missions");
        wait(10).until(ExpectedConditions.titleContains("Daily Missions"));
        WebElement newBtn = wait(10).until(
                ExpectedConditions.presenceOfElementLocated(By.linkText("New Mission")));
        assertThat(newBtn.isDisplayed()).isTrue();
    }

    @Test
    @Order(4)
    void adminCanCreateMission() {
        String uniqueName = "Selenium Quest " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        driver.get(baseUrl + "/daily-missions/create");
        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys(uniqueName);
        driver.findElement(By.id("description")).sendKeys("Read one article today");
        new Select(driver.findElement(By.id("type"))).selectByValue("READING");
        driver.findElement(By.id("targetCount")).sendKeys("1");
        driver.findElement(By.id("rewardPoints")).sendKeys("10");
        driver.findElement(By.id("active")).click();
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        wait(10).until(ExpectedConditions.urlContains("/daily-missions"));
        assertThat(driver.getPageSource()).contains(uniqueName);
    }

    @Test
    @Order(5)
    void adminCanEditMission() {
        String originalName = "Edit Quest " + UUID.randomUUID().toString().substring(0, 8);
        String updatedName  = "Updated Quest " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        createMissionViaUI(originalName, "QUIZ", "2", "20");

        By editLink = By.xpath(
                "//span[@class='name-text' and text()='" + originalName + "']" +
                "/ancestor::tr//a[contains(@class,'btn-row-edit')]");
        wait(10).until(ExpectedConditions.elementToBeClickable(editLink)).click();

        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys(updatedName);
        driver.findElement(By.cssSelector("button.btn-primary")).click();

        wait(10).until(ExpectedConditions.urlContains("/daily-missions"));
        assertThat(driver.getPageSource()).contains(updatedName);
        assertThat(driver.getPageSource()).doesNotContain(originalName);
    }

    @Test
    @Order(6)
    void adminCanDeleteMission() {
        String uniqueName = "Delete Quest " + UUID.randomUUID().toString().substring(0, 8);
        loginAs(ADMIN_EMAIL, PASSWORD);

        createMissionViaUI(uniqueName, "LOGIN", "1", "5");

        By deleteBtn = By.xpath(
                "//span[@class='name-text' and text()='" + uniqueName + "']" +
                "/ancestor::tr//button[contains(@class,'btn-row-delete')]");
        wait(10).until(ExpectedConditions.elementToBeClickable(deleteBtn)).click();

        Alert alert = wait(10).until(ExpectedConditions.alertIsPresent());
        alert.accept();

        wait(10).until(ExpectedConditions.urlContains("/daily-missions"));
        assertThat(driver.getPageSource()).doesNotContain(uniqueName);
    }

    private void createMissionViaUI(String name, String type, String target, String reward) {
        driver.get(baseUrl + "/daily-missions/create");
        wait(10).until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
        driver.findElement(By.id("name")).sendKeys(name);
        driver.findElement(By.id("description")).sendKeys("Selenium test mission");
        new Select(driver.findElement(By.id("type"))).selectByValue(type);
        driver.findElement(By.id("targetCount")).sendKeys(target);
        driver.findElement(By.id("rewardPoints")).sendKeys(reward);
        driver.findElement(By.cssSelector("button.btn-primary")).click();
        wait(10).until(ExpectedConditions.urlContains("/daily-missions"));
    }
}
