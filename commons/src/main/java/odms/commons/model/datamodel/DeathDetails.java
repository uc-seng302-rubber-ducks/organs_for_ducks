package odms.commons.model.datamodel;

import java.time.LocalDateTime;

/**
 * Class to store the death details of a donor
 */
public class DeathDetails {

    private LocalDateTime timeOfDeath;
    private Address placeOfDeath;

    public DeathDetails() {
        this.timeOfDeath = LocalDateTime.now();
        this.placeOfDeath = new Address("", "", "", "", "", "", "");
    }

    public DeathDetails(LocalDateTime timeOfDeath, Address placeOfDeath) {
        this.timeOfDeath = timeOfDeath;
        this.placeOfDeath = placeOfDeath;
    }

    public LocalDateTime getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setTimeOfDeath(LocalDateTime timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
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
        return "DeathDetails{" +
                "timeOfDeath='" + timeOfDeath + '\'' +
                ", placeOfDeath='" + placeOfDeath + '\'' +
                '}';
    }
}
