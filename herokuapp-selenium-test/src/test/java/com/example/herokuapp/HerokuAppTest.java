package com.example.herokuapp;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.jupiter.api.extension.TestWatcher;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class HerokuAppTest {
    protected static final String BASE_URL = "https://the-internet.herokuapp.com";
    private static final Path SCREENSHOT_DIRECTORY = Path.of("target", "screenshots");
    protected WebDriver driver;
    protected WebDriverWait wait;
    private WebDriverWait navigationWait;
    private volatile String currentTestName;

    @RegisterExtension
    final TestReportExtension testWatcher = new TestReportExtension();

    class TestReportExtension implements TestWatcher, TestExecutionExceptionHandler,
            BeforeTestExecutionCallback, AfterTestExecutionCallback {

        private volatile boolean testExecutionFailed = false;

        @Override
        public void beforeTestExecution(ExtensionContext context) {
            testExecutionFailed = false;
            String testId = testName(context);
            String methodName = context.getTestMethod().map(java.lang.reflect.Method::getName).orElse(context.getDisplayName());
            TestRunReport.startStep(testId, "Execute test verification: " + methodName);
        }

        @Override
        public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
            testExecutionFailed = true;
            String testId = testName(context);
            String methodName = context.getTestMethod().map(java.lang.reflect.Method::getName).orElse(context.getDisplayName());
            TestRunReport.completeStep(testId, "Execute test verification: " + methodName, "failed", 0, throwable.getMessage());
            captureScreenshot(testId, methodName);
            throw throwable;
        }

        @Override
        public void afterTestExecution(ExtensionContext context) {
            if (!testExecutionFailed) {
                String testId = testName(context);
                String methodName = context.getTestMethod().map(java.lang.reflect.Method::getName).orElse(context.getDisplayName());
                TestRunReport.completeStep(testId, "Execute test verification: " + methodName, "passed", 0, null);
            }
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            recordOutcome(context, "passed", null);
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            recordOutcome(context, "failed", TestRunReport.formatThrowable(cause));
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            recordOutcome(context, "aborted", TestRunReport.formatThrowable(cause));
        }

        @Override
        public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
            recordOutcome(context, "skipped", reason.orElse(null));
        }

        private void recordOutcome(ExtensionContext context, String status, String failure) {
            try {
                TestRunReport.recordOutcome(testName(context), status, failure);
            } catch (IOException e) {
                throw new UncheckedIOException("Could not update the test results report", e);
            }
        }
    }

    protected abstract String path();

    protected boolean requiresBasicAuth() {
        return false;
    }

    protected PageLoadStrategy pageLoadStrategy() {
        return PageLoadStrategy.NONE;
    }

    protected void waitForUseCase() {
        String expectedHost = URI.create(BASE_URL).getHost();
        navigationWait.withMessage(() -> "Expected host " + expectedHost + " but Chrome is at " + driver.getCurrentUrl())
            .until(d -> {
                try {
                    String currentUrl = d.getCurrentUrl();
                    if (currentUrl == null || currentUrl.isBlank()) {
                        return false;
                    }
                    String currentHost = URI.create(currentUrl).getHost();
                    return currentHost != null
                            && expectedHost.equals(currentHost)
                            && currentUrl.startsWith(BASE_URL);
                } catch (Exception e) {
                    return false;
                }
            });
        navigationWait.withMessage("Page did not finish loading")
            .until(d -> {
                Object state = ((org.openqa.selenium.JavascriptExecutor) d)
                        .executeScript("return document.readyState");
                return "interactive".equals(state) || "complete".equals(state);
            });
    }

    protected String targetUrl() {
        String route = path();
        return route.startsWith("http") ? route : BASE_URL + route;
    }

    @BeforeAll
    void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1440,1000");
        options.setPageLoadStrategy(pageLoadStrategy());
        java.util.logging.Level browserLogLevel = java.util.logging.Level.ALL;
        org.openqa.selenium.logging.LoggingPreferences logPrefs = new org.openqa.selenium.logging.LoggingPreferences();
        logPrefs.enable(org.openqa.selenium.logging.LogType.BROWSER, browserLogLevel);
        options.setCapability("goog:loggingPrefs", logPrefs);
        if (Boolean.parseBoolean(System.getProperty("herokuapp.headless", "false"))) {
            options.addArguments("--headless=new");
        }
        driver = new ChromeDriver(options);
        ((ChromeDriver) driver).executeCdpCommand("Network.enable", java.util.Map.of());
        ((ChromeDriver) driver).executeCdpCommand("Network.setBlockedURLs",
                java.util.Map.of("urls", java.util.List.of("*optimizely.com*")));
        if (requiresBasicAuth()) {
            ((HasAuthentication) driver).register(UsernameAndPassword.of("admin", "admin"));
        }
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(20));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        navigationWait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    @BeforeEach
    void openUseCase(TestInfo testInfo) {
        String className = testInfo.getTestClass().map(Class::getName).orElse(getClass().getName());
        String methodName = testInfo.getTestMethod().map(method -> method.getName()).orElse(testInfo.getDisplayName());
        currentTestName = className + "#" + methodName;
        TestRunReport.startTest(currentTestName, className, methodName, testInfo.getDisplayName());

        long navStart = System.currentTimeMillis();
        String navStep = "Navigate to " + targetUrl();
        TestRunReport.startStep(currentTestName, navStep);
        try {
            driver.get(targetUrl());
            TestRunReport.completeStep(currentTestName, navStep, "passed", System.currentTimeMillis() - navStart, null);
        } catch (Throwable t) {
            TestRunReport.completeStep(currentTestName, navStep, "failed", System.currentTimeMillis() - navStart, t.getMessage());
            throw t;
        }

        long waitStart = System.currentTimeMillis();
        String waitStep = "Wait for page load and readiness";
        TestRunReport.startStep(currentTestName, waitStep);
        try {
            waitForUseCase();
            TestRunReport.completeStep(currentTestName, waitStep, "passed", System.currentTimeMillis() - waitStart, null);
        } catch (Throwable t) {
            TestRunReport.completeStep(currentTestName, waitStep, "failed", System.currentTimeMillis() - waitStart, t.getMessage());
            throw t;
        }
    }

    /**
     * Executes an explicit named step within a test, recording its outcome and timing in the report.
     */
    protected void step(String name, Runnable action) {
        String testId = currentTestName != null ? currentTestName : getClass().getName();
        long start = System.currentTimeMillis();
        TestRunReport.startStep(testId, name);
        try {
            action.run();
            TestRunReport.completeStep(testId, name, "passed", System.currentTimeMillis() - start, null);
        } catch (Throwable t) {
            TestRunReport.completeStep(testId, name, "failed", System.currentTimeMillis() - start, t.getMessage());
            throw t;
        }
    }

    @AfterEach
    void saveTestScreenshot(TestInfo testInfo) throws IOException {
        String className = testInfo.getTestClass().map(Class::getName).orElse(getClass().getName());
        String methodName = testInfo.getTestMethod().map(method -> method.getName()).orElse(testInfo.getDisplayName());
        String testName = className + "#" + methodName;
        captureScreenshot(testName, methodName);
    }

    private String captureScreenshot(String testName, String methodName) {
        String className = getClass().getName();
        String fileName = (className + "_" + methodName).replaceAll("[^A-Za-z0-9._-]", "_") + ".png";
        String savedFileName = null;
        String screenshotError = null;

        if (driver != null) {
            try {
                Files.createDirectories(SCREENSHOT_DIRECTORY);
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                Files.write(SCREENSHOT_DIRECTORY.resolve(fileName), screenshot);
                savedFileName = fileName;
            } catch (IOException | WebDriverException e) {
                screenshotError = e.toString();
            }
        } else {
            screenshotError = "Browser was not started";
        }
        try {
            TestRunReport.recordScreenshot(testName, savedFileName, screenshotError);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not record screenshot for " + testName, e);
        }
        return savedFileName;
    }

    private static String testName(ExtensionContext context) {
        String className = context.getTestClass().map(Class::getName).orElse("unknown test class");
        String methodName = context.getTestMethod()
                .map(java.lang.reflect.Method::getName)
                .orElse(context.getDisplayName());
        return className + "#" + methodName;
    }

    @AfterAll
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
