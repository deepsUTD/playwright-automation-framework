package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class BrokenImagesTest extends HerokuAppTest {
    @Override protected String path() { return "/broken_images"; }

    @Test void pageContainsTheSampleImages() {
        assertEquals(3, driver.findElements(By.cssSelector("img")).size());
    }

    @Test void brokenImageExamplesAreDetectable() {
        Long broken = (Long) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return [...document.images].filter(image => !image.complete || image.naturalWidth === 0).length;");
        assertTrue(broken >= 1, "Expected at least one intentionally broken image");
    }
}
