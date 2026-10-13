package com.example.herokuapp;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises https://jsonplaceholder.typicode.com with an axios client that runs inside the
 * Selenium-driven browser (the API allows cross-origin calls). Axios is loaded from a CDN.
 */
class JsonPlaceholderAxiosTest extends HerokuAppTest {
    private static final String API = "https://jsonplaceholder.typicode.com";
    private static final String AXIOS_URL = "https://cdn.jsdelivr.net/npm/axios@1.7.9/dist/axios.min.js";

    @Override protected String path() { return API; }

    @Override protected void waitForUseCase() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(d -> {
            Object state = ((JavascriptExecutor) d).executeScript("return document.readyState");
            return "interactive".equals(state) || "complete".equals(state);
        });
        Object loaded = ((JavascriptExecutor) driver).executeAsyncScript(
                "var done = arguments[arguments.length - 1];"
                + "if (window.axios) { done(true); return; }"
                + "var s = document.createElement('script');"
                + "s.src = arguments[0];"
                + "s.onload = function() { done(true); };"
                + "s.onerror = function() { done(false); };"
                + "document.head.appendChild(s);", AXIOS_URL);
        assertEquals(Boolean.TRUE, loaded, "axios could not be loaded from " + AXIOS_URL);
    }

    private Map<String, Object> request(String requestConfigJson) {
        @SuppressWarnings("unchecked")
        Map<String, Object> result = (Map<String, Object>) ((JavascriptExecutor) driver).executeAsyncScript(
                "var done = arguments[arguments.length - 1];"
                + "var config = JSON.parse(arguments[0]);"
                + "config.validateStatus = function() { return true; };"
                + "axios(config)"
                + ".then(function(r) { done({status: r.status, data: r.data}); })"
                + ".catch(function(e) { done({error: String(e)}); });", requestConfigJson);
        assertNull(result.get("error"), "axios request failed: " + result.get("error"));
        return result;
    }

    private static int status(Map<String, Object> response) {
        return ((Number) response.get("status")).intValue();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> body(Map<String, Object> response) {
        return (Map<String, Object>) response.get("data");
    }

    @Test void getListReturnsAllPosts() {
        Map<String, Object> response = request("{\"method\":\"get\",\"url\":\"" + API + "/posts\"}");
        assertEquals(200, status(response));
        assertEquals(100, ((List<?>) response.get("data")).size());
    }

    @Test void getSinglePostReturnsExpectedResource() {
        Map<String, Object> response = request("{\"method\":\"get\",\"url\":\"" + API + "/posts/1\"}");
        assertEquals(200, status(response));
        assertEquals(1, ((Number) body(response).get("id")).intValue());
        assertEquals(1, ((Number) body(response).get("userId")).intValue());
        assertNotNull(body(response).get("title"));
    }

    @Test void getUnknownPostReturnsNotFound() {
        Map<String, Object> response = request("{\"method\":\"get\",\"url\":\"" + API + "/posts/9999\"}");
        assertEquals(404, status(response));
    }

    @Test void postCreatesResource() {
        Map<String, Object> response = request("{\"method\":\"post\",\"url\":\"" + API + "/posts\","
                + "\"data\":{\"title\":\"foo\",\"body\":\"bar\",\"userId\":1}}");
        assertEquals(201, status(response));
        assertEquals("foo", body(response).get("title"));
        assertEquals("bar", body(response).get("body"));
        assertEquals(1, ((Number) body(response).get("userId")).intValue());
        assertEquals(101, ((Number) body(response).get("id")).intValue());
    }

    @Test void putReplacesResource() {
        Map<String, Object> response = request("{\"method\":\"put\",\"url\":\"" + API + "/posts/1\","
                + "\"data\":{\"id\":1,\"title\":\"updated title\",\"body\":\"updated body\",\"userId\":1}}");
        assertEquals(200, status(response));
        assertEquals(1, ((Number) body(response).get("id")).intValue());
        assertEquals("updated title", body(response).get("title"));
        assertEquals("updated body", body(response).get("body"));
    }

    @Test void deleteRemovesResource() {
        Map<String, Object> response = request("{\"method\":\"delete\",\"url\":\"" + API + "/posts/1\"}");
        assertEquals(200, status(response));
        assertTrue(body(response).isEmpty(), "DELETE should return an empty object: " + response.get("data"));
    }
}
