package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class RedirectLinkTest extends HerokuAppTest {
    @Override protected String path() { return "/redirector"; }

    @Test void redirectLinkIsAvailable() {
        assertTrue(driver.findElement(By.id("redirect")).isDisplayed());
    }

    @Test void redirectReachesStatusCodesPage() {
        driver.findElement(By.id("redirect")).click();
        wait.until(d -> d.getCurrentUrl().endsWith("/status_codes"));
        assertTrue(driver.getCurrentUrl().endsWith("/status_codes"));
    }
}
