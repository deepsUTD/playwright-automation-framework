# Herokuapp Playwright Tests

This is the independent Playwright TypeScript project for migrating the Java Selenium/JUnit browser tests from `../herokuapp-selenium-test`. The Selenium source project and its test files are left unchanged; running or changing this project does not modify or execute the Selenium suite.

## Setup

```powershell
npm install
npx playwright install chromium
```

## Run

```powershell
npm test
npm run test:headed
npm run typecheck
```

Every test run generates a linked custom HTML report at `custom-report/index.html`. Open that file in a browser to see the pass/fail summary; select a test to view its steps, errors, failure screenshot, and any available Playwright trace. Screenshots are captured after a test fails, so they show the page state at the end of the failed test rather than a time-travel image of the exact failed instruction.

The default base URL is `https://the-internet.herokuapp.com`. Set `HEROKUAPP_BASE_URL` to override it. Tests require network access to the target site.

## Migrated coverage

The `tests/` specs cover the Selenium browser journeys for authentication and form input, dynamic pages and controls, alerts and dialogs, windows, frames, downloads and uploads, tables, DOM examples, and browser APIs. The Java Selenium-driven Axios checks are also migrated. Duplicate JSON/CSV login scenarios with identical test data are represented once per shared behavior; standalone API-only JavaScript/Mocha tests remain separate.

The migration agent is available from the source workspace at `.github/agents/playwright-migration.agent.md`.
