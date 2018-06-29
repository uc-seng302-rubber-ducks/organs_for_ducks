package seng302.model.datamodel;


import seng302.model.User;

public class ContactDetails {


    private String homePhoneNumber;
    private String cellPhoneNumber;
    private Address address;
    private String email;
    private transient User attachedUser;

    public ContactDetails() {
        this.homePhoneNumber = "";
        this.cellPhoneNumber = "";
        this.address = new Address("", "", "", "", "", "", "");
        this.email = "";
        this.attachedUser = null;
    }

    public ContactDetails(String homePhoneNumber, String cellPhoneNumber, Address address, String email) {
        this.homePhoneNumber = homePhoneNumber;
        this.cellPhoneNumber = cellPhoneNumber;
        this.address = address;
        this.email = email;
    }


    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        this.homePhoneNumber = homePhoneNumber;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        this.cellPhoneNumber = cellPhoneNumber;
    }

    public User getAttachedUser() {
        return attachedUser;
    }

    public void setAttachedUser(User attachedUser) {
        this.attachedUser = attachedUser;
    }


    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreetNumber() {
        return address.getStreetNumber();
    }

    public void setStreetNumber(String streetNumber) {
        address.setStreetNumber(streetNumber);
    }

    public String getStreetName() {
        return address.getStreetName();
    }

    public void setStreetName(String streetName) {
        address.setStreetName(streetName);
    }

    public String getNeighborhood() {
        return address.getNeighborhood();
    }

    public void setNeighborhood(String neighborhood) {
        address.setNeighborhood(neighborhood);
    }

    public String getCity() {
        return address.getCity();
    }

    public void setCity(String city) {
        address.setCity(city);
    }

    public String getRegion() {
        return address.getRegion();
    }

    public void setRegion(String region) {
        address.setRegion(region);
    }

    public String getZipCode() {
        return address.getZipCode();
    }

    public void setZipCode(String zipCode) {
        address.setZipCode(zipCode);
    }

    public String getCountry() {
        return address.getCountry();
    }

    public void setCountry(String country) {
        address.setCountry(country);
    }



    @Override
    public String toString() {
        return "ContactDetails{" +
                "homePhoneNumber='" + homePhoneNumber + '\'' +
                ", cellPhoneNumber='" + cellPhoneNumber + '\'' +
                ", address=" + address +
                ", email='" + email + '\'' +
                ", attachedUser=" + attachedUser +
                '}';
    }


}
