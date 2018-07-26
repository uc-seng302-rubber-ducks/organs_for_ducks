Feature: As an user, I want to login to the system, either as a User or Clinician.

  Background: User login to the system
    Given I have started the GUI
    Given The login screen is loaded

  Scenario: I login as user with valid NHI
    Given a user with the NHI "ABC1234" exists
    When I entered NHI "ABC1234"
    And clicked on user Login button
    Then I should see my NHI "ABC1234" along with my other details at the user view screen

  Scenario: I login as user with invalid NHI
    Given a user with the NHI "AD" does not exist
    When I entered NHI "AD"
    And clicked on user Login button
    Then I should see error message "User was not found. \nTo register a new user, please click sign up."

  Scenario: I login as Clinician with valid credentials
    Given a clinician with Staff Id "0" and password "admin" exists
    When I clicked on Login As Clinician Button
    And I entered Staff ID "0" and Password "admin"
    And clicked on clinician Login button
    Then I should see my Staff ID "0" along with my other details at the clinician view screen

  Scenario: I login as Clinician with invalid staff ID
    Given a clinician with staff id "-1000" does not exist
    When I clicked on Login As Clinician Button
    And I entered Staff ID "-1000" and Password "admin"
    And clicked on clinician Login button
    Then I should see error message "The Clinician does not exist"

  Scenario: I login as Clinician with invalid password
    Given a clinician with Staff Id "0" and password "admin" exists
    When I clicked on Login As Clinician Button
    And I entered Staff ID "0" and Password "garbledo"
    And clicked on clinician Login button
    Then I should see error message "An error occurred. Please try again later."


