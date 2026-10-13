# Rahul Shetty AutomationPractice Selenium checks

This Maven project runs browser-based checks against `https://rahulshettyacademy.com/AutomationPractice/`.

Coverage includes radio-button exclusivity, country autocomplete, dropdown values, independent checkboxes, alert and confirm accept/dismiss behavior, new windows and tabs, both tables and the fixed-table total, hide/show, hover-menu links, and the iframe.

## Build and run

From this directory, build the executable JAR with:

```powershell
mvn -DskipTests package
```

Run the generated JAR with:

```powershell
java -jar target/automation-practice-selenium.jar
```

Or run `run-tests.bat`, which builds the JAR if missing, runs it, and returns its exit status.

Requirements: Maven, JDK 17 or newer, Google Chrome, and access to the practice site. Selenium Manager may download the matching ChromeDriver on first run.
