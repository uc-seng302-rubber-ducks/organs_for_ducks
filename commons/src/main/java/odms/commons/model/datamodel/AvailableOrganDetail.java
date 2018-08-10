package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AvailableOrganDetail {
    private Organs organ;
    private String donorNhi;
    private LocalDateTime momentOfDeath;
    private String region;
    private String bloodType;
    private Double progress;
    private long age;

    public AvailableOrganDetail(Organs organ, String nhi, LocalDateTime momentOfDeath, String region, String bloodType, long age) {
        this.organ = organ;
        this.donorNhi = nhi;
        this.momentOfDeath = momentOfDeath;
        this.region = region;
        this.bloodType = bloodType;
        this.progress = (double) momentOfDeath.until(momentOfDeath.plusHours(organ.getStorageHours()), ChronoUnit.SECONDS);
        this.age = age;
    }

    public AvailableOrganDetail() {
        this.donorNhi = "";
        this.momentOfDeath = null;
        this.organ = null;
        this.region = "";
        this.bloodType = "";
    }

    public Organs getOrgan() {
        return organ;
    }

    public String getDonorNhi() {
        return donorNhi;
    }

    public LocalDateTime getMomentOfDeath() {
        return momentOfDeath;
    }

    public String getRegion() {
        return region;
    }

    public void setMomentOfDeath(LocalDateTime momentOfDeath) {
        this.momentOfDeath = momentOfDeath;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setOrgan(Organs organ) {
        this.organ = organ;
    }

    public void setDonorNhi(String donorNhi) {
        this.donorNhi = donorNhi;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    /**
     * takes a time and returns if the organ is still valid
     *
     * @param timeToaskabout time that the organ needs to be valid at
     *
     * @return true if valid; false if not
     */
    public boolean isOrganStillValid(LocalDateTime timeToaskabout){
        int hoursOrganIsViable = organ.getStorageHours();
        return (timeToaskabout.isBefore(momentOfDeath.plusHours(hoursOrganIsViable)));
    }

    public boolean isOrganStillValid(){
        return isOrganStillValid(LocalDateTime.now());
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }
}
