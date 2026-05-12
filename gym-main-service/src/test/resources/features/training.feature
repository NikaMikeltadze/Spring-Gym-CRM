Feature: Training operations
  As a gym system user
  I want to add trainings and fetch available training types
  So that training sessions can be created and managed

  @component @training
  Scenario: Add training successfully
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "Yoga" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 201
    And the response should contain training details

  @component @training
  Scenario: Add training with missing duration should fail validation
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "Yoga" and no duration
    When I send an add training request
    Then the response status should be 400
    And the response should contain validation error for field "trainingDuration"

  @component @training
  Scenario: Add training with non-existent trainee should return 400
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "nonexistent.trainee", trainer "John.Smith", type "Yoga" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 400
    And the response should contain message "Trainee not found"

  @component @training
  Scenario: Add training with non-existent trainer should return 400
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And training type "Yoga" exists
    And I have training data with trainee "Sarah.Williams", trainer "nonexistent.trainer", type "Yoga" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 400
    And the response should contain message "Trainer not found"

  @component @training
  Scenario: Add training with non-existent type should return 400
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And a trainer exists with username "John.Smith"
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "NonexistentType" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 400
    And the response should contain message "Training type not found"

  @component @training
  Scenario: Add training without authentication should return 401
    Given I am not authenticated
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "Yoga" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 401

  @component @training
  Scenario: Add training with missing trainee should fail validation
    Given I have a valid JWT token for authenticated requests
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "", trainer "John.Smith", type "Yoga" and duration 1.5 hours
    When I send an add training request
    Then the response status should be 400
    And the response should contain validation error

  @component @training
  Scenario: Fetch all training types successfully
    Given I have a valid JWT token for authenticated requests
    When I request all training types
    Then the response status should be 200
    And the response should contain list of training types

  @component @training
  Scenario: Fetch training types should return consistent data
    Given I have a valid JWT token for authenticated requests
    When I request all training types
    Then the response status should be 200
    And the response should contain at least 1 training type

  @component @training
  Scenario: Add training with date in past should succeed
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "Yoga", duration 1.5 hours and date "2024-01-15"
    When I send an add training request
    Then the response status should be 201

  @component @training
  Scenario: Add training with future date should fail validation
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "Sarah.Williams"
    And a trainer exists with username "John.Smith"
    And training type "Yoga" exists
    And I have training data with trainee "Sarah.Williams", trainer "John.Smith", type "Yoga", duration 1.5 hours and date "2026-12-31"
    When I send an add training request
    Then the response status should be 400
    And the response should contain validation error for field "trainingDate"
