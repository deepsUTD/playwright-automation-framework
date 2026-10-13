package com.example.genai;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

public final class TestContext {
    WebDriver driver;
    WebDriverWait wait;
    AiTestDataProvider testDataProvider;
    List<String> generatedNames = new ArrayList<>();
    String lastAlertText;
}
