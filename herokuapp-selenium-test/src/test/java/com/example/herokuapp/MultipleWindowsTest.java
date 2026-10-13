package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class MultipleWindowsTest extends HerokuAppTest {
    @Override protected String path() { return "/windows"; }

    @Test void linkOpensNewWindow() {
        String original = driver.getWindowHandle();
        driver.findElement(By.linkText("Click Here")).click();
        wait.until(d -> d.getWindowHandles().size() == 2);
        Set<String> handles = driver.getWindowHandles();
        handles.remove(original);
        driver.switchTo().window(handles.iterator().next());
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("New Window"));
    }

    @Test void newWindowCanBeClosedAndOriginalRestored() {
        String original = driver.getWindowHandle();
        driver.findElement(By.linkText("Click Here")).click();
        wait.until(d -> d.getWindowHandles().size() == 2);
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(original)) {
                driver.switchTo().window(handle);
                driver.close();
            }
        }
        driver.switchTo().window(original);
        assertEquals(1, driver.getWindowHandles().size());
        assertTrue(driver.getCurrentUrl().endsWith("/windows"));
    }
}
