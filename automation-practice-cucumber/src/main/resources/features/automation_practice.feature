Feature: Rahul Shetty AutomationPractice controls
  Exercise the visible practice-page controls with Selenium.

  Background:
    Given the AutomationPractice page is loaded

  Scenario Outline: Radio choices are mutually exclusive
    When I select radio option "<value>"
    Then radio option "<value>" is the only selected choice

    Examples:
      | value  |
      | radio1 |
      | radio2 |
      | radio3 |

  Scenario: Radio group starts with no selection
    Then no radio option is selected

  Scenario: Country autocomplete selects a matching suggestion
    When I type "Ind" into country autocomplete
    Then the country suggestions include "India"
    When I choose country suggestion "India"
    Then the country field contains "India"

  Scenario: Country autocomplete handles a query with no matches
    When I type "NoSuchCountry987" into country autocomplete
    Then no country suggestions are shown
    And the country field retains "NoSuchCountry987"

  Scenario: Dropdown exposes each supported option
    When I select each dropdown value
    Then the dropdown supports Select, Option1, Option2, and Option3

  Scenario: Dropdown starts on its placeholder
    Then dropdown initially shows "Select"

  Scenario: Checkboxes can be selected independently and cleared
    When I select checkbox Option1 and Option2
    Then those checkboxes are selected independently
    When I clear all checkbox selections
    Then all three checkboxes are unchecked

  Scenario: Checkboxes start unchecked
    Then all three checkboxes are unchecked

  Scenario: Alert reports the entered name
    When I enter "Selenium Test" and trigger the "Alert" dialog
    Then the alert says "Hello Selenium Test, share this practice page and share your knowledge" and I accept it
    And the alert form field is cleared

  Scenario: Alert handles an empty name
    When I trigger the "Alert" dialog with an empty name
    Then the alert says "Hello , share this practice page and share your knowledge" and I accept it
    And the alert form field is cleared

  Scenario Outline: Confirm dialog supports accept and dismiss
    When I enter "Confirm Test" and trigger the "Confirm" dialog
    Then the confirm says "Hello Confirm Test, Are you sure you want to confirm?" and I "<choice>" it
    And the alert form field is cleared

    Examples:
      | choice  |
      | accept  |
      | dismiss |

  Scenario: Confirm handles an empty name
    When I trigger the "Confirm" dialog with an empty name
    Then the confirm says "Hello , Are you sure you want to confirm?" and I "dismiss" it
    And the alert form field is cleared

  Scenario: Open Window and Open Tab create child browsing contexts
    When I open and close the practice window and tab
    Then both child pages came from qaclickacademy.com
    And the original practice page remains active

  Scenario: Course table has valid data rows
    When I inspect the course table
    Then every course row has instructor, course, and valid price including a free course

  Scenario: Fixed table total matches its rows
    When I inspect the fixed-header table
    Then the nine amounts sum to the displayed total of 296

  Scenario: Hide and Show control textbox visibility
    When I hide and show the displayed textbox
    Then the textbox is visible again

  Scenario Outline: Hide and Show preserves typed text
    When I enter "<value>" and hide then show the textbox
    Then the textbox is visible and retains "<value>"

    Examples:
      | value               |
      | Persist me 123      |
      | Edge case value 987 |

  Scenario: Mouse hover reveals its menu
    When I hover over the Mouse Hover button
    Then the Top and Reload links are visible

  Scenario: Iframe content can be inspected and exited
    When I switch into the academy iframe
    Then the Courses link is visible inside the iframe
    When I return to the practice page
    Then the parent page controls are available
