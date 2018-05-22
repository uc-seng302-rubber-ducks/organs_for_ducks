Feature: Create a profile

  Scenario: I can create a one or more users, each with a name and date of birth
    Given I have started the CLI
    When I register a user with the NHI "CHU8716", first name "Gary", last name "Oak" and date of birth "1997-05-24"
    Then The user should be stored within the application

  Scenario: The created timestamp should be automatically added to the profile
    Given I have started the CLI
    When I register a user with the NHI "VGA9720", first name "Drak", last name "Ibenheim" and date of birth "1997-12-16"
    And I view the previously created user
    Then the timestamp should be displayed along with the rest of the profile

  Scenario: I can print all users and respective attributes as a list.
    Given I have started the CLI
    And a user with the NHI "ABC1234" exists
    And a user with the NHI "GHT5641" exists
    When I use the view command to view all users
    Then The profiles for "ABC1234" and "GHT5631" are displayed

  Scenario: An error message is displayed when I try to print all users as a list while there are no users
    Given I have started the CLI
    And There are no users in the system
    When I use the view command to view all users
    Then The error message "There are no users in the system"

  Scenario: I attempt to add two users with identical names
    Given I have started the CLI
    And There exists a user with the NHI "GVT8635", first name "Gary", last name "Snail" and date of birth "2002-10-03"
    When I register a user with the NHI "BHF8624", first name "Gary", last name "Snail" and date of birth "2015-11-13"
    Then There are two profiles with first name "Gary" and last name "Snail"

  Scenario: Attempting to add a user with the same identifier
    Given I have started the CLI
    And There exists a user with the NHI "HUG8625", first name "Buschemi", last name "Swol" and date of birth "2006-07-10"
    When I register a user with the NHI "HUG8625", first name "Ben", last name "Swolo" and date of birth "1997-07-10"
    Then There is an error message
