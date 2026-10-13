package com.example.genai;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public final class BrowserHooks {
    private static final String PRACTICE_URL = "https://rahulshettyacademy.com/AutomationPractice/";
    private final TestContext context;

    public BrowserHooks(TestContext context) {
        this.context = context;
    }

    @Before
    public void startScenario() {
        context.testDataProvider = AiTestDataProviderFactory.fromEnvironment();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1200");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        context.driver = new ChromeDriver(options);
        context.driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(25));
        context.wait = new WebDriverWait(context.driver, Duration.ofSeconds(12));
        context.driver.get(PRACTICE_URL);
    }

    @After
    public void closeScenario() {
        if (context.driver != null) {
            context.driver.quit();
        }
    }
}
