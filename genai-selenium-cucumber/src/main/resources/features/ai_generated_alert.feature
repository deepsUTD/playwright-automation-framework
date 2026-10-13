Feature: GenAI-assisted Selenium test data
  Use generated synthetic inputs in an end-to-end browser flow.

  Background:
    Given the AutomationPractice page is open

  Scenario: Generated names are echoed by the browser alert
    When the test data provider generates 3 synthetic names
    Then each generated name is echoed by the alert and accepted
    And the name field is cleared after every alert

  Scenario: Empty input is handled by the alert UI
    When I submit the alert with an empty name
    Then the alert contains the empty-name greeting
    And the name field is cleared after every alert

  Scenario: Special characters survive the alert round trip
    When I submit the name "QA O'Connor & Sons 42" to the alert
    Then the alert echoes "QA O'Connor & Sons 42" and I accept it
    And the name field is cleared after every alert
