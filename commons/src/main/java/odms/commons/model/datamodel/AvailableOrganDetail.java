package odms.commons.model.datamodel;

import javafx.concurrent.Service;
import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressBarService;
import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.LocalDateTime;

public class AvailableOrganDetail {
    private Organs organ;
    private String donorNhi;
    private LocalDateTime momentOfDeath;
    private LocalDateTime expiryDate;
    private String region;
    private String bloodType;
    private transient ProgressBarService progressTask; //NOSONAR

    public AvailableOrganDetail(Organs organ, String nhi, LocalDateTime momentOfDeath, String region, String bloodType) {
        this.organ = organ;
        this.donorNhi = nhi;
        this.momentOfDeath = momentOfDeath;
        this.region = region;
        this.bloodType = bloodType;
        this.progressTask = new ProgressBarService(momentOfDeath, organ);
        this.expiryDate = calculateExpiryDate(momentOfDeath, organ);
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

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void generateProgressTask() {
        this.progressTask = new ProgressBarService(momentOfDeath, organ);
    }

    /**
     * takes a time and returns if the organ is still valid
     *
     * @param timeToaskabout time that the organ needs to be valid at.
     *
     * @return trtue if valid; false if not
     */
    public boolean isOrganStillValid(LocalDateTime timeToaskabout) {
        long secondsOrganIsViable = organ.getStorageSeconds();
        return (timeToaskabout.isBefore(momentOfDeath.plusSeconds(secondsOrganIsViable)));
    }

    public boolean isOrganStillValid() {
        return isOrganStillValid(LocalDateTime.now());
    }

    /**
     * Uses the organs expiry date to return the seconds left until the organ expires
     *
     * @param fromThisTime time to calculate expiry time for. Will most often be LocalDateTime.now()
     *
     * @return long value of how many seconds are left
     */
    public long calculateTimeLeft(LocalDateTime fromThisTime) {
        long timeLeft = SECONDS.between(fromThisTime, expiryDate);
        if (timeLeft < 0) {
            return 0;
        } else {
            return timeLeft;
        }
    }

    /**
     * Returns an expiry date for an organ given a time of death and organ type
     *
     * @param timeOfDeath LocalDateTime of when the donor died
     * @param organType Organs enum of the type of organ
     * @return LocalDateTime of when the organ will expire
     */
    private LocalDateTime calculateExpiryDate(LocalDateTime timeOfDeath, Organs organType) {
        long expiryTime = organType.getStorageSeconds();
        return timeOfDeath.plusSeconds(expiryTime);
    }

    public Service getProgressTask() {
        return progressTask;
    }
}
