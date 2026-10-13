package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class AddRemoveElementsTest extends HerokuAppTest {
    @Override protected String path() { return "/add_remove_elements/"; }

    @Test void addButtonCreatesDeleteButtons() {
        driver.findElement(By.cssSelector("button[onclick='addElement()']")).click();
        driver.findElement(By.cssSelector("button[onclick='addElement()']")).click();
        assertEquals(2, driver.findElements(By.className("added-manually")).size());
    }

    @Test void deleteButtonRemovesOnlyItsElement() {
        driver.findElement(By.cssSelector("button[onclick='addElement()']")).click();
        driver.findElement(By.className("added-manually")).click();
        assertTrue(driver.findElements(By.className("added-manually")).isEmpty());
    }
}
