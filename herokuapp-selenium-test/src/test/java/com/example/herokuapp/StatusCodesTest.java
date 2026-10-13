package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class StatusCodesTest extends HerokuAppTest {
    @Override protected String path() { return "/status_codes"; }

    @Test void statusCodeLinksAreListed() {
        for (String code : new String[]{"200", "301", "404", "500"}) {
            assertTrue(driver.findElement(By.linkText(code)).isDisplayed());
        }
    }

    @Test void successAndNotFoundResponsesRenderTheirStatusText() {
        driver.findElement(By.linkText("200")).click();
        assertTrue(driver.findElement(By.id("content")).getText().contains("200 status code"));
        driver.get(BASE_URL + "/status_codes/404");
        assertTrue(driver.findElement(By.id("content")).getText().contains("404 status code"));
    }
}
