package com.example.practice.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AutomationPracticeSteps {
    private final TestContext context;
    private boolean textboxVisibleAfterToggle;
    private boolean hoverLinksVisible;
    private boolean courseRowsValid;
    private boolean hasFreeCourse;
    private int fixedTableAmount;
    private final List<String> childWindowUrls = new ArrayList<>();

    public AutomationPracticeSteps(TestContext context) {
        this.context = context;
    }

    @Given("the AutomationPractice page is loaded")
    public void practicePageIsLoaded() {
        require(context.driver.getTitle().equals("Practice Page"),
                "Unexpected page title: " + context.driver.getTitle());
    }

    @When("I select radio option {string}")
    public void selectRadioOption(String value) {
        context.driver.findElement(By.cssSelector(
                "input[name='radioButton'][value='" + value + "']")).click();
    }

    @Then("radio option {string} is the only selected choice")
    public void selectedRadioIsExclusive(String value) {
        List<WebElement> radios = context.driver.findElements(By.name("radioButton"));
        require(radios.size() == 3, "Expected three radio options");
        require(context.driver.findElement(By.cssSelector(
                "input[name='radioButton'][value='" + value + "']")).isSelected(),
                value + " was not selected");
        require(radios.stream().filter(WebElement::isSelected).count() == 1,
                "Radio options should be mutually exclusive");
    }

    @Then("no radio option is selected")
    public void noRadioOptionIsSelected() {
        List<WebElement> radios = context.driver.findElements(By.name("radioButton"));
        require(radios.size() == 3, "Expected three radio options");
        require(radios.stream().noneMatch(WebElement::isSelected), "A radio option is selected by default");
    }

    @When("I type {string} into country autocomplete")
    public void typeIntoCountryAutocomplete(String query) {
        WebElement input = context.driver.findElement(By.id("autocomplete"));
        input.clear();
        input.sendKeys(query);
    }

    @Then("the country suggestions include {string}")
    public void countrySuggestionsInclude(String country) {
        WebElement suggestion = context.wait.until(driver -> driver
                .findElements(By.cssSelector("ul.ui-autocomplete li"))
                .stream()
                .filter(option -> option.getText().trim().equals(country))
                .findFirst()
                .orElse(null));
        require(suggestion.isDisplayed(), "Country suggestion is not visible: " + country);
    }

    @Then("no country suggestions are shown")
    public void noCountrySuggestionsAreShown() {
        context.wait.until(driver -> {
            Object pending = ((JavascriptExecutor) driver).executeScript(
                    "const widget = window.jQuery('#autocomplete').autocomplete('instance'); "
                            + "return widget ? widget.pending : 0;");
            return pending instanceof Number && ((Number) pending).intValue() == 0;
        });
        require(context.driver.findElements(By.cssSelector("ul.ui-autocomplete li"))
                .stream().noneMatch(WebElement::isDisplayed), "Unexpected country suggestions were shown");
    }

    @Then("the country field retains {string}")
    public void countryFieldRetains(String value) {
        require(context.driver.findElement(By.id("autocomplete")).getAttribute("value").equals(value),
                "The unmatched country query was not retained");
    }

    @When("I choose country suggestion {string}")
    public void chooseCountrySuggestion(String country) {
        context.wait.until(driver -> driver
                .findElements(By.cssSelector("ul.ui-autocomplete li"))
                .stream()
                .filter(option -> option.getText().trim().equals(country))
                .findFirst()
                .orElse(null)).click();
    }

    @Then("the country field contains {string}")
    public void countryFieldContains(String country) {
        require(context.driver.findElement(By.id("autocomplete")).getAttribute("value").equals(country),
                "Expected selected country " + country);
    }

    @When("I select each dropdown value")
    public void selectEachDropdownValue() {
        Select dropdown = new Select(context.driver.findElement(By.id("dropdown-class-example")));
        for (WebElement option : dropdown.getOptions()) {
            String value = option.getAttribute("value");
            dropdown.selectByValue(value);
            require(dropdown.getFirstSelectedOption().getAttribute("value").equals(value),
                    "Dropdown did not select " + value);
        }
    }

    @Then("the dropdown supports Select, Option1, Option2, and Option3")
    public void dropdownOptionsAreAvailable() {
        List<String> actualValues = new Select(context.driver.findElement(By.id("dropdown-class-example")))
                .getOptions().stream().map(option -> option.getAttribute("value")).toList();
        require(actualValues.equals(List.of("", "option1", "option2", "option3")),
                "Unexpected dropdown values: " + actualValues);
    }

    @Then("dropdown initially shows {string}")
    public void dropdownInitiallyShows(String label) {
        Select dropdown = new Select(context.driver.findElement(By.id("dropdown-class-example")));
        require(dropdown.getFirstSelectedOption().getText().trim().equals(label),
                "Unexpected initial dropdown selection");
    }

    @When("I select checkbox Option1 and Option2")
    public void selectFirstTwoCheckboxes() {
        clearCheckboxes();
        context.driver.findElement(By.id("checkBoxOption1")).click();
        context.driver.findElement(By.id("checkBoxOption2")).click();
    }

    @Then("those checkboxes are selected independently")
    public void checkboxesSelectedIndependently() {
        require(context.driver.findElement(By.id("checkBoxOption1")).isSelected(), "Option1 should be checked");
        require(context.driver.findElement(By.id("checkBoxOption2")).isSelected(), "Option2 should be checked");
        require(!context.driver.findElement(By.id("checkBoxOption3")).isSelected(), "Option3 should remain unchecked");
    }

    @When("I clear all checkbox selections")
    public void clearAllCheckboxSelections() {
        clearCheckboxes();
    }

    @Then("all three checkboxes are unchecked")
    public void allCheckboxesAreUnchecked() {
        require(context.driver.findElements(By.cssSelector("input[type='checkbox']"))
                .stream().noneMatch(WebElement::isSelected), "A checkbox is still checked");
    }

    @When("I enter {string} and trigger the {string} dialog")
    public void enterNameAndTriggerDialog(String name, String dialogType) {
        WebElement nameInput = context.driver.findElement(By.id("name"));
        nameInput.clear();
        if (!name.isEmpty()) {
            nameInput.sendKeys(name);
        }
        String buttonId = dialogType.equalsIgnoreCase("alert") ? "alertbtn" : "confirmbtn";
        context.driver.findElement(By.id(buttonId)).click();
    }

    @When("I trigger the {string} dialog with an empty name")
    public void triggerDialogWithEmptyName(String dialogType) {
        context.driver.findElement(By.id("name")).clear();
        String buttonId = dialogType.equalsIgnoreCase("alert") ? "alertbtn" : "confirmbtn";
        context.driver.findElement(By.id(buttonId)).click();
    }

    @Then("the alert says {string} and I accept it")
    public void alertHasTextAndIsAccepted(String expectedText) {
        Alert alert = context.wait.until(ExpectedConditions.alertIsPresent());
        require(alert.getText().equals(expectedText), "Unexpected alert text: " + alert.getText());
        alert.accept();
    }

    @Then("the confirm says {string} and I {string} it")
    public void confirmHasTextAndIsHandled(String expectedText, String action) {
        Alert confirm = context.wait.until(ExpectedConditions.alertIsPresent());
        require(confirm.getText().equals(expectedText), "Unexpected confirm text: " + confirm.getText());
        if (action.equalsIgnoreCase("accept")) {
            confirm.accept();
        } else if (action.equalsIgnoreCase("dismiss")) {
            confirm.dismiss();
        } else {
            throw new AssertionError("Unsupported confirm action: " + action);
        }
    }

    @Then("the alert form field is cleared")
    public void alertFormFieldIsCleared() {
        require(context.driver.findElement(By.id("name")).getAttribute("value").isEmpty(),
                "Alert/confirm did not clear the name field");
    }

    @When("I open and close the practice window and tab")
    public void openAndCloseWindowAndTab() {
        childWindowUrls.clear();
        String mainWindow = context.driver.getWindowHandle();
        openAndCloseChild("openwindow", mainWindow);
        openAndCloseChild("opentab", mainWindow);
    }

    @Then("both child pages came from qaclickacademy.com")
    public void bothChildPagesHaveExpectedHost() {
        require(childWindowUrls.size() == 2, "Expected both child page URLs to be recorded");
        require(childWindowUrls.stream().allMatch(url -> url.contains("qaclickacademy.com")),
                "Unexpected child page destinations: " + childWindowUrls);
    }

    @Then("the original practice page remains active")
    public void originalPracticePageRemainsActive() {
        require(context.driver.getTitle().equals("Practice Page"), "Did not return to the practice page");
        require(context.driver.findElement(By.cssSelector("input[name='radioButton']")).isDisplayed(),
            "Practice page controls are unavailable");
    }

    @When("I inspect the course table")
    public void inspectCourseTable() {
        List<WebElement> rows = context.driver.findElements(By.cssSelector("table[name='courses'] tbody tr"))
                .stream()
                .filter(row -> !row.findElements(By.tagName("td")).isEmpty())
                .toList();
        courseRowsValid = rows.size() == 10;
        hasFreeCourse = false;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() != 3) {
                courseRowsValid = false;
                continue;
            }
            int price = Integer.parseInt(cells.get(2).getText().trim());
            courseRowsValid &= !cells.get(0).getText().isBlank()
                    && !cells.get(1).getText().isBlank()
                    && price >= 0;
            hasFreeCourse |= price == 0;
        }
        courseRowsValid &= hasFreeCourse;
    }

    @Then("every course row has instructor, course, and valid price including a free course")
    public void courseTableRowsAreValid() {
        require(courseRowsValid,
                "Course table contains a missing field, invalid price, missing free course, or unexpected row count");
    }

    @When("I inspect the fixed-header table")
    public void inspectFixedHeaderTable() {
        List<WebElement> rows = context.driver.findElements(By.cssSelector(".tableFixHead table tbody tr"));
        fixedTableAmount = 0;
        require(rows.size() == 9, "Expected 9 fixed-table rows, found " + rows.size());
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            require(cells.size() == 4, "Fixed-table rows should contain four cells");
            fixedTableAmount += Integer.parseInt(cells.get(3).getText().trim());
        }
    }

    @Then("the nine amounts sum to the displayed total of 296")
    public void fixedTableAmountMatchesTotal() {
        require(fixedTableAmount == 296, "Fixed-table amount sum changed: " + fixedTableAmount);
        require(context.driver.findElement(By.tagName("body")).getText().contains("Total Amount Collected: 296"),
                "Displayed fixed-table total does not match 296");
    }

    @When("I hide and show the displayed textbox")
    public void hideAndShowTextbox() {
        WebElement textbox = context.driver.findElement(By.id("displayed-text"));
        require(textbox.isDisplayed(), "Textbox should initially be visible");
        context.driver.findElement(By.id("hide-textbox")).click();
        context.wait.until(driver -> !driver.findElement(By.id("displayed-text")).isDisplayed());
        context.driver.findElement(By.id("show-textbox")).click();
        context.wait.until(driver -> driver.findElement(By.id("displayed-text")).isDisplayed());
        textboxVisibleAfterToggle = textbox.isDisplayed();
    }

    @Then("the textbox is visible again")
    public void textboxIsVisibleAgain() {
        require(textboxVisibleAfterToggle, "Show did not restore the textbox");
    }

    @When("I enter {string} and hide then show the textbox")
    public void enterAndHideThenShowTextbox(String value) {
        WebElement textbox = context.driver.findElement(By.id("displayed-text"));
        textbox.clear();
        textbox.sendKeys(value);
        context.driver.findElement(By.id("hide-textbox")).click();
        context.wait.until(driver -> !driver.findElement(By.id("displayed-text")).isDisplayed());
        context.driver.findElement(By.id("show-textbox")).click();
        context.wait.until(driver -> driver.findElement(By.id("displayed-text")).isDisplayed());
    }

    @Then("the textbox is visible and retains {string}")
    public void textboxIsVisibleAndRetains(String value) {
        WebElement textbox = context.driver.findElement(By.id("displayed-text"));
        require(textbox.isDisplayed(), "Textbox should be visible after Show");
        require(textbox.getAttribute("value").equals(value), "Textbox content was not retained");
    }

    @When("I hover over the Mouse Hover button")
    public void hoverOverMouseHoverButton() {
        WebElement hoverButton = context.driver.findElement(By.id("mousehover"));
        new Actions(context.driver).moveToElement(hoverButton).perform();
        WebElement topLink = context.wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".mouse-hover-content a[href='#top']")));
        WebElement reloadLink = context.driver.findElement(By.cssSelector(".mouse-hover-content a:not([href='#top'])"));
        hoverLinksVisible = topLink.isDisplayed() && reloadLink.isDisplayed();
        topLink.click();
    }

    @Then("the Top and Reload links are visible")
    public void hoverLinksAreVisible() {
        require(hoverLinksVisible, "Hover menu links are not visible");
        require(context.driver.getCurrentUrl().contains("#top"), "Top link did not navigate to the page top");
    }

    @When("I switch into the academy iframe")
    public void switchIntoAcademyIframe() {
        WebElement frame = context.wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
        context.driver.switchTo().frame(frame);
    }

    @Then("the Courses link is visible inside the iframe")
    public void coursesLinkIsVisibleInIframe() {
        WebElement courses = context.wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Courses")));
        require(courses.isDisplayed(), "Courses link is not visible inside the iframe");
    }

    @When("I return to the practice page")
    public void returnToPracticePage() {
        context.driver.switchTo().defaultContent();
    }

    @Then("the parent page controls are available")
    public void parentPageControlsAreAvailable() {
        require(context.driver.findElement(By.id("mousehover")).isDisplayed(),
                "Could not return to the parent page");
    }

    private void clearCheckboxes() {
        for (WebElement checkbox : context.driver.findElements(By.cssSelector("input[type='checkbox']"))) {
            if (checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }

    private void openAndCloseChild(String controlId, String mainWindow) {
        Set<String> existingHandles = new HashSet<>(context.driver.getWindowHandles());
        context.driver.findElement(By.id(controlId)).click();
        String child = context.wait.until(driver -> driver.getWindowHandles().stream()
                .filter(handle -> !existingHandles.contains(handle))
                .findFirst()
                .orElse(null));
        context.driver.switchTo().window(child);
        context.wait.until(driver -> !driver.getCurrentUrl().equals("about:blank"));
        require(!context.driver.getCurrentUrl().equals("about:blank"), controlId + " opened a blank page");
        childWindowUrls.add(context.driver.getCurrentUrl());
        context.driver.close();
        context.driver.switchTo().window(mainWindow);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
