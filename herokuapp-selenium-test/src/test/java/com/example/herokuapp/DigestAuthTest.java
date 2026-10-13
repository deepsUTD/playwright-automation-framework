package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class DigestAuthTest extends HerokuAppTest {
    @Override protected String path() { return "/digest_auth"; }
    @Override protected boolean requiresBasicAuth() { return true; }

    @Test void validDigestCredentialsShowSuccess() {
        assertTrue(driver.findElement(By.tagName("p")).getText().contains("Congratulations"));
    }

    @Test void authenticatedPageShowsDigestHeading() {
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Digest Auth"));
    }
}
