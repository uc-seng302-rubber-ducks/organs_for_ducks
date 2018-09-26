package odms.commons.model.dto;

import odms.commons.model.datamodel.TransplantDetails;

import java.util.Map;

/**
 * simple sub-object of organs-receiver pairs that
 * contains a map of int and matching organs info pair.
 */
public class MatchingOrgans {
    private Map<Integer, TransplantDetails> matchingOrgansRanking;

    public MatchingOrgans(Map<Integer, TransplantDetails> matchingOrgansRanking) {
        this.matchingOrgansRanking = matchingOrgansRanking;
    }

    public Map<Integer, TransplantDetails> getMatchingOrgansRanking() {
        return matchingOrgansRanking;
    }

    public void addMatchingOrgansRanking(Integer integer, TransplantDetails transplantDetails) {
        matchingOrgansRanking.put(integer, transplantDetails);
    }
}
