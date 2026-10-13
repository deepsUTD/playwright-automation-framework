package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static org.junit.jupiter.api.Assertions.*;

class KeyPressesTest extends HerokuAppTest {
    @Override protected String path() { return "/key_presses"; }

    @Test void typingLettersUpdatesKeyResult() {
        driver.findElement(By.id("target")).sendKeys("abc");
        assertTrue(driver.findElement(By.id("result")).getText().contains("C"));
    }

    @Test void specialKeysAreReported() {
        driver.findElement(By.id("target")).sendKeys(Keys.ESCAPE);
        assertTrue(driver.findElement(By.id("result")).getText().contains("ESC"));
    }
}
