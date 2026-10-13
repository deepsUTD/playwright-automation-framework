package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

class DynamicControlsTest extends HerokuAppTest {
    @Override protected String path() { return "/dynamic_controls"; }

    @Test void checkboxCanBeRemovedAndAddedBack() {
        driver.findElement(By.cssSelector("#checkbox-example button")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "It's gone!"));
        assertTrue(driver.findElements(By.cssSelector("#checkbox input")).isEmpty());
        driver.findElement(By.cssSelector("#checkbox-example button")).click();
        wait.until(d -> !d.findElements(By.cssSelector("#checkbox input")).isEmpty());
        assertTrue(driver.findElement(By.cssSelector("#checkbox input")).isDisplayed());
    }

    @Test void inputCanBeEnabledAndDisabled() {
        var input = driver.findElement(By.cssSelector("#input-example input"));
        assertFalse(input.isEnabled());
        driver.findElement(By.cssSelector("#input-example button")).click();
        wait.until(d -> d.findElement(By.cssSelector("#input-example input")).isEnabled());
        driver.findElement(By.cssSelector("#input-example button")).click();
        wait.until(d -> !d.findElement(By.cssSelector("#input-example input")).isEnabled());
        assertFalse(driver.findElement(By.cssSelector("#input-example input")).isEnabled());
    }
}
