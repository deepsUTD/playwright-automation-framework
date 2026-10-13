package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class ShiftingContentTest extends HerokuAppTest {
    @Override protected String path() { return "/shifting_content"; }

    @Test void shiftingContentExamplesAreListed() {
        assertTrue(driver.findElements(By.cssSelector("#content ul li a")).size() >= 3);
    }

    @Test void imageShiftExampleRendersContent() {
        driver.findElement(By.linkText("Example 2: An image")).click();
        assertTrue(driver.findElement(By.cssSelector("#content img")).isDisplayed());
        assertFalse(driver.findElement(By.cssSelector("#content")).getText().isBlank());
    }
}
