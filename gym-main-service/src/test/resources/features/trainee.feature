Feature: Trainee management endpoints
  As a gym system user
  I want to manage trainee profiles and assignments
  So that trainees can be properly configured and linked with trainers

  @component @trainee
  Scenario: Register a new trainee successfully
    Given I have trainee registration data with first name "John" and last name "Doe" and date of birth "1990-01-01"
    When I send a trainee registration request
    Then the response status should be 201
    And the response should contain a trainee username starting with "John.Doe"
    And the response should contain a temporary password

  @component @trainee
  Scenario: Register trainee with invalid first name should fail
    Given I have trainee registration data with first name "" and last name "Doe"
    When I send a trainee registration request
    Then the response status should be 400
    And the response should contain validation error for field "firstName"

  @component @trainee
  Scenario: Register trainee with duplicate name should fail with 409
    Given a trainee already exists with first name "Jane" and last name "Doe"
    And I have trainee registration data with first name "Jane" and last name "Doe"
    When I send a trainee registration request
    Then the response status should be 409
    And the response should contain message "already registered as trainer"

  @component @trainee
  Scenario: Get trainee profile should return trainee details
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    When I request trainee profile for "John.Doe"
    Then the response status should be 200
    And the response should contain trainee username "John.Doe"
    And the response should contain trainee details

  @component @trainee
  Scenario: Get non-existent trainee profile should return 404
    Given I have a valid JWT token for authenticated requests
    When I request trainee profile for "nonexistent.trainee"
    Then the response status should be 404
    And the response should contain message "Trainee not found"

  @component @trainee
  Scenario: Update trainee profile successfully
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    And I have trainee update data with first name "Johnny" and last name "Doe"
    When I send a trainee profile update request for "John.Doe"
    Then the response status should be 200
    And the response should contain trainee first name "Johnny"

  @component @trainee
  Scenario: Update trainee with invalid date of birth should fail
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    And I have trainee update data with invalid date of birth "invalid-date"
    When I send a trainee profile update request for "John.Doe"
    Then the response status should be 400

  @component @trainee
  Scenario: Get trainee trainings successfully
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    When I request trainee trainings for "John.Doe"
    Then the response status should be 200
    And the response should contain a list of trainings

  @component @trainee
  Scenario: Get trainee trainings for non-existent trainee should return 404
    Given I have a valid JWT token for authenticated requests
    When I request trainee trainings for "nonexistent"
    Then the response status should be 404

  @component @trainee
  Scenario: Assign trainer to trainee successfully
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    And a trainer exists with username "John.Smith"
    When I assign trainer "John.Smith" to trainee "John.Doe"
    Then the response status should be 200

  @component @trainee
  Scenario: Assign non-existent trainer should return 404
    Given I have a valid JWT token for authenticated requests
    And a trainee exists with username "John.Doe"
    When I assign trainer "nonexistent.trainer" to trainee "John.Doe"
    Then the response status should be 404
    And the response should contain message "Trainer not found"
