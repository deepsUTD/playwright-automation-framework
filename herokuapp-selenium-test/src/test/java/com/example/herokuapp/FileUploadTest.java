package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

class FileUploadTest extends HerokuAppTest {
    @Override protected String path() { return "/upload"; }

    @Test void uploadsAFileSuccessfully() throws Exception {
        var file = Files.createTempFile("herokuapp-upload-", ".txt");
        try {
            Files.writeString(file, "Selenium upload test");
            driver.findElement(By.id("file-upload")).sendKeys(file.toAbsolutePath().toString());
            driver.findElement(By.id("file-submit")).click();
            var resultWait = new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(60));
            var heading = resultWait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .visibilityOfElementLocated(By.tagName("h3")));
            assertTrue(heading.getText().contains("File Uploaded!"));
            var uploaded = resultWait.until(org.openqa.selenium.support.ui.ExpectedConditions
                    .visibilityOfElementLocated(By.id("uploaded-files")));
            assertTrue(uploaded.getText().contains(file.getFileName().toString()));
        } finally {
            Files.deleteIfExists(file);
        }
    }

    @Test void uploadFormExposesRequiredControls() {
        assertTrue(driver.findElement(By.id("file-upload")).isDisplayed());
        assertTrue(driver.findElement(By.id("file-submit")).isEnabled());
    }
}
