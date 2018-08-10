package odms.utils;

import odms.commons.model.datamodel.AvailableOrganDetail;
import odms.commons.model.datamodel.TransplantDetails;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrganRanker {


    public OrganRanker() {
    }

    public Map<AvailableOrganDetail, TransplantDetails> matchOrgansToRecievers(List<AvailableOrganDetail> organsAvailable,
                                                                               List<TransplantDetails> waitingTransplants){
        HashMap<AvailableOrganDetail, TransplantDetails> availableOrganDetailTransplantDetailsHashMap;

        for(AvailableOrganDetail organAvailable: organsAvailable) {
            TransplantDetails bestMatch;
            for (TransplantDetails awaitingTransplant : waitingTransplants) {
                if(awaitingTransplant.getOrgan() != organAvailable.getOrgan()){
                    break;
                }
                    break;
                if((organAvailable.getAge() <= 12 || awaitingTransplant.getAge() <=12) && (organAvailable.getAge() >= 12 || awaitingTransplant.getAge() >=12)){
                    break;
                }else if(organAvailable.getAge() >= awaitingTransplant.getAge() + 15 && organAvailable.getAge() <= awaitingTransplant.getAge() - 15){
                    break;
                }
                if(!organAvailable.getBloodType().equalsIgnoreCase(awaitingTransplant.getBloodType())){
                    break;
                }
                if(!organAvailable.isOrganStillValid()){
                    break;
                }
                if(bestMatch.getORD().isBefore(awaitingTransplant.getORD())){
                    break;
                }
            }
        }
    }


}
