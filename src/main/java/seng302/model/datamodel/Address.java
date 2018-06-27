package seng302.model.datamodel;

public class Address {

    private String streetNumber;
    private String streetName;
    private String neighbourhood;
    private String city;
    private String region;
    private String zipCode;
    private String country;

    public Address(String streetNumber, String streetName, String neighbourhood, String city, String region, String zipCode, String country) {
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.neighbourhood = neighbourhood;
        this.city = city;
        this.region = region;
        this.zipCode = zipCode;
        this.country = country;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStringAddress() {
        return streetNumber + " " + streetName + "\n" + neighbourhood;
    }


    @Override
    public String toString() {
        return
                streetNumber + " " +
                        streetName + " \n" +
                        neighbourhood + "\n" +
                        city + " " +
                        region + ' ' +
                        zipCode + "\n" +
                        country;
    }
}
