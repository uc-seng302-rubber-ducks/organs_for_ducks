Feature: As a clinician, I want to be able to disqualify organs on a user

  Background: Clinician logged into the system
    Given I have started the GUI
    And a user with the NHI "XYZ4567" exists
    And The login screen is loaded
    And The donation tab is open

  Scenario: I want to disqualify an organ and then remove it
    When I click on the first organ in the available organs list
    And I click on the disqualify organ button
    And I enter a description
    And I click confirm
    Then I should see the disqualified organ in the table
    And I click on the disqualified organ in the table
    And I click the remove disqualification button
    And I enter a reason why
    And I confirm the removal
    Then I should see that the disqualified organ is not in the table

  Scenario: When A user dies, any disqualified organs they had should become expired
    When I click on the first organ in the available organs list
    And I click on the disqualify organ button
    And I enter a description
    And I click confirm
    Then I should see the disqualified organ in the table
    And then i open the user details
    And then i mark the user dead
    Then the organ should be expired






