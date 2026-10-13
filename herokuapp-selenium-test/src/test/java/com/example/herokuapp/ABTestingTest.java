package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ABTestingTest extends HerokuAppTest {
    @Override protected String path() { return "/abtest"; }

    @Test void pageShowsExperimentHeading() {
        assertTrue(driver.findElement(org.openqa.selenium.By.tagName("h3")).getText().contains("A/B Test"));
    }

    @Test void pageRendersExperimentContent() {
        assertFalse(driver.findElement(org.openqa.selenium.By.tagName("body")).getText().isBlank());
    }
}
