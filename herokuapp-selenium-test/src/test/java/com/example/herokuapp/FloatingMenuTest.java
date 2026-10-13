package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class FloatingMenuTest extends HerokuAppTest {
    @Override protected String path() { return "/floating_menu"; }

    @Test void floatingMenuContainsNavigationLinks() {
        assertTrue(driver.findElements(By.cssSelector("#menu a")).size() >= 4);
    }

    @Test void menuRemainsVisibleAfterScrolling() {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        assertTrue(driver.findElement(By.id("menu")).isDisplayed());
    }
}
