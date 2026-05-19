Feature: Authentication endpoints
  As a user
  I want to authenticate and change my password
  So that I can access protected resources and manage my security

  @component @auth
  Scenario: Login with valid credentials should return JWT token
    Given I have valid credentials with username "John.Smith" and password "aB3dEfGh1K"
    When I send a login request
    Then the response status should be 200
    And the response should contain a JWT token
    And I should store the token for authenticated requests

  @component @auth
  Scenario: Login with invalid password should return 401
    Given I have valid username "John.Smith" but invalid password "wrongPassword"
    When I send a login request
    Then the response status should be 401
    And the response should contain message "Invalid username or password"

  @component @auth
  Scenario: Login with non-existent user should return 401
    Given I have username "nonexistent.user" and password "somePassword"
    When I send a login request
    Then the response status should be 401
    And the response should contain message "Invalid username or password"

  @component @auth
  Scenario: Change password with valid current password should succeed
    Given I have a valid JWT token for authenticated requests
    And I have current password "aB3dEfGh1K" and new password "newPass2"
    When I send a change password request
    Then the response status should be 200

  @component @auth
  Scenario: Change password with blank new password should fail validation
    Given I have a valid JWT token for authenticated requests
    And I have current password "aB3dEfGh1K" and new password ""
    When I send a change password request
    Then the response status should be 400
    And the response should contain validation error for field "newPassword"

  @component @auth
  Scenario: Change password without authentication should return 401
    Given I am not authenticated
    And I have current password "aB3dEfGh1K" and new password "newPass2"
    When I send a change password request
    Then the response status should be 401

  @component @auth
  Scenario: Change password with empty current password should fail validation
    Given I have a valid JWT token for authenticated requests
    And I have current password "" and new password "newPass2"
    When I send a change password request
    Then the response status should be 400
    And the response should contain validation error for field "password"

