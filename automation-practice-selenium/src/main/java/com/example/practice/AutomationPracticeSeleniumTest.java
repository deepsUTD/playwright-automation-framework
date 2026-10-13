package com.example.practice;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AutomationPracticeSeleniumTest {
    private static final String PRACTICE_URL = "https://rahulshettyacademy.com/AutomationPractice/";
    private static final Duration WAIT = Duration.ofSeconds(12);

    private AutomationPracticeSeleniumTest() {
    }

    public static void main(String[] args) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1440,1200");
        options.setPageLoadStrategy(PageLoadStrategy.EAGER);

        WebDriver driver = new ChromeDriver(options);
        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
            System.out.println("Opening " + PRACTICE_URL);
            driver.get(PRACTICE_URL);
            System.out.println("Loaded page title: " + driver.getTitle());
            require(driver.getTitle().equals("Practice Page"), "Unexpected page title: " + driver.getTitle());
            System.out.println("Given the AutomationPractice page is loaded");

            verifyRadioButtons(driver);
            verifyCountryAutocomplete(driver, wait);
            verifyDropdownOptions(driver);
            verifyCheckboxes(driver);
            verifyAlertAndConfirm(driver, wait);
            verifyNewWindowAndTab(driver, wait);
            verifyCourseTable(driver);
            verifyFixedTableAndTotal(driver);
            verifyHideAndShow(driver, wait);
            verifyHoverMenu(driver, wait);
            verifyIframe(driver, wait);

            System.out.println("All AutomationPractice Selenium scenarios passed.");
        } finally {
            driver.quit();
        }
    }

    private static void verifyRadioButtons(WebDriver driver) {
        System.out.println("When I select each radio option");
        List<WebElement> radios = driver.findElements(By.name("radioButton"));
        require(radios.size() == 3, "Expected three radio options");

        for (WebElement radio : radios) {
            radio.click();
            require(radio.isSelected(), "Selected radio option is not checked");
            long selectedCount = radios.stream().filter(WebElement::isSelected).count();
            require(selectedCount == 1, "Radio options should be mutually exclusive");
        }
        System.out.println("Then each radio option can be selected exclusively");
    }

    private static void verifyCountryAutocomplete(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I search the country autocomplete for Ind");
        WebElement input = driver.findElement(By.id("autocomplete"));
        input.clear();
        input.sendKeys("Ind");

        WebElement india = wait.until(currentDriver -> currentDriver
                .findElements(By.cssSelector("ul.ui-autocomplete li"))
                .stream()
                .filter(option -> option.getText().trim().equals("India"))
                .findFirst()
                .orElse(null));
        india.click();
        require(input.getAttribute("value").equals("India"), "Autocomplete did not select India");
        System.out.println("Then India is selected from the suggestion list");
    }

    private static void verifyDropdownOptions(WebDriver driver) {
        System.out.println("When I select every dropdown option");
        Select dropdown = new Select(driver.findElement(By.id("dropdown-class-example")));
        List<String> expectedValues = List.of("", "option1", "option2", "option3");
        List<String> actualValues = dropdown.getOptions().stream().map(option -> option.getAttribute("value")).toList();
        require(actualValues.equals(expectedValues), "Dropdown options changed: " + actualValues);

        for (String value : expectedValues) {
            dropdown.selectByValue(value);
            require(dropdown.getFirstSelectedOption().getAttribute("value").equals(value),
                    "Dropdown did not select " + value);
        }
        System.out.println("Then Select and Option1 through Option3 are selectable");
    }

    private static void verifyCheckboxes(WebDriver driver) {
        System.out.println("When I check and uncheck independent options");
        List<WebElement> checkboxes = List.of(
                driver.findElement(By.id("checkBoxOption1")),
                driver.findElement(By.id("checkBoxOption2")),
                driver.findElement(By.id("checkBoxOption3")));

        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                checkbox.click();
            }
        }
        checkboxes.get(0).click();
        checkboxes.get(1).click();
        require(checkboxes.get(0).isSelected() && checkboxes.get(1).isSelected(),
                "Checkboxes should be independently selectable");
        require(!checkboxes.get(2).isSelected(), "Unchecked third checkbox should remain unchecked");
        checkboxes.get(0).click();
        checkboxes.get(1).click();
        require(checkboxes.stream().noneMatch(WebElement::isSelected), "Checkboxes did not clear");
        System.out.println("Then checkbox selections are independent and reversible");
    }

    private static void verifyAlertAndConfirm(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I submit a name to Alert and Confirm");
        WebElement nameInput = driver.findElement(By.id("name"));
        nameInput.sendKeys("Selenium Test");
        driver.findElement(By.id("alertbtn")).click();
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        require(alert.getText().equals("Hello Selenium Test, share this practice page and share your knowledge"),
                "Unexpected alert message: " + alert.getText());
        alert.accept();
        require(nameInput.getAttribute("value").isEmpty(), "Alert did not clear the name field");

        nameInput.sendKeys("Confirm Test");
        driver.findElement(By.id("confirmbtn")).click();
        Alert confirmAccept = wait.until(ExpectedConditions.alertIsPresent());
        require(confirmAccept.getText().equals("Hello Confirm Test, Are you sure you want to confirm?"),
                "Unexpected confirm message: " + confirmAccept.getText());
        confirmAccept.accept();
        require(nameInput.getAttribute("value").isEmpty(), "Confirm did not clear the name field after accept");

        nameInput.sendKeys("Dismiss Test");
        driver.findElement(By.id("confirmbtn")).click();
        wait.until(ExpectedConditions.alertIsPresent()).dismiss();
        require(nameInput.getAttribute("value").isEmpty(), "Confirm did not clear the name field after dismiss");
        System.out.println("Then alert text, confirm text, accept, dismiss, and field clearing are verified");
    }

    private static void verifyNewWindowAndTab(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I open and close the practice window and tab");
        String mainWindow = driver.getWindowHandle();
        Set<String> originalWindowHandles = new HashSet<>(driver.getWindowHandles());
        driver.findElement(By.id("openwindow")).click();
        wait.until(currentDriver -> currentDriver.getWindowHandles().size() > originalWindowHandles.size());
        verifyAndCloseChildWindow(driver, wait, mainWindow, originalWindowHandles);

        Set<String> originalTabHandles = new HashSet<>(driver.getWindowHandles());
        driver.findElement(By.id("opentab")).click();
        wait.until(currentDriver -> currentDriver.getWindowHandles().size() > originalTabHandles.size());
        verifyAndCloseChildWindow(driver, wait, mainWindow, originalTabHandles);
        require(driver.getWindowHandle().equals(mainWindow), "Driver did not return to the original window");
        System.out.println("Then each child window opens and returns to the practice page");
    }

    private static void verifyAndCloseChildWindow(
            WebDriver driver,
            WebDriverWait wait,
            String mainWindow,
            Set<String> originalHandles) {
        String childWindow = wait.until(currentDriver -> currentDriver.getWindowHandles().stream()
                .filter(handle -> !originalHandles.contains(handle))
                .findFirst()
                .orElse(null));
        driver.switchTo().window(childWindow);
        wait.until(currentDriver -> !currentDriver.getCurrentUrl().equals("about:blank"));
        require(!driver.getCurrentUrl().equals("about:blank"), "Child window remained blank");
        driver.close();
        driver.switchTo().window(mainWindow);
    }

    private static void verifyCourseTable(WebDriver driver) {
        System.out.println("When I inspect the course table");
        List<WebElement> rows = driver.findElements(By.cssSelector("table[name='courses'] tbody tr"))
            .stream()
            .filter(row -> !row.findElements(By.tagName("td")).isEmpty())
            .toList();
        require(rows.size() == 10, "Expected 10 course rows, found " + rows.size());

        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            require(cells.size() == 3, "Course row must have instructor, course, and price cells");
            require(!cells.get(0).getText().isBlank(), "Course instructor is empty");
            require(!cells.get(1).getText().isBlank(), "Course title is empty");
            require(Integer.parseInt(cells.get(2).getText().trim()) >= 0, "Course price must be non-negative");
        }
        System.out.println("Then all course rows have an instructor, title, and valid price");
    }

    private static void verifyFixedTableAndTotal(WebDriver driver) {
        System.out.println("When I inspect the fixed-header table");
        List<WebElement> rows = driver.findElements(By.cssSelector(".tableFixHead table tbody tr"));
        require(rows.size() == 9, "Expected 9 fixed-table rows, found " + rows.size());

        int amountSum = 0;
        for (WebElement row : rows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            require(cells.size() == 4, "Fixed-table row must contain name, position, city, and amount");
            require(!cells.get(0).getText().isBlank(), "Fixed-table name is empty");
            amountSum += Integer.parseInt(cells.get(3).getText().trim());
        }
        require(amountSum == 296, "Fixed-table amount sum changed: " + amountSum);
        require(driver.findElement(By.tagName("body")).getText().contains("Total Amount Collected: 296"),
                "Displayed total does not match the row sum");
        System.out.println("Then the nine amounts sum to the displayed total of 296");
    }

    private static void verifyHideAndShow(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I hide and show the textbox");
        WebElement textbox = driver.findElement(By.id("displayed-text"));
        require(textbox.isDisplayed(), "Textbox should initially be visible");
        driver.findElement(By.id("hide-textbox")).click();
        wait.until(currentDriver -> !currentDriver.findElement(By.id("displayed-text")).isDisplayed());
        driver.findElement(By.id("show-textbox")).click();
        wait.until(currentDriver -> currentDriver.findElement(By.id("displayed-text")).isDisplayed());
        require(driver.findElement(By.id("displayed-text")).isDisplayed(), "Show did not restore the textbox");
        System.out.println("Then Hide and Show update textbox visibility");
    }

    private static void verifyHoverMenu(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I hover over the mouse-hover control");
        WebElement hoverButton = driver.findElement(By.id("mousehover"));
        new Actions(driver).moveToElement(hoverButton).perform();
        WebElement topLink = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".mouse-hover-content a[href='#top']")));
        WebElement reloadLink = driver.findElement(By.cssSelector(".mouse-hover-content a:not([href='#top'])"));
        require(topLink.isDisplayed() && reloadLink.isDisplayed(), "Hover menu links are not visible");
        topLink.click();
        require(driver.getCurrentUrl().contains("#top"), "Top link did not navigate to the page top");
        System.out.println("Then the Top and Reload hover-menu choices are revealed");
    }

    private static void verifyIframe(WebDriver driver, WebDriverWait wait) {
        System.out.println("When I inspect the practice iframe");
        WebElement frame = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("iframe")));
        driver.switchTo().frame(frame);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Courses")));
        require(driver.findElement(By.linkText("Courses")).isDisplayed(), "Iframe Courses link is not visible");
        driver.switchTo().defaultContent();
        require(driver.findElement(By.id("mousehover")).isDisplayed(), "Could not return to the parent page");
        System.out.println("Then the iframe is accessible and the parent page is restored");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
