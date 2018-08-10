package odms.utils;

import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganRanker {


    public OrganRanker() {
    }

    /**
     * A mehtod that takes a list of Organs available and a list of people awaiting a transplant and then attempts to match
     * them based on the following criteria
     *
     * Must match:
     * Organ type
     * Blood type
     * Age difference of no more than 15 years or both donor and receiver under 12
     *
     * Conditions to find better matches(in order):
     * Time Waiting
     * Region
     *
     * This version currently only checks they are in the same region
     * @param organsAvailable A list of avaliable organs
     * @param waitingTransplants A list of people awaiting a transplant
     * @return A map of organs to the best match, Note a receiver may be the best match for more than one organ
     */
    public Map<AvailableOrganDetail, TransplantDetails> matchOrgansToReceivers(List<AvailableOrganDetail> organsAvailable,
                                                                               List<TransplantDetails> waitingTransplants){
        HashMap<AvailableOrganDetail, TransplantDetails> bestOrganMatches = new HashMap<>();

        for(AvailableOrganDetail organAvailable: organsAvailable) {
            TransplantDetails bestMatch = null;
            for (TransplantDetails awaitingTransplant : waitingTransplants) {
                if(awaitingTransplant.getOrgan() != organAvailable.getOrgan()){
                    continue;
                }
                if((organAvailable.getAge() <= 12 || awaitingTransplant.getAge() <=12) && (organAvailable.getAge() > 12 || awaitingTransplant.getAge() > 12)){
                    continue;
                }else {
                    if(organAvailable.getAge() >= awaitingTransplant.getAge() + 15 || organAvailable.getAge() <= awaitingTransplant.getAge() - 15){
                        continue;
                    }
                }
                if(!organAvailable.getBloodType().equalsIgnoreCase(awaitingTransplant.getBloodType())){
                    continue;
                }
                if(bestMatch == null){
                    if(awaitingTransplant.getRegion().equalsIgnoreCase(organAvailable.getRegion())){
                        bestMatch = awaitingTransplant;
                        continue;
                    } else {
                        continue;
                    }
                }
                if(bestMatch.getORD().isBefore(awaitingTransplant.getORD())){
                    continue;
                }
                if(!awaitingTransplant.getRegion().equalsIgnoreCase(organAvailable.getRegion())){
                    continue; //TODO: this just checks that the region is the same It needs some way of getting distances to be implemneted fully 10/8 JB
                }
                bestMatch = awaitingTransplant;
            }
            if(bestMatch != null) {
                bestOrganMatches.put(organAvailable, bestMatch);
            }
        }
        return bestOrganMatches;
    }


}
