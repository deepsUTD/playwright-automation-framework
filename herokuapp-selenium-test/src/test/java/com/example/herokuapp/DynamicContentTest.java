package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class DynamicContentTest extends HerokuAppTest {
    @Override protected String path() { return "/dynamic_content"; }

    @Test void dynamicContentRowsAreRendered() {
        assertEquals(3, driver.findElements(By.cssSelector(".row .large-10")).size());
    }

    @Test void contentCanBeRefreshedWithoutLosingPageStructure() {
        int before = driver.findElements(By.cssSelector(".row .large-10")).size();
        driver.navigate().refresh();
        wait.until(d -> d.findElements(By.cssSelector(".row .large-10")).size() == 3);
        assertEquals(before, driver.findElements(By.cssSelector(".row .large-10")).size());
    }
}
