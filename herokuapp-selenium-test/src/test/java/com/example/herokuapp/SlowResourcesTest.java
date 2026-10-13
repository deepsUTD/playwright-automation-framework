package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class SlowResourcesTest extends HerokuAppTest {
    @Override protected String path() { return "/slow"; }

    @Test void slowResourcesPageRendersItsHeading() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Slow Resources"));
    }

    @Test void slowPageKeepsMainNavigationUsable() {
        assertTrue(driver.findElement(By.linkText("Elemental Selenium")).isDisplayed());
    }
}
