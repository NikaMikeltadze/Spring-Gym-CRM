Feature: Trainer workload operations
  As a gym system user
  I want to query trainer monthly workload
  So that I can review aggregated trainer activity

  @component @workload @positive
  Scenario: Get trainer monthly workload successfully
    Given a trainer workload exists for trainer "john.smith" in year 2026 and month 5
    When I request the monthly workload
    Then the workload response status should be 200
    And the workload response should contain trainer username "john.smith"
    And the workload response should contain year 2026 and month 5
    And the workload response should contain monthly duration 10.0

  @component @workload @positive
  Scenario: Get trainer monthly workload for existing trainer without month data returns zero
    Given a trainer workload exists for trainer "john.smith" in year 2026 and month 5
    When I request the monthly workload for year 2026 and month 6
    Then the workload response status should be 200
    And the workload response should contain trainer username "john.smith"
    And the workload response should contain year 2026 and month 6
    And the workload response should contain monthly duration 0.0

  @component @workload @negative
  Scenario: Get trainer monthly workload with invalid month should fail
    Given a trainer workload exists for trainer "john.smith" in year 2026 and month 5
    When I request the monthly workload with invalid month 13
    Then the workload response status should be 400
    And the workload error response should contain message "month must be between 1 and 12"

  @component @workload @negative
  Scenario: Get trainer monthly workload with invalid year should fail
    Given a trainer workload exists for trainer "john.smith" in year 2026 and month 5
    When I request the monthly workload with invalid year 0
    Then the workload response status should be 400
    And the workload error response should contain message "year must be positive"

  @component @workload @negative
  Scenario: Get trainer monthly workload for unknown trainer should fail
    When I request the monthly workload for trainer "unknown.trainer" in year 2026 and month 5
    Then the workload response status should be 404
    And the workload error response should contain message "Trainer not found: unknown.trainer"

  @component @workload @positive
  Scenario: Delete trainer monthly workload successfully
    Given a trainer workload exists for trainer "john.smith" in year 2026 and month 5
    When I delete the monthly workload
    Then the workload response status should be 204
