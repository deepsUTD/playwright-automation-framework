# Herokuapp Selenium Test

A Java 25+, Maven, Selenium, and JUnit 5 suite for the examples listed on `https://the-internet.herokuapp.com/`. Each example has its own test class and multiple independent test methods where applicable.

## Run

From this directory:

```powershell
mvn test
```

Chrome runs visibly by default. At the end of each use-case class, the browser stays open for five seconds for manual verification, then closes. The full suite is intentionally long because this pause is applied per use case.

For headless execution:

```powershell
mvn -Dherokuapp.headless=true test
```

Requirements: JDK 25+, Maven, Google Chrome, and network access to the site. Selenium Manager may download the matching ChromeDriver on first run.

## Coverage

There is one class for each of the 44 examples on the site homepage, covering authentication, forms, dynamic controls, dialogs, frames, downloads/uploads, tables, navigation, and DOM interactions. Credentials used by the site's demo Basic and Digest Auth examples are `admin` / `admin`.

## Custom Test Report

Every test run automatically generates an interactive, styled HTML report matching the Playwright custom reporter:

- **Summary Report:** `custom-report/index.html` (and `target/custom-report/index.html`)
- Displays overall pass/fail/aborted/skipped summary counts and a tabular test list.
- Each test name is a clickable link to a dedicated test detail page showing:
  - Overall status and execution duration.
  - Step-by-step progress with status badges and error messages.
  - Failure details and full stack trace on errors.
  - Screenshots captured during test execution, associated directly with the failed step on failures.

