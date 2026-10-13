package com.example.herokuapp;

import java.time.Duration;
import java.util.logging.Level;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.logging.LogEntry;
import static org.junit.jupiter.api.Assertions.*;

/** Validates the browser console output of https://the-internet.herokuapp.com/javascript_error. */
class JavaScriptError extends HerokuAppTest {
    @Override protected String path() { return "/javascript_error"; }

    @Test void pageLogsSevereJavaScriptError() {
        LogEntry entry = ConsoleLogs.waitForEntry(driver,
                e -> e.getLevel().intValue() >= Level.SEVERE.intValue(), Duration.ofSeconds(20));
        assertTrue(entry.getMessage().contains("javascript_error"),
                "Error should originate from the page: " + entry.getMessage());
        assertTrue(entry.getMessage().contains("Cannot read properties of undefined"),
                "Unexpected error message: " + entry.getMessage());
    }

    @Test void errorIsReportedAfterLogsAreCleared() {
        ConsoleLogs.clear(driver);
        driver.navigate().refresh();
        LogEntry entry = ConsoleLogs.waitForEntry(driver,
                e -> e.getLevel() == Level.SEVERE, Duration.ofSeconds(20));
        assertNotNull(entry.getMessage());
    }

    @Test void pageWithoutScriptErrorHasNoConsoleErrors() {
        driver.get(BASE_URL + "/checkboxes");
        ConsoleLogs.clear(driver);
        driver.navigate().refresh();
        new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(60))
                .until(d -> "complete".equals(((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState"))
                        && !d.findElements(org.openqa.selenium.By.cssSelector("input[type='checkbox']"))
                                .isEmpty());
        ConsoleLogs.assertNoErrors(driver, ConsoleLogs::isResourceLoadFailure);
    }
}
