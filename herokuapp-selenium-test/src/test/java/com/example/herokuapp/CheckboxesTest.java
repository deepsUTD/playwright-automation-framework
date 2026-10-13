package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CheckboxesTest extends HerokuAppTest {
    @Override protected String path() { return "/checkboxes"; }

    @Test void pageHasTwoIndependentCheckboxes() {
        assertEquals(2, driver.findElements(By.cssSelector("input[type='checkbox']")).size());
    }

    @Test void checkboxesToggleIndependently() {
        By checkboxSelector = By.cssSelector("input[type='checkbox']");
        List<WebElement> boxes = driver.findElements(checkboxSelector);
        if (boxes.get(1).isSelected()) {
            boxes.get(1).click();
            wait.until(d -> !d.findElements(checkboxSelector).get(1).isSelected());
        }
        assertFalse(boxes.get(1).isSelected());
        boxes.get(0).click();
        wait.until(d -> d.findElements(checkboxSelector).get(0).isSelected());
        assertTrue(boxes.get(0).isSelected());
        assertFalse(boxes.get(1).isSelected());
        boxes.get(0).click();
        assertFalse(boxes.get(0).isSelected());
    }
}
