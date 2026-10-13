package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import static org.junit.jupiter.api.Assertions.*;

class InputsTest extends HerokuAppTest {
    @Override protected String path() { return "/inputs"; }

    @Test void numberInputAcceptsPositiveAndNegativeValues() {
        var input = driver.findElement(By.tagName("input"));
        input.sendKeys("42");
        assertEquals("42", input.getAttribute("value"));
        input.clear();
        input.sendKeys("-7");
        assertEquals("-7", input.getAttribute("value"));
    }

    @Test void numberInputSupportsIncrementAndDecrement() {
        var input = driver.findElement(By.tagName("input"));
        input.sendKeys("5");
        input.sendKeys(Keys.ARROW_UP);
        assertEquals("6", input.getAttribute("value"));
        input.sendKeys(Keys.ARROW_DOWN);
        assertEquals("5", input.getAttribute("value"));
    }
}
