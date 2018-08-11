package odms.utils;

import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;

import java.util.Comparator;
import java.util.List;

public final class OrganSorter {

    public static List<TransplantDetails> sortOrgansIntoRankedOrder(AvailableOrganDetail beingDonated, List<TransplantDetails> transplantDetails){
        transplantDetails.sort((Comparator<TransplantDetails>) (o1, o2) -> {
            if(o1.getORD().isBefore(o2.getORD())) return -1;
            if(o1.getORD().isEqual(o2.getORD())){
                return 0; //todo: when we have a distance check add this here to rank organs on the same day against distance 11/8 JB
            }
            if(o1.getORD().isAfter(o2.getORD())) return 1;
            return 0;
        });
        return transplantDetails;
    }
}
