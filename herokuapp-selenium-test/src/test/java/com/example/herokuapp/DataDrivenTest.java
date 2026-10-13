package com.example.herokuapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.json.Json;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/** Data-driven login tests for https://the-internet.herokuapp.com/login. */
class DataDrivenTest extends HerokuAppTest {
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
    @MethodSource("validCredentials")
    void validCredentialsLogIn(String username, String password) {
        login(username, password);
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("flash"), "You logged into a secure area!"));
        assertTrue(driver.getCurrentUrl().endsWith("/secure"));
    }

    @DisplayName("invalid username and password combinations show an error")
    @ParameterizedTest(name = "[{index}] {0} / {1}")
    @MethodSource("invalidCredentials")
    void invalidCredentialsShowError(String username, String password) {
        login(username, password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("flash")));
        assertTrue(driver.findElement(By.id("flash")).getText().contains("is invalid"));
        assertTrue(driver.getCurrentUrl().endsWith("/login"));
    }

    static Stream<Arguments> validCredentials() throws IOException {
        return credentials("valid");
    }

    static Stream<Arguments> invalidCredentials() throws IOException {
        return credentials("invalid");
    }

    @SuppressWarnings("unchecked")
    private static Stream<Arguments> credentials(String group) throws IOException {
        try (InputStream in = DataDrivenTest.class.getResourceAsStream("/credentials.json")) {
            assertNotNull(in, "credentials.json not found on the test classpath");
            String json = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            Map<String, Object> root = new Json().toType(json, Json.MAP_TYPE);
            List<Map<String, Object>> rows = (List<Map<String, Object>>) root.get(group);
            return rows.stream().map(row -> Arguments.of(row.get("username"), row.get("password")));
        }
    }
}
