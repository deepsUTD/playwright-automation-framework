package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class EntryAdTest extends HerokuAppTest {
    @Override protected String path() { return "/entry_ad"; }

    @Test void entryAdPageLoads() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Entry Ad"));
    }

    @Test void modalCanBeClosedWhenDisplayed() {
        var modal = driver.findElements(By.id("modal")).stream().filter(org.openqa.selenium.WebElement::isDisplayed).findFirst();
        if (modal.isPresent()) {
            driver.findElement(By.cssSelector("#modal .modal-footer p")).click();
            wait.until(d -> !d.findElement(By.id("modal")).isDisplayed());
        }
        assertTrue(driver.findElement(By.tagName("h3")).isDisplayed());
    }
}
