package com.example.genai;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class GenAiAlertSteps {
    private static final String ALERT_PREFIX = "Hello ";
    private static final String ALERT_SUFFIX = ", share this practice page and share your knowledge";
    private final TestContext context;

    public GenAiAlertSteps(TestContext context) {
        this.context = context;
    }

    @Given("the AutomationPractice page is open")
    public void practicePageIsOpen() {
        require(context.driver.getTitle().equals("Practice Page"),
                "Unexpected page title: " + context.driver.getTitle());
    }

    @When("the test data provider generates {int} synthetic names")
    public void generateNames(int count) throws Exception {
        context.generatedNames = context.testDataProvider.generateSyntheticNames(count);
        require(context.generatedNames.size() == count, "Provider returned the wrong number of names");
        require(context.generatedNames.stream().allMatch(name -> name != null && !name.isBlank()),
                "Provider returned a blank name");
        require(new HashSet<>(context.generatedNames).size() == count, "Provider returned duplicate names");
        System.out.println("Test data provider: " + context.testDataProvider.providerName());
    }

    @Then("each generated name is echoed by the alert and accepted")
    public void generatedNamesAreEchoed() {
        for (String name : context.generatedNames) {
            WebElement nameInput = context.driver.findElement(By.id("name"));
            nameInput.clear();
            nameInput.sendKeys(name);
            context.driver.findElement(By.id("alertbtn")).click();

            Alert alert = context.wait.until(ExpectedConditions.alertIsPresent());
            String expected = ALERT_PREFIX + name + ALERT_SUFFIX;
            require(alert.getText().equals(expected), "Expected alert '" + expected + "', got '" + alert.getText() + "'");
            alert.accept();
        }
    }

    @Then("the name field is cleared after every alert")
    public void nameFieldIsCleared() {
        require(context.driver.findElement(By.id("name")).getAttribute("value").isEmpty(),
                "The alert flow should clear the name field");
    }

    @When("I submit the alert with an empty name")
    public void submitAlertWithEmptyName() {
        context.driver.findElement(By.id("name")).clear();
        context.driver.findElement(By.id("alertbtn")).click();
    }

    @Then("the alert contains the empty-name greeting")
    public void emptyNameGreetingAppears() {
        Alert alert = context.wait.until(ExpectedConditions.alertIsPresent());
        context.lastAlertText = alert.getText();
        require(context.lastAlertText.equals(ALERT_PREFIX + ALERT_SUFFIX),
                "Unexpected empty-name alert: " + context.lastAlertText);
        alert.accept();
    }

    @When("I submit the name {string} to the alert")
    public void submitSpecificName(String name) {
        WebElement nameInput = context.driver.findElement(By.id("name"));
        nameInput.clear();
        nameInput.sendKeys(name);
        context.driver.findElement(By.id("alertbtn")).click();
    }

    @Then("the alert echoes {string} and I accept it")
    public void alertEchoesSpecificName(String name) {
        Alert alert = context.wait.until(ExpectedConditions.alertIsPresent());
        String expected = ALERT_PREFIX + name + ALERT_SUFFIX;
        require(alert.getText().equals(expected), "Unexpected alert text: " + alert.getText());
        alert.accept();
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
