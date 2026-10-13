package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class LargeDeepDomTest extends HerokuAppTest {
    @Override protected String path() { return "/large"; }

    @Test void largeTableContainsManyRowsAndColumns() {
        assertTrue(driver.findElements(By.cssSelector("#large-table tbody tr")).size() >= 50);
        assertTrue(driver.findElements(By.cssSelector("#large-table tbody tr:first-child td")).size() >= 10);
    }

    @Test void deeplyNestedTargetCanBeLocated() {
        var target = driver.findElement(By.id("sibling-50.3"));
        assertTrue(target.isDisplayed());
        assertFalse(target.getText().isBlank());
    }
}
