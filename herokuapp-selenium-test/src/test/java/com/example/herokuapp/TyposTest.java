package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class TyposTest extends HerokuAppTest {
    @Override protected String path() { return "/typos"; }

    @Test void pageExplainsTheTypoExample() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Typos"));
    }

    @Test void expectedParagraphIsPresent() {
        assertTrue(driver.findElement(By.id("content")).getText().contains("Sometimes you'll see a typo"));
    }
}
