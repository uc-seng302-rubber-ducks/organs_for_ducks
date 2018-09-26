Feature: Create a profile

  Scenario: I can create a one or more users, each with a name and date of birth
    Given I have started the CLI
    When I register a user with the NHI "CHU8716", first name "Gary", last name "Oak" and date of birth "1997-05-24"
    Then The user should be stored within the application


  Scenario: I attempt to add two users with identical names
    Given I have started the CLI
    And There are no users in the system
    And There exists a user with the NHI "GVT8635", first name "Gary", last name "Snail" and date of birth "2002-10-03"
    When I register a user with the NHI "BHF8624", first name "Gary", last name "Snail" and date of birth "2015-11-13"
    Then There are two profiles with first name "Gary" and last name "Snail"
