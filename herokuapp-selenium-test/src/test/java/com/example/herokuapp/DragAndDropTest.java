package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import static org.junit.jupiter.api.Assertions.*;

class DragAndDropTest extends HerokuAppTest {
    @Override protected String path() { return "/drag_and_drop"; }

    @Test void bothDraggableColumnsArePresent() {
        assertEquals(2, driver.findElements(By.cssSelector("#columns > div")).size());
        assertEquals("A", driver.findElement(By.xpath("//*[@id=\"column-a\"]")).getText().trim());
        assertEquals("B", driver.findElement(By.id("column-b")).getText().trim());
    }

    @Test void draggingColumnAOverBChangesTheirLabels() {
        new Actions(driver).dragAndDrop(driver.findElement(By.xpath("//*[@id=\"column-a\"]")), driver.findElement(By.id("column-b"))).perform();
        wait.until(d -> d.findElement(By.xpath("//*[@id=\"column-a\"]")).getText().trim().equals("B"));
        assertEquals("B", driver.findElement(By.xpath("//*[@id=\"column-a\"]")).getText().trim());
        assertEquals("A", driver.findElement(By.id("column-b")).getText().trim());
    }
}
