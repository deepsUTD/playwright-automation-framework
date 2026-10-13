package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class SecureFileDownloadTest extends HerokuAppTest {
    @Override protected String path() { return "/download_secure"; }
    @Override protected boolean requiresBasicAuth() { return true; }

    @Test void secureDownloadPageListsFiles() {
        assertTrue(driver.getPageSource().contains("Secure File Downloader"));
        assertTrue(driver.findElements(By.cssSelector("#content a")).size() > 0);
    }

    @Test void secureDownloadLinksHaveFileNames() {
        var links = driver.findElements(By.cssSelector("#content a"));
        assertTrue(links.stream().allMatch(link -> !link.getText().isBlank()));
    }
}
