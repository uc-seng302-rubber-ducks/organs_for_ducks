#Feature: cached resources are gradually timed out and can be removed
#
#
#  Scenario: several items in the cache but only one should be timed out
#    Given the cache is pre-populated
#    When all data before "date" is removed
#    Then the cache should not contain an entry with key "Xanax-pancreaze"
#
#  Scenario: several items in the cached, all to be cleared
#    Given the cache is pre-populated
#    When the cache is cleared
#    Then the cache should be empty