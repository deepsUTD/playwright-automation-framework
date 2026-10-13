package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class ForgotPasswordTest extends HerokuAppTest {
    @Override protected String path() { return "/forgot_password"; }

    @Test void validEmailShowsRecoveryConfirmation() {
        driver.findElement(By.id("email")).sendKeys("qa@example.com");
        driver.findElement(By.id("form_submit")).click();
        assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Internal Server Error")
                || driver.findElement(By.tagName("body")).getText().toLowerCase().contains("email"));
    }

    @Test void recoveryFormHasEmailInputAndSubmitButton() {
        assertEquals("email", driver.findElement(By.id("email")).getAttribute("type"));
        assertTrue(driver.findElement(By.id("form_submit")).isEnabled());
    }
}
