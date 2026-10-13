package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HoversTest extends HerokuAppTest {
    @Override protected String path() { return "/hovers"; }

    private List<WebElement> waitForProfileImages() {
        return new WebDriverWait(driver, Duration.ofSeconds(30)).until(webDriver -> {
            List<WebElement> images = webDriver.findElements(By.cssSelector(".figure img"));
            return images.size() == 3 && images.stream().allMatch(WebElement::isDisplayed)
                    ? images : null;
        });
    }

    @Test void allProfileImagesAreRendered() {
        assertEquals(3, waitForProfileImages().size());
    }

    @Test void hoveringRevealsProfileCaptionAndLink() {
        List<WebElement> images = waitForProfileImages();
        new Actions(driver).moveToElement(images.get(0)).perform();
        var caption = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".figure .figcaption")));
        assertTrue(caption.getText().contains("name: user1"));
        assertTrue(caption.findElement(By.tagName("a")).isDisplayed());
    }
}
