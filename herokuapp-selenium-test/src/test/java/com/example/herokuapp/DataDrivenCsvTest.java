package com.example.herokuapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

/** Data-driven login tests for https://the-internet.herokuapp.com/login using CSV test data. */
class DataDrivenCsvTest extends HerokuAppTest {
    @Override protected String path() { return "/login"; }

    @Override protected void waitForUseCase() {
        super.waitForUseCase();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }

    private void login(String username, String password) {
        driver.findElement(By.id("username")).sendKeys(username);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        new WebDriverWait(driver, Duration.ofSeconds(45))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
    }

    @DisplayName("valid username and password combinations log in")
    @ParameterizedTest(name = "[{index}] {0} / {1}")
    @CsvFileSource(resources = "/valid-credentials.csv", numLinesToSkip = 1)
    void validCredentialsLogIn(String username, String password) {
        login(username, password);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("flash"), "You logged into a secure area!"));
        assertTrue(driver.getCurrentUrl().endsWith("/secure"));
    }

    @DisplayName("invalid username and password combinations show an error")
    @ParameterizedTest(name = "[{index}] {0} / {1}")
    @CsvFileSource(resources = "/invalid-credentials.csv", numLinesToSkip = 1)
    void invalidCredentialsShowError(String username, String password) {
        login(username, password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
        assertTrue(driver.findElement(By.id("flash")).getText().contains("is invalid"));
        assertTrue(driver.getCurrentUrl().endsWith("/login"));
    }
}
