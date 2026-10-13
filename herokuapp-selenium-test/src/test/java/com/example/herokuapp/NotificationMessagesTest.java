package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import static org.junit.jupiter.api.Assertions.*;

class NotificationMessagesTest extends HerokuAppTest {
    @Override protected String path() { return "/notification_message"; }

    @Test void notificationLinkCanBeClicked() {
        assertTrue(driver.findElement(By.linkText("Click here")).isDisplayed());
    }

    @Test void notificationDisplaysOneDocumentedOutcome() {
        driver.findElement(By.linkText("Click here")).click();
        String message = wait.until(d -> d.findElement(By.id("flash")).getText());
        assertTrue(message.contains("Action successful") || message.contains("Action unsuccesful"), message);
    }
}
