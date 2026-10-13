package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class FileDownloadTest extends HerokuAppTest {
    @Override protected String path() { return "/download"; }

    @Test void downloadableFilesAreListed() {
        assertTrue(driver.findElements(By.cssSelector("#content a")).size() > 0);
    }

    @Test void downloadLinkHasAFileTarget() {
        var link = driver.findElement(By.cssSelector("#content a"));
        assertFalse(link.getText().isBlank());
        assertTrue(link.getAttribute("href").matches(".*/download/[^/]+$"));
    }
}
