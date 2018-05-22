Feature: Dead users should be deleted from the transplant waiting list

  Background: The clinician has receivers
    Given I have started the GUI
    And a user with the NHI "AFF1232" exists
    And they are registered to receive a "Heart"

  Scenario: A deceased user should not be in the transplant waiting list
    Given The user is alive
    When I clicked on Login As Clinician Button
    And I entered Staff ID "0" and Password "admin"
    And clicked on clinician Login button
    And I open the user page
    When The user is updated to have died on "2/10/2003"
    Then the user should not be contained within the transplant waiting list
