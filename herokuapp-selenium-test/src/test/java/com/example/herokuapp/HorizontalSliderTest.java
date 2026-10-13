package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static org.junit.jupiter.api.Assertions.*;

class HorizontalSliderTest extends HerokuAppTest {
    @Override protected String path() { return "/horizontal_slider"; }

    @Test void sliderAndValueLabelArePresent() {
        assertTrue(driver.findElement(By.cssSelector("input[type='range']")).isDisplayed());
        assertTrue(driver.findElement(By.id("range")).isDisplayed());
    }

    @Test void keyboardChangesSliderValue() {
        var slider = driver.findElement(By.cssSelector("input[type='range']"));
        slider.click();
        slider.sendKeys(Keys.ARROW_RIGHT);
        assertNotEquals("0", driver.findElement(By.id("range")).getText());
    }
}
