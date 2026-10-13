package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import static org.junit.jupiter.api.Assertions.*;

class FramesTest extends HerokuAppTest {
    @Override protected String path() { return "/frames"; }

    @Override
    protected void waitForUseCase() {
        wait.until(d -> d.getCurrentUrl() != null
                && d.getCurrentUrl().contains("/frames")
                && !d.findElements(By.cssSelector("a[href='/nested_frames']")).isEmpty()
                && !d.findElements(By.cssSelector("a[href='/iframe']")).isEmpty());
    }

    @Test void framesPageLinksToBothFrameExamples() {
        wait.until(d -> d.getCurrentUrl() != null && d.getCurrentUrl().contains("/frames"));
        var nestedFrames = driver.findElements(By.cssSelector("a[href='/nested_frames']"));
        var iframeLinks = driver.findElements(By.cssSelector("a[href='/iframe']"));

        assertFalse(nestedFrames.isEmpty(), "Nested Frames link should be present on the frames page");
        assertFalse(iframeLinks.isEmpty(), "iFrame link should be present on the frames page");
        assertTrue(nestedFrames.getFirst().isDisplayed());
        assertTrue(iframeLinks.getFirst().isDisplayed());
    }

    @Test void iframeEditorCanBeReadAndEdited() {
        wait.until(d -> d.getCurrentUrl() != null && d.getCurrentUrl().contains("/frames"));

        var iframeLink = wait.until(d -> {
            var links = d.findElements(By.cssSelector("a[href='/iframe']"));
            return links.isEmpty() ? null : links.getFirst();
        });

        iframeLink.click();
        wait.until(d -> !d.findElements(By.id("mce_0_ifr")).isEmpty());

        driver.switchTo().frame("mce_0_ifr");
        wait.until(d -> !d.findElements(By.id("tinymce")).isEmpty());

        var body = driver.findElement(By.id("tinymce"));
        String initialText = (String) ((JavascriptExecutor) driver).executeScript(
                "return (arguments[0].innerText || arguments[0].textContent || '').trim();", body);
        assertFalse(initialText.isBlank(), "The iframe editor should not be empty");

        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].innerHTML = '<p>Selenium frame test</p>';", body);

        String editedText = (String) ((JavascriptExecutor) driver).executeScript(
                "return (arguments[0].innerText || arguments[0].textContent || '').trim();", body);

        assertEquals("Selenium frame test", editedText, "The iframe editor should contain the updated text");
        driver.switchTo().defaultContent();
    }
}
