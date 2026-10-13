# Playwright Automation Framework

This repository contains TypeScript Playwright tests, Cypress end-to-end tests, and an isolated Python SSN form-validation project.

## Repository layout

- `tests/` and `e2e/`: existing Playwright tests
- `tests/api/`: Playwright API tests for the TfL BikePoint endpoint
- `tfl_bikepoint_bdd/`: Gherkin/pytest-bdd Selenium tests for TfL BikePoint
- `bikepoint-selenium/`: executable Java Selenium JAR and batch runner for TfL BikePoint API checks
- `automation-practice-selenium/`: executable Java Selenium JAR and batch runner for the AutomationPractice UI
- `automation-practice-cucumber/`: Cucumber/Gherkin Selenium scenarios for the AutomationPractice UI
- `genai-selenium-cucumber/`: provider-neutral GenAI test-data interface with Selenium/Cucumber UI scenarios
- `herokuapp-selenium-test/`: standalone Selenium/JUnit suite with one class per Herokuapp example
- `cypress/` and `cypress.config.js`: Cypress tests and configuration
- `ssn_validation/`: local form, pytest/Selenium suites, shared fixture, and Python dependencies

## Python SSN validation

From the repository root:

```powershell
npm run setup:ssn
py -3 -m pytest -c ssn_validation/pytest.ini
```

The suite runs the SSN cases in Chrome, Firefox, and Edge when their browsers and WebDrivers are available. Screenshot comparisons use Pillow.

## Cypress

Start a static server from the repository root:

```powershell
py -3 -m http.server 8000
```

In another terminal, run:

```powershell
npm run test:cypress
```

## Playwright

Install Node dependencies with `npm ci`, then run the existing suite with `npx playwright test`.

## TfL BikePoint API

Run the API module tests with `npm run test:api`. The tests check the response status, location fields, coordinates, and non-negative bike/dock/space counts.

Run the BDD Selenium scenario with `npm run setup:tfl-bdd` followed by `npm run test:tfl-bdd`. It runs in Chrome, Firefox, and Edge when available.

## AutomationPractice UI

Build and run the Selenium JAR with `mvn -f automation-practice-selenium/pom.xml -DskipTests package`, then execute `automation-practice-selenium/run-tests.bat`. The batch file builds the JAR when it is missing.

Run the Cucumber BDD variant with `mvn -f automation-practice-cucumber/pom.xml -DskipTests package`, then execute `automation-practice-cucumber/run-tests.bat`.

## GenAI Selenium Cucumber

See `genai-selenium-cucumber/README.md` for setup. With no `GENAI_API_KEY`, tests use deterministic synthetic fixtures; configure `GENAI_API_KEY`, `GENAI_BASE_URL`, and `GENAI_MODEL` to use an OpenAI-compatible chat-completions provider.
