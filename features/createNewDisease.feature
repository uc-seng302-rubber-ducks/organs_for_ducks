Feature: As an clinician, I want to create (add) new disease to Donor's profile.

  Background: clinician creates new disease to Donor's profile
    Given I have started the GUI
    Given The Create New Disease screen is loaded

  Scenario: I create new disease that is not chronic nor cured
    When I entered Disease Name "Lazyness Disease" and used the default Diagnosis Date
    And clicked on Create Button
    Then I should see the Disease Name "Lazyness Disease" at the Current Diseases Table

  Scenario: I create new disease that is chronic
    When I entered Disease Name "Death Disease" and used the default Diagnosis Date
    And clicked on Status Chronic
    And clicked on Create Button
    Then I should see the Disease Name "Death Disease" and the word chronic in red next to disease name at the Current Diseases Table

  Scenario: I create new disease that is cured
    When I entered Disease Name "Dengue Fever" and used the default Diagnosis Date
    And clicked on Status Cured
    And clicked on Create Button
    Then I should see the Disease Name "Dengue Fever" at the Past Diseases Table

  Scenario: I create new disease without entering the name
    When I entered Disease Name "" and used the default Diagnosis Date
    And clicked on Status Cured
    And clicked on Create Button
    Then I should see error message for disease name "Invalid Disease Name"


  Scenario: I create new disease when diagnosis date is before date of birth
    When I entered Disease Name "Lazyness Disease" and Diagnosis Date "3/1/1800"
    And clicked on Create Button
    Then I should see error message for diagnosis date "Invalid Diagnosis Date"

  Scenario: I create new disease when diagnosis date is after current date
    When I entered Disease Name "Lazyness Disease" and Diagnosis Date "5/3/2200"
    And clicked on Create Button
    Then I should see error message for diagnosis date "Invalid Diagnosis Date"
