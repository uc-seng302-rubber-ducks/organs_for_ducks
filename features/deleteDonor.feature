Feature: Delete a profile

  Scenario: I can delete a particular user
    Given I have started the CLI
    And a user with the NHI "DGH5745" exists
    When I delete the user with the above NHI
    Then The user should no longer be in the system