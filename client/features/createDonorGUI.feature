Feature: As a client, I want to create a user account on the system.

  Background: client create a new user account
    Given I have started the GUI
    Given The user sign up screen is loaded
    And There are no users in the system


#  Scenario: I create a user profile with basic info
#    When I register a user using the GUI with the NHI "ADE1987", first name "Dwayne" and date of birth "3/1/2017"
#    And Clicked on the Create Profile button
#    Then I should see my NHI "ADE1987" along with my other details at the user view screen

  Scenario: I create a user profile when date of birth is in the future
    When I register a user using the GUI with the NHI "ADE1987", first name "Dwayne" and date of birth "3/1/2500"
    And Clicked on the Create Profile button
    Then I should see an invalid date of birth, "invalidDOB" error message

#  Scenario: I create a user profile when date of death is in the future
#    When I register a user using the GUI with the NHI "ADE1987", first name "Dwayne", date of birth "3/1/2017" and date of death "2/5/2500"
#    And Clicked on the Create Profile button
#    Then I should see an invalid date of death, "invalidDOB" error message

#  Scenario: I create a user profile with basic info and health info
#    When I register a user using the GUI with the NHI "ADE1987", first name "Dwayne" and date of birth "3/1/2017"
#    And with health info, which consist of birth gender "Male", height "1.75", weight "65", blood type "B+", alcohol consumption "None", and unticked on the smoker checkbox
#    And Clicked on the Create Profile button
#    Then I should see my NHI "ADE1987" first name "Dwayne", Smoker is marked as "No", alcohol "None" and date of birth "2017-01-03"

#  Scenario: I create a user profile with basic info and preferred name
#    When I register a user using the GUI with the NHI "ADE1987", first name "Dwayne" and date of birth "3/1/2017"
#    And entered preferred name "The Rock"
#    And Clicked on the Create Profile button
#    Then I should see my preferred name "The Rock" along with my other details at the user view screen
