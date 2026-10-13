package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class ChallengingDomTest extends HerokuAppTest {
    @Override protected String path() { return "/challenging_dom"; }

    @Test void tableHasRowsAndColumns() {
        assertTrue(driver.findElements(By.cssSelector("table tbody tr")).size() >= 1);
        assertEquals(7, driver.findElements(By.cssSelector("table thead th")).size());
    }

    @Test void actionButtonsAreAvailable() {
        assertEquals(3, driver.findElements(By.cssSelector("a.button")).size());
        assertTrue(driver.findElement(By.id("canvas")).isDisplayed());
    }
}
