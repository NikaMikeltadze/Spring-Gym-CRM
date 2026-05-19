Feature: Integration between Main Service and Workload Service

  Scenario: Positive - Adding a training updates the workload service
    Given the workload service is up and expecting a workload update
    When I save a new training in the main service
    Then the main service returns a 201 success
    And the workload service received the request successfully

