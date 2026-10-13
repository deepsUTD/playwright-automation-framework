package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import static org.junit.jupiter.api.Assertions.*;

class ExitIntentTest extends HerokuAppTest {
    @Override protected String path() { return "/exit_intent"; }

    @Test void exitIntentModalAppearsAtTopBoundary() {
        new Actions(driver).moveByOffset(400, 0).perform();
        var modal = driver.findElements(By.id("ouibounce-modal")).stream().filter(org.openqa.selenium.WebElement::isDisplayed).findFirst();
        assertTrue(modal.isPresent(), "Expected exit-intent modal");
    }

    @Test void exitIntentModalCanBeDismissed() {
        new Actions(driver).moveByOffset(400, 0).perform();
        var modal = wait.until(d -> d.findElements(By.id("ouibounce-modal")).stream()
                .filter(org.openqa.selenium.WebElement::isDisplayed).findFirst().orElse(null));
        modal.findElement(By.cssSelector(".modal-footer p")).click();
        wait.until(d -> !d.findElement(By.id("ouibounce-modal")).isDisplayed());
        assertFalse(driver.findElement(By.id("ouibounce-modal")).isDisplayed());
    }
}
