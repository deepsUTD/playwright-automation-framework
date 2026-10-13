package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

class JavaScriptAlertsTest extends HerokuAppTest {
    @Override protected String path() { return "/javascript_alerts"; }

    @Test void alertCanBeAccepted() {
        driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();
        Alert alert = wait.until(d -> d.switchTo().alert());
        assertEquals("I am a JS Alert", alert.getText());
        alert.accept();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.id("result"), "You successfully clicked an alert"));
        assertTrue(driver.findElement(By.id("result")).getText().contains("You successfully clicked an alert"));
    }

    @Test void confirmCanBeAccepted() {
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        Alert confirm = wait.until(d -> d.switchTo().alert());
        assertEquals("I am a JS Confirm", confirm.getText());
        confirm.accept();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("result"), "You clicked: Ok"));
        assertTrue(driver.findElement(By.id("result")).getText().contains("You clicked: Ok"));
    }

    @Test void confirmCanBeDismissed() {
        driver.findElement(By.xpath("//button[text()='Click for JS Confirm']")).click();
        Alert confirm = wait.until(d -> d.switchTo().alert());
        assertEquals("I am a JS Confirm", confirm.getText());
        confirm.dismiss();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("result"), "You clicked: Cancel"));
        assertTrue(driver.findElement(By.id("result")).getText().contains("You clicked: Cancel"));
    }

    @Test void promptCanBeAcceptedWithText() {
        driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();
        Alert prompt = wait.until(d -> d.switchTo().alert());
        assertEquals("I am a JS prompt", prompt.getText());
        prompt.sendKeys("Selenium prompt");
        prompt.accept();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("result"), "Selenium prompt"));
        assertTrue(driver.findElement(By.id("result")).getText().contains("Selenium prompt"));
    }

    @Test void promptCanBeDismissed() {
        driver.findElement(By.xpath("//button[text()='Click for JS Prompt']")).click();
        Alert prompt = wait.until(d -> d.switchTo().alert());
        assertEquals("I am a JS prompt", prompt.getText());
        prompt.dismiss();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("result"), "You entered: null"));
        assertTrue(driver.findElement(By.id("result")).getText().contains("You entered: null"));
    }
}
