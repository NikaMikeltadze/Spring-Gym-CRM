Feature: Gym Main Service Trainee Component Tests

  Scenario: Positive - Valid Trainee Registration
    Given a valid trainee registration request with first name "Jane" and last name "Smith"
    When the client posts to "/api/trainee/register"
    Then the status should be 200 or 201
    And the response should contain a generated username and password

  Scenario: Negative - Invalid Trainee Registration
    Given a trainee registration request with missing last name
    When the client posts to "/api/trainee/register"
    Then the status should be 400

