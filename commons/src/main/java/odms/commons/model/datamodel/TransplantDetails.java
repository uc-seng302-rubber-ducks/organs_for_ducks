package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDate;

/**
 * a class that holds all
 * details related to transplant.
 *
 */
public class TransplantDetails {
    private String bloodType;
    private String nhi;
    private String name;
    private Organs organ;
    private LocalDate ord; //Organ Registration Date
    private String region;
    private long age;

    public TransplantDetails(String nhi, String name, Organs organ, LocalDate ord, String region, long age, String bloodType) {
        this.nhi = nhi;
        this.name = name;
        this.organ = organ;
        this.ord = ord;
        this.region = region;
        this.age = age;
        this.bloodType = bloodType;
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
        return ord;
    }


    public String getRegion() {
        return region;
    }

    public long getAge() {
        return age;
    }

    public String getBloodType() {
        return bloodType;
    }
}
