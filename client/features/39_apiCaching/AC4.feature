Feature: cache is persisted between app launches

  Scenario: app is opened, cache added to, app closed and reopened
    Given the cache is empty
    And the app is logged in as a "user"
    And the "medication" tab is selected
    When the interactions between "drugA" and "drugB" are requested
    And the app is closed and reopened
    Then an entry for "drugA" and "drugB" should be in the cache