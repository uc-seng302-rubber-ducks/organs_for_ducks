Feature: As an user, I want to login to the system, either as a User or Clinician.

  Background: User login to the system
    Given I have started the GUI
    Given The login screen is loaded

  Scenario: I login as user with valid NHI
    When I entered NHI "ABC1234"
    And clicked on user Login button
    Then I should see my NHI "ABC1234" along with my other details at the user view screen

  Scenario: I login as user with invalid NHI
    When I entered NHI "AD"
    And clicked on user Login button
    Then I should see error message "User was not found.\nTo register a new user please click sign up."

  Scenario: I login as Clinician with valid credentials
    When I clicked on Login As Clinician Button
    And I entered Staff ID "0" and Password "admin"
    And clicked on clinician Login button
    Then I should see my Staff ID "0" along with my other details at the clinician view screen

  Scenario: I login as Clinician with invalid staff ID
    When I clicked on Login As Clinician Button
    And I entered Staff ID "-1000" and Password "admin"
    And clicked on clinician Login button
    Then I should see error message "The Clinician does not exist"

  Scenario: I login as Clinician with invalid password
    When I clicked on Login As Clinician Button
    And I entered Staff ID "0" and Password "garbledo"
    And clicked on clinician Login button
    Then I should see error message "Your password is incorrect please try again"


