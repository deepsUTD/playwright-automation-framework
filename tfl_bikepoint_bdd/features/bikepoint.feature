Feature: TfL BikePoint availability API
  Retrieve BikePoint locations and inspect live availability details.

  Scenario: A listed BikePoint has valid availability counts
    Given the TfL BikePoint collection is loaded
    When I request the selected BikePoint by ID
    Then its location matches and availability counts are non-negative integers

  Scenario: The collection endpoint returns 200
    Given the TfL BikePoint API is ready
    When I request all BikePoint locations
    Then the API returns HTTP 200 with a non-empty collection

  Scenario: An empty required search query returns 400
    Given the TfL BikePoint API is ready
    When I search BikePoints with an empty query
    Then the API returns HTTP 400 with a required-query error

  Scenario: An unknown BikePoint ID returns 404
    Given the TfL BikePoint API is ready
    When I request an unknown BikePoint ID
    Then the API returns HTTP 404 with a not-found error
