package com.example.herokuapp;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

class FormAuthenticationTest extends HerokuAppTest {
    @Override protected String path() { return "/login"; }
    @Override protected void waitForUseCase() {
        super.waitForUseCase();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("username")));
    }

    @Nested
    @DisplayName("Login feature")
    class LoginFeature {
        @Test
        @DisplayName("it logs in with valid credentials and can log out")
        void validCredentialsLogInAndCanLogOut() {
            driver.findElement(By.id("username")).sendKeys("tomsmith");
            driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.id("flash"), "You logged into a secure area!"));
            assertTrue(driver.getCurrentUrl().endsWith("/secure"));
            assertTrue(driver.findElement(By.id("flash")).getText().contains("You logged into a secure area!"));
            driver.findElement(By.cssSelector("a[href='/logout']")).click();
                wait.until(d -> d.getCurrentUrl().endsWith("/login"));
        }

        @Test
        @DisplayName("it shows an error for invalid credentials")
        void invalidCredentialsShowError() {
            driver.findElement(By.id("username")).sendKeys("invalid-user");
            driver.findElement(By.id("password")).sendKeys("wrong-password");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
                wait.until(ExpectedConditions.textToBePresentInElementLocated(
                    By.id("flash"), "Your username is invalid"));
            assertTrue(driver.findElement(By.id("flash")).getText().contains("Your username is invalid"));
        }
    }
}
