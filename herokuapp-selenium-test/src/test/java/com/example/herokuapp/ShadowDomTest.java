package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class ShadowDomTest extends HerokuAppTest {
    @Override protected String path() { return "/shadowdom"; }

    @Test void shadowHostElementsExist() {
        assertEquals(2, driver.findElements(By.cssSelector("my-paragraph")).size());
    }

    @Test void openShadowRootsExposeTheirText() {
        Object text = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                "return [...document.querySelectorAll('my-paragraph')].map(host => host.shadowRoot.innerText).join(' ')");
        assertTrue(text.toString().contains("Let's have some different text!"));
    }
}
