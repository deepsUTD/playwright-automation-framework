package com.example.tfl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class BikePointApiSeleniumTest {
    private static final String API_ROOT = "https://api.tfl.gov.uk";
    private static final String UNKNOWN_BIKEPOINT_ID = "BikePoints_DOES_NOT_EXIST_987654321";
    private static final ObjectMapper JSON = new ObjectMapper();

    private BikePointApiSeleniumTest() {
    }

    public static void main(String[] args) throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1000");

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.get(API_ROOT + "/BikePoint");

            verifyBikePointCollection(driver);
            verifyEmptySearchReturnsBadRequest(driver);
            verifyUnknownBikePointReturnsNotFound(driver);

            System.out.println("All TfL BikePoint Selenium API scenarios passed.");
        } finally {
            driver.quit();
        }
    }

    private static void verifyBikePointCollection(WebDriver driver) throws Exception {
        System.out.println("Given the TfL BikePoint API is available");
        System.out.println("When I request all BikePoint locations");
        ApiResponse response = get(driver, "/BikePoint");
        require(response.status() == 200, "GET /BikePoint expected HTTP 200, got " + response.status());

        JsonNode bikePoints = JSON.readTree(response.body());
        require(bikePoints.isArray() && !bikePoints.isEmpty(), "BikePoint response must be a non-empty array");

        for (JsonNode bikePoint : bikePoints) {
            requireText(bikePoint, "id");
            requireText(bikePoint, "commonName");
            require(bikePoint.path("placeType").asText().equals("BikePoint"), "Unexpected placeType");
            require(bikePoint.path("lat").isNumber(), "Latitude must be numeric");
            require(bikePoint.path("lat").asDouble() >= -90 && bikePoint.path("lat").asDouble() <= 90,
                    "Latitude is outside its valid range");
            require(bikePoint.path("lon").isNumber(), "Longitude must be numeric");
            require(bikePoint.path("lon").asDouble() >= -180 && bikePoint.path("lon").asDouble() <= 180,
                    "Longitude is outside its valid range");

            Map<String, String> properties = propertyValues(bikePoint.path("additionalProperties"));
            requireCount(properties, "nbbikes", bikePoint.path("id").asText());
            requireCount(properties, "nbdocks", bikePoint.path("id").asText());
            String spaces = properties.getOrDefault("nbspaces", properties.get("nbemptydocks"));
            requireCountValue(spaces, "NbSpaces/NbEmptyDocks", bikePoint.path("id").asText());
        }

        System.out.println("Then HTTP 200 includes valid locations and availability counts");
    }

    private static void verifyEmptySearchReturnsBadRequest(WebDriver driver) throws Exception {
        System.out.println("Given the search query is required");
        System.out.println("When I search BikePoints with an empty query");
        ApiResponse response = get(driver, "/BikePoint/Search?query=");
        require(response.status() == 400, "Empty search expected HTTP 400, got " + response.status());

        JsonNode error = JSON.readTree(response.body());
        require(error.path("httpStatusCode").asInt() == 400, "Error payload must report HTTP 400");
        require(error.path("message").asText().contains("A value is required"),
                "Error payload should explain that a query value is required");
        System.out.println("Then HTTP 400 explains that the query value is required");
    }

    private static void verifyUnknownBikePointReturnsNotFound(WebDriver driver) throws Exception {
        System.out.println("Given a BikePoint ID that does not exist");
        System.out.println("When I request that BikePoint");
        ApiResponse response = get(driver, "/BikePoint/" + UNKNOWN_BIKEPOINT_ID);
        require(response.status() == 404, "Unknown BikePoint expected HTTP 404, got " + response.status());

        JsonNode error = JSON.readTree(response.body());
        require(error.path("httpStatusCode").asInt() == 404, "Error payload must report HTTP 404");
        require(error.path("httpStatus").asText().equals("NotFound"), "Error payload must report NotFound");
        System.out.println("Then HTTP 404 reports that the BikePoint was not found");
    }

    private static ApiResponse get(WebDriver driver, String path) {
        Object rawResponse = ((JavascriptExecutor) driver).executeAsyncScript(
                "const path = arguments[0];"
                        + "const done = arguments[arguments.length - 1];"
                        + "fetch(path, { headers: { Accept: 'application/json' } })"
                        + ".then(async response => done({ status: response.status, body: await response.text() }))"
                        + ".catch(error => done({ error: String(error) }));",
                path);

        if (!(rawResponse instanceof Map<?, ?> response)) {
            throw new AssertionError("The browser did not return an API response for " + path);
        }
        if (response.containsKey("error")) {
            throw new AssertionError("API request failed for " + path + ": " + response.get("error"));
        }

        return new ApiResponse(((Number) response.get("status")).intValue(), String.valueOf(response.get("body")));
    }

    private static Map<String, String> propertyValues(JsonNode properties) {
        Map<String, String> values = new HashMap<>();
        if (properties.isArray()) {
            Iterator<JsonNode> iterator = properties.elements();
            while (iterator.hasNext()) {
                JsonNode property = iterator.next();
                values.put(property.path("key").asText().toLowerCase(), property.path("value").asText());
            }
        }
        return values;
    }

    private static void requireCount(Map<String, String> properties, String key, String bikePointId) {
        requireCountValue(properties.get(key), key, bikePointId);
    }

    private static void requireCountValue(String value, String name, String bikePointId) {
        require(value != null, bikePointId + " is missing " + name);
        int count;
        try {
            count = Integer.parseInt(value);
        } catch (NumberFormatException error) {
            throw new AssertionError(bikePointId + " has a non-integer " + name + ": " + value, error);
        }
        require(count >= 0, bikePointId + " has a negative " + name);
    }

    private static void requireText(JsonNode object, String property) {
        require(!object.path(property).asText().isBlank(), "BikePoint is missing " + property);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private record ApiResponse(int status, String body) {
    }
}
