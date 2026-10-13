package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import static org.junit.jupiter.api.Assertions.*;

class JQueryUiMenusTest extends HerokuAppTest {
    @Override protected String path() { return "/jqueryui/menu"; }

    @Test void enabledMenuCanBeOpened() {
        assertTrue(driver.findElement(By.id("ui-id-3")).isDisplayed());
    }

    @Test void submenuCanNavigateToPdf() {
        var enabled = driver.findElement(By.id("ui-id-3"));
        new Actions(driver).moveToElement(enabled).perform();
        var downloads = wait.until(d -> d.findElement(By.id("ui-id-4")));
        new Actions(driver).moveToElement(downloads).perform();
        wait.until(org.openqa.selenium.support.ui.ExpectedConditions.visibilityOfElementLocated(By.id("ui-id-5"))).click();
        wait.until(d -> d.getCurrentUrl().contains("/pdf"));
        assertTrue(driver.getCurrentUrl().contains("pdf"));
    }
}
