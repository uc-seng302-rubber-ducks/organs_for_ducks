package seng302.Model;

import java.time.LocalDate;

/**
 * a class that holds all
 * details related to transplant.
 * @author acb116
 */
public class TransplantDetails {
    private String nhi;
    private String name;
    private Organs organ;
    private LocalDate oRD; //Organ Registration Date
    private String region;

    public TransplantDetails(String nhi, String name, Organs organ, LocalDate ORD, String region){
        this.nhi = nhi;
        this.name = name;
        this.organ = organ;
        this.oRD = ORD;
        this.region = region;
    }

    public String getNhi() {
        return nhi;
    }

    public String getName() {
        return name;
    }


    public Organs getOrgan() {
        return organ;
    }


    public LocalDate getORD() {
        return oRD;
    }


    public String getRegion() {
        return region;
    }

}
