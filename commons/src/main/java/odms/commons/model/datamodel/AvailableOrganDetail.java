package odms.commons.model.datamodel;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressTask;

import java.time.LocalDateTime;

public class AvailableOrganDetail {
    private Organs organ;
    private String donorNhi;
    private LocalDateTime momentOfDeath;
    private String region;
    private String bloodType;
    private transient Service progressTask;

    public AvailableOrganDetail(Organs organ, String nhi, LocalDateTime momentOfDeath, String region, String bloodType) {
        this.organ = organ;
        this.donorNhi = nhi;
        this.momentOfDeath = momentOfDeath;
        this.region = region;
        this.bloodType = bloodType;
        this.progressTask = new Service() {
            @Override
            protected Task createTask() {
                return new ProgressTask(momentOfDeath, organ);
            }
        };
    }

    public AvailableOrganDetail() {
        this.donorNhi = "";
        this.momentOfDeath = null;
        this.organ = null;
        this.region = "";
        this.bloodType = "";
        this.progressTask = new Service() {
            @Override
            protected Task createTask() {
                return new ProgressTask(momentOfDeath, organ);
            }
        };
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


    /**
     * takes a time and returns if the organ is still valid
     *
     * @param timeToaskabout time that the organ needs to be valid at
     *
     * @return trtue if valid; false if not
     */
    public boolean isOrganStillValid(LocalDateTime timeToaskabout){
        double hoursOrganIsViable = organ.getStorageHours();
        return (timeToaskabout.isBefore(momentOfDeath.plusHours((long) hoursOrganIsViable)));
    }

    public boolean isOrganStillValid(){
        return isOrganStillValid(LocalDateTime.now());
    }

    public Service getProgressTask() {
        return progressTask;
    }
}
