package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class WysiwygEditorTest extends HerokuAppTest {
    @Override protected String path() { return "/tinymce"; }

    @Test void editorFrameIsAvailable() {
        assertTrue(driver.findElement(By.id("mce_0_ifr")).isDisplayed());
    }

    @Test void editorAcceptsTextInput() {
        driver.switchTo().frame("mce_0_ifr");
        var editor = driver.findElement(By.id("tinymce"));
        editor.clear();
        editor.sendKeys("Herokuapp Selenium editor test");
        assertEquals("Herokuapp Selenium editor test", editor.getText());
        driver.switchTo().defaultContent();
    }
}
