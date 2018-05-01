#Feature: Delete a profile
#  Scenario: I can delete a particular donor
#    Given I have started the CLI
#    And a user with the NHI "DGH5745" exists
#    When I delete the donor with the above NHI
#    Then The donor should no longer be in the system
#
#  Scenario: i attempt to delete a donor that does not exist
#    Given I have started the CLI
#    And a donor with the NHI "BLA0000" does not exist
#    When I delete the donor with the above NHI
#    Then There is an error message