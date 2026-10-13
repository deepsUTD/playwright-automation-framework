package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

class DynamicLoadingTest extends HerokuAppTest {
    @Override protected String path() { return "/dynamic_loading"; }

    @Test void exampleOneRevealsHiddenContent() {
        driver.findElement(By.linkText("Example 1: Element on page that is hidden")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#start button"))).click();
        assertEquals("Hello World!", wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish"))).getText());
    }

    @Test void exampleTwoLoadsContentAfterStart() {
        driver.get(BASE_URL + "/dynamic_loading/2");
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#start button"))).click();
        assertEquals("Hello World!", wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish"))).getText());
    }
}
