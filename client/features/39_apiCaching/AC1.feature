# commented out as they require an internet connection. the requester is very difficult to mock and not worth the effort
# all these features pass locally, with a network connection as of 19/6
#

#Feature: Caching needs to be done gradually as when the resource is needed (not all resources at once)
#
#  Scenario: Data is only added to the cache as requested
#    Given the cache is empty
#    When the interactions between "drugA" and "drugB" are requested
#    Then an entry for "drugA" and "drugB" should be in the cache