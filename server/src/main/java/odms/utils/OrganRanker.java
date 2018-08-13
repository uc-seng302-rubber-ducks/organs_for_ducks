package odms.utils;

import odms.commons.model._enum.Organs;
import odms.commons.model._enum.Regions;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.CityDistanceCalculator;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class OrganRanker {

    private final double HELICOPTER_SPEED = 62.5; // Speed a helicopter flies in M/s. From a google search of how fast does a medical helicopter fly


    public OrganRanker() {
    }

    /**
     * A mehtod that takes a list of Organs available and a list of people awaiting a transplant and then attempts to match
     * them based on the following criteria
     * <p>
     * Must match:
     * Organ type
     * Blood type
     * Age difference of no more than 15 years or both donor and receiver under 12
     * <p>
     * Conditions to find better matches(in order):
     * Time Waiting
     * Region
     * <p>
     * This version currently only checks they are in the same region
     *
     * @param organsAvailable    A list of avaliable organs
     * @param waitingTransplants A list of people awaiting a transplant
     * @return A map of organs to the best match, Note a receiver may be the best match for more than one organ
     */
    public Map<AvailableOrganDetail, List<TransplantDetails>> matchOrgansToReceivers(List<AvailableOrganDetail> organsAvailable,
                                                                                     List<TransplantDetails> waitingTransplants) {
        Map<AvailableOrganDetail, List<TransplantDetails>> organMatches = new HashMap<>();
        Set<String> regionsWithDistance = Regions.getEnums();
        CityDistanceCalculator distanceCalculator = new CityDistanceCalculator();
        for (AvailableOrganDetail organAvailable : organsAvailable) {
            List<TransplantDetails> matches = new ArrayList<>();
            LocalDateTime death = organAvailable.getMomentOfDeath();
            Organs organ = organAvailable.getOrgan();
            double timeRemaining = death.until(death.plusSeconds(organ.getUpperBoundSeconds()), ChronoUnit.SECONDS);
            for (TransplantDetails awaitingTransplant : waitingTransplants) {
                if (awaitingTransplant.getOrgan() != organAvailable.getOrgan()) {
                    continue;
                }
                if ((organAvailable.getAge() <= 12 || awaitingTransplant.getAge() <= 12) && (organAvailable.getAge() > 12 || awaitingTransplant.getAge() > 12)) {
                    continue;
                } else {
                    if (organAvailable.getAge() >= awaitingTransplant.getAge() + 15 || organAvailable.getAge() <= awaitingTransplant.getAge() - 15) {
                        continue;
                    }
                }
                if (!organAvailable.getBloodType().equalsIgnoreCase(awaitingTransplant.getBloodType())) {
                    continue;
                }

                if(regionsWithDistance.contains(awaitingTransplant.getRegion()) && regionsWithDistance.contains(organAvailable.getRegion())){
                    double distanceBetweenDonorAndReceiver = distanceCalculator.distanceBetweenRegions(
                            awaitingTransplant.getRegion(),
                            organAvailable.getRegion());
                    double timeToDonor = distanceBetweenDonorAndReceiver/HELICOPTER_SPEED;
                    if(timeToDonor > timeRemaining) continue;
                } else if (!awaitingTransplant.getRegion().equalsIgnoreCase(organAvailable.getRegion())) {
                    continue;
                }
                matches.add(awaitingTransplant);
            }

            organMatches.put(organAvailable, matches);
        }
        return organMatches;
    }


}
