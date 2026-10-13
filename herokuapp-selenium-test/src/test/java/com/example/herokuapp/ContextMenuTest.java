package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.interactions.Actions;
import static org.junit.jupiter.api.Assertions.*;

class ContextMenuTest extends HerokuAppTest {
    @Override protected String path() { return "/context_menu"; }

    @Test void rightClickTargetShowsExpectedAlert() {
        new Actions(driver).contextClick(driver.findElement(By.id("hot-spot"))).perform();
        assertEquals("You selected a context menu", wait.until(d -> d.switchTo().alert()).getText());
        driver.switchTo().alert().accept();
    }

    @Test void targetIsVisibleAndHasInstruction() {
        assertTrue(driver.findElement(By.id("hot-spot")).isDisplayed());
        assertTrue(driver.findElement(By.tagName("h3")).getText().contains("Context Menu"));
    }
}
