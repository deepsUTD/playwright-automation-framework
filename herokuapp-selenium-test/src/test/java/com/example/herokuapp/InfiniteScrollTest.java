package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class InfiniteScrollTest extends HerokuAppTest {
    @Override protected String path() { return "/infinite_scroll"; }

    @Test void initialPageHasContent() {
        assertTrue(driver.findElement(By.cssSelector(".scroll")).isDisplayed());
        assertEquals("Infinite Scroll", driver.findElement(By.tagName("h3")).getText());
        assertTrue(driver.findElement(By.cssSelector(".scroll a[href*='/infinite_scroll/']")).isDisplayed());
    }

    @Test void scrollingLoadsAdditionalContent() {
        By scrollContainer = By.cssSelector(".scroll");
        By nextPageLink = By.cssSelector(".scroll a[href*='/infinite_scroll/']");
        String initialContent = driver.findElement(scrollContainer).getText();
        String initialPageUrl = driver.findElement(nextPageLink).getAttribute("href");
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
        wait.until(d -> {
            String content = d.findElement(scrollContainer).getText();
            String nextUrl = d.findElement(nextPageLink).getAttribute("href");
            return content.length() > initialContent.length() && !initialPageUrl.equals(nextUrl);
        });
        assertTrue(driver.findElement(scrollContainer).getText().length() > initialContent.length());
        assertNotEquals(initialPageUrl, driver.findElement(nextPageLink).getAttribute("href"));
    }
}
