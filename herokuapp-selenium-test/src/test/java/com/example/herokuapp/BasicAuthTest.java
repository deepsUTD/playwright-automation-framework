package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class BasicAuthTest extends HerokuAppTest {
    @Override protected String path() { return "/basic_auth"; }
    @Override protected boolean requiresBasicAuth() { return true; }

    @Test void validCredentialsShowSuccessMessage() {
        assertTrue(driver.findElement(By.tagName("p")).getText().contains("Congratulations"));
    }

    @Test void authenticatedPageHasExpectedHeading() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Basic Auth"));
    }
}
