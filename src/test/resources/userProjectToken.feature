@TodoLy
Feature: User and Project with Token authentication

  Background:
    Given generate unique email in "EMAIL"
    And generate password in "PASSWORD"

  @Regression @UserProjectToken
  Scenario: Create a user, use its token to create, update and delete a project, then delete the user
    # 1. create user (no auth)
    When send POST request "api/user.json" with NONE auth and body
    """
    {
      "Email":"EMAIL",
      "FullName":"Juan Tester",
      "Password":"PASSWORD"
    }
    """
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "Email" should be "EMAIL"
    And the "FullName" should be "Juan Tester"
    And the response should match schema "userSchema.json"

    # 2. get token with the created user's credentials
    When send GET request "api/authentication/token.json" with BASIC auth
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "UserEmail" should be "EMAIL"
    And save "TokenString" in "TOKEN"

    # 3. create project (token)
    When send POST request "api/projects.json" with TOKEN auth and body
    """
    {
      "Content":"Proyecto Token",
      "Icon":4
    }
    """
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "Content" should be "Proyecto Token"
    And the response should match schema "projectSchema.json"
    And save "Id" in "ID_PROJECT"

    # 4. update project (token)
    When send PUT request "api/projects/ID_PROJECT.json" with TOKEN auth and body
    """
    {
      "Content":"Proyecto Token Actualizado"
    }
    """
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "Content" should be "Proyecto Token Actualizado"

    # 5. delete project (token)
    When send DELETE request "api/projects/ID_PROJECT.json" with TOKEN auth
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "Deleted" should be "true"

    # 6. delete user (token)
    When send DELETE request "api/user/0.json" with TOKEN auth
    Then code should be 200
    And the response should not contain "ErrorCode"
    And the "Email" should be "EMAIL"

    # 7. the user no longer exists
    When send GET request "api/authentication/token.json" with BASIC auth
    Then code should be 200
    And the "ErrorCode" should be "105"
    And the "ErrorMessage" should be "Account doesn't exist"
