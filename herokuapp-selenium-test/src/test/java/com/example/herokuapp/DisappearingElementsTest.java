package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class DisappearingElementsTest extends HerokuAppTest {
    @Override protected String path() { return "/disappearing_elements"; }

    @Test void stableNavigationLinksArePresent() {
        assertTrue(driver.findElements(By.linkText("Home")).size() >= 1);
        assertTrue(driver.findElements(By.linkText("About")).size() >= 1);
        assertTrue(driver.findElements(By.linkText("Contact Us")).size() >= 1);
    }

    @Test void navigationRendersAtLeastFourItems() {
        assertTrue(driver.findElements(By.cssSelector("ul li a")).size() >= 4);
    }
}
