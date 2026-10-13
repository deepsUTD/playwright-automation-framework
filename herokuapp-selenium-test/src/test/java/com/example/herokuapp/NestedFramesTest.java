package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class NestedFramesTest extends HerokuAppTest {
    @Override protected String path() { return "/nested_frames"; }

    @Test void topFrameContainsLeftRightAndMiddleFrames() {
        driver.switchTo().frame("frame-top");
        for (String name : new String[]{"frame-left", "frame-middle", "frame-right"}) {
            driver.switchTo().frame(name);
            assertFalse(driver.findElement(By.tagName("body")).getText().isBlank());
            driver.switchTo().parentFrame();
        }
    }

    @Test void bottomFrameContainsItsText() {
        driver.switchTo().frame("frame-bottom");
        assertEquals("BOTTOM", driver.findElement(By.tagName("body")).getText().trim());
        driver.switchTo().defaultContent();
    }
}
