package odms.commons.model.datamodel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Class to store the death details of a donor
 */
public class DeathDetails {

    private LocalDateTime momentOfDeath;
    private Address placeOfDeath;

    public DeathDetails() {
        this.momentOfDeath = null;
        this.placeOfDeath = new Address("", "", "", "", "", "", "");
    }

    public DeathDetails(LocalDateTime momentOfDeath, Address placeOfDeath) {
        this.momentOfDeath = momentOfDeath;
        this.placeOfDeath = placeOfDeath;
    }

    public LocalDateTime getMomentOfDeath() {
        return momentOfDeath;
    }

    public void setMomentOfDeath(LocalDateTime momentOfDeath) {
        this.momentOfDeath = momentOfDeath;
    }

    /**
     * Uses the moment of death to return a LocalDate version, useful for date pickers
     *
     * @return LocalDate portion of moment of death
     */
    public LocalDate getDateOfDeath() {
        if (momentOfDeath != null) {
            return momentOfDeath.toLocalDate();
        } else {
            return null;
        }
    }

    /**
     * Uses the moment of death to return a LocalTime version, useful for date independent time calculations
     *
     * @return LocalTime portion of moment of death
     */
    public LocalTime getTimeOfDeath() {
        if (momentOfDeath != null) {
            return momentOfDeath.toLocalTime();
        } else {
            return null;
        }
    }

    /**
     * Combines a date and time into a LocalDateTime.
     * If Date of Death is null, Moment of Death is null
     * If only Time of Death is null, Moment of Death's time is set to 00:00
     *
     * @param date of death
     * @param time of death
     * @return LocalDateTime of death
     */
    public LocalDateTime createMomentOfDeath(LocalDate date, LocalTime time) {
        if (time == null) {
            time = LocalTime.MIDNIGHT;
        }
        if (date != null) {
            return time.atDate(date);
        } else {
            return null;
        }
    }

    public Address getPlaceOfDeath() {
        return placeOfDeath;
    }

    public void setDeathAddress(Address placeOfDeath) {
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
                momentOfDeath + " " + '\n' +
                        placeOfDeath;
    }
}
