Feature: Trainer management endpoints
  As a gym system user
  I want to manage trainer profiles and view their workload
  So that trainers can be properly configured and their workload tracked

  @component @trainer
  Scenario: Register a new trainer successfully
    Given I have trainer registration data with first name "Smith" and last name "Johnson" and training type 1
    When I send a trainer registration request
    Then the response status should be 201
    And the response should contain a trainer username starting with "Smith.Johnson"
    And the response should contain a temporary password

  @component @trainer
  Scenario: Register trainer with missing training type should fail
    Given I have trainer registration data with first name "John" and last name "Smith" without a training type
    When I send a trainer registration request
    Then the response status should be 400
    And the response should contain validation error for field "trainingTypeId"

  @component @trainer
  Scenario: Register trainer with duplicate first and last name should succeed with incremented username
    Given a trainer already exists with first name "Mike" and last name "Johnson" and training type 5
    And I have trainer registration data with first name "Mike" and last name "Johnson" and training type 5
    When I send a trainer registration request
    Then the response status should be 201
    And the response should contain a trainer username starting with "Mike.Johnson1"

  @component @trainer
  Scenario: Get trainer profile should return trainer details
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson"
    When I request trainer profile for "Smith.Johnson"
    Then the response status should be 200
    And the response should contain trainer username "Smith.Johnson"
    And the response should contain trainer details

  @component @trainer
  Scenario: Get non-existent trainer profile should return 404
    Given I have a valid JWT token for authenticated requests
    When I request trainer profile for "nonexistent.trainer"
    Then the response status should be 404
    And the response should contain message "Trainer not found"

  @component @trainer
  Scenario: Update trainer profile successfully
    Given I have a valid JWT token for authenticated requests
    And a trainer already exists with first name "Smith" and last name "Johnson" and training type 1
    And I have trainer update data with first name "James" and last name "Bond" and active status "true"
    When I send a trainer profile update request for "Smith.Johnson" with training type 2
    Then the response status should be 200
    And the response should contain trainer first name "James" and last name "Bond"
    And the response should still contain training type 1

  @component @trainer
  Scenario: Update trainer with blank first name should fail
    Given I have a valid JWT token for authenticated requests
    And a trainer already exists with first name "Smith" and last name "Johnson" and training type 1
    And I have trainer update data with first name "" and last name "Bond" and active status "true"
    When I send a trainer profile update request for "Smith.Johnson" with training type 2
    Then the response status should be 400
    And the response should contain validation error

  @component @trainer
  Scenario: Deactivate trainer successfully
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson"
    When I deactivate trainer "Smith.Johnson"
    Then the response status should be 200
    And the trainer should have active status set to false

  @component @trainer
  Scenario: Activate trainer successfully
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson" and active status "false"
    When I activate trainer "Smith.Johnson"
    Then the response status should be 200
    And the trainer should have active status set to true

  @component @trainer
  Scenario: Get trainer trainings successfully
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson"
    When I request trainer trainings for "Smith.Johnson"
    Then the response status should be 200
    And the response should contain a list of trainings

  @component @trainer
  Scenario: Get trainer trainings for non-existent trainer should return 404
    Given I have a valid JWT token for authenticated requests
    When I request trainer trainings for "nonexistent"
    Then the response status should be 404

  @component @trainer
  Scenario: Get trainer monthly workload successfully
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson"
    When I request trainer monthly workload for "Smith.Johnson" for year 2024 and month 6
    Then the response status should be 200
    And the workload response should contain trainer username "Smith.Johnson"
    And the response should contain year 2024 and month 6
    And the response should contain training duration value

  @component @trainer
  Scenario: Get trainer workload with invalid year should return 400
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "Smith.Johnson"
    When I request trainer monthly workload for "Smith.Johnson" for year "1999" and month 6
    Then the response status should be 400
