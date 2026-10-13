package com.example.herokuapp;

import java.time.Duration;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Reads and validates browser console logs. Requires the driver to be created with
 * the "goog:loggingPrefs" capability (enabled in {@link HerokuAppTest}).
 * Reading the log buffer clears it, so each read returns only entries since the previous read.
 */
public final class ConsoleLogs {
    private ConsoleLogs() {}

    public static List<LogEntry> read(WebDriver driver) {
        return driver.manage().logs().get(LogType.BROWSER).getAll();
    }

    public static void clear(WebDriver driver) {
        read(driver);
    }

    public static List<LogEntry> readAtLevel(WebDriver driver, Level minimum) {
        return read(driver).stream()
                .filter(entry -> entry.getLevel().intValue() >= minimum.intValue())
                .toList();
    }

    /** Waits until a console entry matching the predicate appears and returns it. */
    public static LogEntry waitForEntry(WebDriver driver, Predicate<LogEntry> matcher, Duration timeout) {
        LogEntry[] found = new LogEntry[1];
        new WebDriverWait(driver, timeout)
                .withMessage("No matching console log entry appeared")
                .until(d -> {
                    found[0] = read(d).stream().filter(matcher).findFirst().orElse(null);
                    return found[0] != null;
                });
        return found[0];
    }

    public static void assertNoErrors(WebDriver driver) {
        assertNoErrors(driver, entry -> false);
    }

    /** Fails on any SEVERE entry except those matched by {@code ignore}. */
    public static void assertNoErrors(WebDriver driver, Predicate<LogEntry> ignore) {
        List<LogEntry> errors = readAtLevel(driver, Level.SEVERE).stream()
                .filter(ignore.negate())
                .toList();
        assertTrue(errors.isEmpty(), "Unexpected console errors: " + errors);
    }

    /** Matches network failures such as "Failed to load resource: ... 503". */
    public static boolean isResourceLoadFailure(LogEntry entry) {
        return entry.getMessage().contains("Failed to load resource");
    }
}
