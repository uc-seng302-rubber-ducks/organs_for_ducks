Feature: Create a profile
  Scenario: I can create a one or more donors, each with a name and date of birth
    Given I have started the CLI
    When I register a donor with the first name "Gary" and last name "Oak" and date of birth "1997-05-24"
    Then The donor should be stored within the application

  Scenario: The created timestamp should be automatically added to the profile
    Given I have started the CLI
    When I register a donor with the first name "Drak" and last name "Ibenheim" and date of birth "1997-12-16"
    And I view the previously created donor
    Then the timestamp should be displayed along with the rest of the profile

  Scenario: I can print all donors and respective attributes as a list.
    Given I have started the CLI
    And a user with the first name "John" and last name "Snow" and date of birth "1965-02-04"
    And a user with the first name "Ed" and last name "Ward" and date of birth "2002-11-06"
    When I use the view command to view all donors
    Then The profiles for "John Snow" and "Ed Ward" are displayed

  Scenario: An error message is displayed when I try to print all donors as a list while there are no donors
    Given I have started the CLI
    And There are no donors in the system
    When I use the view command to view all donors
    Then The error message "There are no donors in the system"

  Scenario: I attempt to add two donors with identical names
    Given I have started the CLI

  Scenario: Attempting to add a donor with the same identifier
    Given I have started the CLI

Feature: Register an organ
  Scenario: I can set the organs to donate for a particular donor
    Given I have started the CLI

  Scenario: i attempt to set the organs for a donor that does not exist
    Given I have started the CLI

  Scenario: I can print all the organs that all donors wish to donate
    Given I have started the CLI

  Scenario: I attempt to print all the organs when no donors are in the system
    Given I have started the CLI

  Scenario: I can print all the organs that a particular donor wishes to donate
    Given I have started the CLI

  Scenario: I attempt to print the organs of an unregistered donor
    Given I have started the CLI

Feature: Update a profile
  Scenario: I can update a donor's full name
    Given I have started the CLI

  Scenario: I can update a donor's date of birth
    Given I have started the CLI

  Scenario: I can update a donor's date of death
    Given I have started the CLI

  Scenario: I can update a donor's address
    Given I have started the CLI

  Scenario: I can update a donor's region
    Given I have started the CLI

  Scenario: I can update a donor's blood type
    Given I have started the CLI

  Scenario: I can update a donor's gender
    Given I have started the CLI

  Scenario: I can add organs the donor wishes to donate
    Given I have started the CLI

  Scenario: I can remove organs the donor wishes to donate
    Given I have started the CLI

  Scenario: I attempt to add an organ that the donor already donates
    Given I have started the CLI

  Scenario: I attempt to remove an organ that the donor is not donating
    Given I have started the CLI

Feature: Delete a profile
  Scenario: I can delete a particular donor
    Given I have started the CLI

  Scenario: i attempt to delete a donor that does not exist
    Given I have started the CLI
