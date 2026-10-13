# AutomationPractice Cucumber Selenium checks

This Maven project exercises the Rahul Shetty AutomationPractice page with Cucumber feature scenarios and Selenium Chrome.

Coverage includes radio buttons, country autocomplete, dropdown options, checkboxes, alerts and confirm accept/dismiss, windows/tabs, course and fixed-header tables, hide/show, hover links, and iframe navigation.

## Build and run

Build the executable JAR from this directory:

```powershell
mvn -DskipTests package
```

Run it directly:

```powershell
java -jar target/automation-practice-cucumber.jar
```

Or run `run-tests.bat`; it builds the JAR if missing and propagates the Cucumber exit code.

Requirements: Maven, JDK 17 or newer, Google Chrome, and network access to `rahulshettyacademy.com`. Selenium Manager may download a matching ChromeDriver on first run.
