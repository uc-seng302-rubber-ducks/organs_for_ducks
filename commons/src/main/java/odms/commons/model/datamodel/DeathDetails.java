package odms.commons.model.datamodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Class to store the death details of a donor
 */
public class DeathDetails {

    private LocalDate dateOfDeath;
    private LocalTime timeOfDeath;
    private Address placeOfDeath;

    public DeathDetails() {
        this.dateOfDeath = null;
        this.timeOfDeath = null;
        this.placeOfDeath = new Address("", "", "", "", "", "", "");
    }

    public DeathDetails(LocalDate dateOfDeath, LocalTime timeOfDeath, Address placeOfDeath) {
        this.dateOfDeath = dateOfDeath;
        this.timeOfDeath = timeOfDeath;
        this.placeOfDeath = placeOfDeath;
    }

    public LocalDate getDateOfDeath() {
        return dateOfDeath;
    }

    public void setDateOfDeath(LocalDate dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public LocalTime getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(LocalTime timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }


    /**
     * Combines the Date of Death and Time of Death into a LocalDateTime for easier use for other classes.
     * If Date of Death is null, Moment of Death is null
     * If only Time of Death is null, Moment of Death's time is set to 00:00
     */
    public LocalDateTime getMomentOfDeath() {
        LocalTime timeofDeath = getTimeOfDeath();
        if (timeofDeath == null) {
            timeofDeath = LocalTime.MIDNIGHT;
        }
        if (getDateOfDeath() != null) {
            return timeofDeath.atDate(getDateOfDeath());
        } else {
            return null;
        }
    }

    public Address getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setAddress(Address placeOfDeath) {
        this.placeOfDeath = placeOfDeath;
    }

    public String getCity() {
        return placeOfDeath.getCity();
    }

    public void setCity(String city) {
        placeOfDeath.setCity(city);
    }

    public String getRegion() {
        return placeOfDeath.getRegion();
    }

    public void setRegion(String region) {
        placeOfDeath.setRegion(region);
    }

    public String getCountry() {
        return placeOfDeath.getCountry();
    }

    public void setCountry(String country) {
        placeOfDeath.setCountry(country);
    }

    @Override
    public String toString() {
        return
                dateOfDeath + " " + '\n' +
                        timeOfDeath + " " + '\n' +
                        placeOfDeath;
    }
}
