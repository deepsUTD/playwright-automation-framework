# Project Guidelines

## Project Scope

- This repository contains Java Selenium/JUnit 5 tests for `the-internet.herokuapp.com` and a JavaScript Mocha API test using Axios and Selenium WebDriver.
- Preserve the existing separation between Java tests and JavaScript API tests.
- Do not commit generated output from `target/` or installed dependencies from `node_modules/`.

## Code Style

- Follow the surrounding file's language and conventions.
- Apply the coding standards of the language and frameworks used by the test.
- Add a concise description to every function or method, including its purpose and any non-obvious inputs or side effects; use the language's standard documentation format.
- Use clear, descriptive test names that state the behavior being verified.
- Keep test cases independent and focused on one behavior.
- Reuse existing constants, setup, teardown, and helper patterns before adding new abstractions.
- Extract repeated setup or behavior into small reusable helpers or components; keep tests modular and avoid duplicated logic without introducing unnecessary abstractions.
- Before adding code, check nearby tests and helpers for duplication; consolidate genuinely repeated logic rather than copying it.
- Use `const` by default in JavaScript and `final` where practical in Java.
- Keep assertions specific and beginner-friendly: use clear expected-versus-actual checks, validate important values, and include a short message when it helps explain a failure.

## Browser and API Tests

- Use Selenium WebDriver for browser interactions and keep browser lifecycle management in setup and teardown hooks.
- Every test script or suite that initializes resources must have explicit initialization and cleanup/teardown, including when a test fails.
- Wait explicitly for the page and target elements to reach the needed state before interacting with them. Use Selenium expected conditions or equivalent condition-based waits; avoid fixed sleeps.
- Keep explicit wait timeouts as short as practical for the condition, and never exceed 60 seconds.
- Preserve headless support for Java tests through the `herokuapp.headless` Maven property.
- For API tests, use Axios for HTTP requests and Chai for assertions.
- Do not hard-code credentials, tokens, or environment-specific secrets.
- Treat network-dependent tests as integration tests and report failures explicitly; do not add silent fallbacks.

## Test Design

- Prefer readable arrange, act, assert flows with one primary behavior per test.
- Use data-driven or parameterized tests wherever multiple inputs exercise the same behavior and assertions; keep separate tests when scenarios have meaningfully different flows or outcomes.
- Make tests independent and deterministic; do not rely on execution order or pre-existing browser state.
- Cover meaningful success and failure behavior where applicable, and verify observable outcomes rather than only that an action completed.
- Identify and test relevant edge cases, such as empty or boundary values, invalid input, missing elements or data, repeated actions, and state transitions. Choose cases that match the feature and avoid redundant or unrealistic scenarios.
- Use shared helpers for genuinely repeated behavior, while keeping each test's intent and assertions easy for a beginner to follow.
- Keep test data, journey intent, and assertions as independent of the browser automation framework as practical. Isolate framework-specific setup, selectors, waits, and interactions so the test behavior can be ported to another framework, such as Playwright, with minimal changes; do not add abstractions that obscure the test or conflict with the project's Selenium conventions.

## Build and Test

- Java test suite: `mvn test`
- Java headless suite: `mvn -Dherokuapp.headless=true test`
- JavaScript API test: `npm run test:api`
- The Java build requires JDK 25+, Maven, Google Chrome, and network access.
- Run the smallest relevant test command after a change, then run the broader suite when shared setup or build configuration changes.

## Change Requirements

- Make focused changes and preserve existing test behavior unless the request explicitly changes it.
- Update related documentation when commands, prerequisites, or test behavior changes.
- Do not edit generated reports or build artifacts as part of source changes.
