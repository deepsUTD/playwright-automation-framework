package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class JavascriptErrorTest extends HerokuAppTest {
    @Override protected String path() { return "/javascript_error"; }

    @Test void pageStillRendersDespiteItsJavascriptError() {
        assertTrue(driver.getTitle().contains("The Internet"));
        assertTrue(driver.findElement(By.tagName("body")).getText().contains("JavaScript"));
    }

    @Test void errorDemonstrationPageHasHeading() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("JavaScript"));
    }
}
