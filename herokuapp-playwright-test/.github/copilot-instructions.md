# Playwright Project Guidelines

## Project Scope

- This project contains TypeScript Playwright tests migrated from the Java Selenium/JUnit suite for `the-internet.herokuapp.com`.
- Keep this project independent from the original Selenium project; do not edit or remove source tests during migration.

## Code and Test Design

- Use TypeScript, Playwright Test, and the surrounding project's conventions.
- Give each test a descriptive title and each helper function a concise description.
- Keep scenarios focused and independent, with clear arrange, act, and assert steps.
- Make assertions specific and beginner-friendly. Prefer Playwright web-first assertions and observable outcomes.
- Use data-driven tests when multiple inputs share the same journey and expected behavior.
- Check for duplication before adding code. Reuse concise helpers or page components for repeated behavior without obscuring test intent.
- Cover relevant success, failure, boundary, and state-transition cases without adding redundant scenarios.
- Keep journey intent and test data maintainable if browser automation frameworks change; isolate selectors and framework-specific interactions where practical.

## Browser Automation

- Use Playwright locators and built-in auto-waiting; do not use fixed sleeps.
- Rely on Playwright Test fixtures for page/browser setup and teardown.
- Keep explicit timeouts as short as practical and never exceed 60 seconds.
- Use the configured base URL and avoid hard-coded secrets.
- Network-dependent tests must report failures explicitly; do not hide failures with silent fallbacks.

## Build and Test

- Install dependencies with `npm install`.
- Install Chromium with `npx playwright install chromium`.
- Run tests with `npm test`; run headed with `npm run test:headed`.
- Check TypeScript with `npm run typecheck`.
- Test runs generate a linked custom HTML report at `custom-report/index.html`; inspect linked details and failure screenshots there.
- Test execution requires Node.js, Playwright's Chromium browser, and network access to the target website.
