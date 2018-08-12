package odms.utils;

import odms.commons.model._enum.Regions;
import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;
import odms.commons.utils.CityDistanceCalculator;

import java.util.List;
import java.util.Set;

public final class OrganSorter {

    private OrganSorter() {
    }

    public static List<TransplantDetails> sortOrgansIntoRankedOrder(AvailableOrganDetail beingDonated, List<TransplantDetails> transplantDetails){
        transplantDetails.sort((o1, o2) -> {
            if(o1.getORD().isBefore(o2.getORD())) return -1;
            if(o1.getORD().isEqual(o2.getORD())){
                Set<String> regionsWithDistance = Regions.getEnums();
                if(regionsWithDistance.contains(o1.getRegion()) && regionsWithDistance.contains(o2.getRegion()) && regionsWithDistance.contains(beingDonated.getRegion())){
                    CityDistanceCalculator  cdc = new CityDistanceCalculator();
                        double o1Distance = cdc.distanceBetweenRegions(beingDonated.getRegion(),o1.getRegion());
                        double o2Distance = cdc.distanceBetweenRegions(beingDonated.getRegion(), o2.getRegion());
                        if(o1Distance == o2Distance) return 0;
                        else if(o1Distance > o2Distance) return 1;
                        else if(o2Distance > o1Distance) return -1;
                } else {
                    return 0;
                }
            }
            if(o1.getORD().isAfter(o2.getORD())) return 1;
            return 0;
        });
        return transplantDetails;
    }
}
