package com.example.herokuapp;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.*;

class GeolocationTest extends HerokuAppTest {
    @Override protected String path() { return "/geolocation"; }

    @Test void geolocationButtonIsAvailable() {
        assertTrue(driver.findElement(By.cssSelector("button[onclick*='getLocation']")).isDisplayed());
    }

    @Test void allowLocationDisplaysCoordinates() {
        ((ChromeDriver) driver).executeCdpCommand("Browser.grantPermissions", java.util.Map.of(
                "permissions", java.util.List.of("geolocation"), "origin", BASE_URL));
        ((ChromeDriver) driver).executeCdpCommand("Emulation.setGeolocationOverride", java.util.Map.of(
                "latitude", 37.422, "longitude", -122.084, "accuracy", 50));
        driver.findElement(By.cssSelector("button[onclick*='getLocation']")).click();
        wait.until(d -> d.findElement(By.id("lat-value")).getText().matches(".*\\d.*"));
        assertTrue(driver.findElement(By.id("lat-value")).getText().contains("37.422"));
        assertTrue(driver.findElement(By.id("long-value")).getText().contains("-122.084"));
    }
}
