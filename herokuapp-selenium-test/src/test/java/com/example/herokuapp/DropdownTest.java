package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.support.ui.Select;
import static org.junit.jupiter.api.Assertions.*;

class DropdownTest extends HerokuAppTest {
    @Override protected String path() { return "/dropdown"; }
    @Override protected PageLoadStrategy pageLoadStrategy() { return PageLoadStrategy.NONE; }
    @Override protected void waitForUseCase() {
        super.waitForUseCase();
        waitForDropdown();
    }

    private Select waitForDropdown() {
        return wait.until(d -> {
            var elements = d.findElements(By.id("dropdown"));
            if (elements.isEmpty() || !elements.get(0).isDisplayed()) {
                return null;
            }
            Select dropdown = new Select(elements.get(0));
            return dropdown.getOptions().size() >= 3 ? dropdown : null;
        });
    }

    @Test void dropdownHasPlaceholderAndTwoOptions() {
        Select dropdown = waitForDropdown();
        assertEquals(3, dropdown.getOptions().size());
        assertEquals("Please select an option", dropdown.getFirstSelectedOption().getText());
    }

    @Test void bothOptionsCanBeSelected() {
        Select dropdown = waitForDropdown();
        for (String value : new String[]{"1", "2"}) {
            dropdown.selectByValue(value);
            assertEquals(value, dropdown.getFirstSelectedOption().getAttribute("value"));
        }
    }
}
