Feature: cache is persisted between app launches

  Scenario: app is opened, cache added to, app closed and reopened
    Given the cache is empty
    And the app is logged in as a "user"
    And the "medication" tab is selected
    And the user is taking "Codeine" and "Xanax"
    When the interactions between "Codeine" and "Xanax" are requested
    And the app is closed and reopened
    Then an entry for "Codeine" and "Xanax" should be in the cache