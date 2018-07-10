Feature: Caching needs to be done gradually as when the resource is needed (not all resources at once)

  Scenario: Data is only added to the cache as requested
    Given the cache is empty
    When the interactions between "drugA" and "drugB" are requested
    Then an entry for "drugA" and "drugB" should be in the cache