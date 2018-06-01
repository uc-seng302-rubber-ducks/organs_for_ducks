package seng302.model.datamodel;


import seng302.model.User;

public class ContactDetails {


    public String homePhoneNumber;
    public String cellPhoneNumber;
    public Address address;
    public String email;
    public transient User attachedUser;

    public ContactDetails() {
        this.homePhoneNumber = null;
        this.cellPhoneNumber = null;
        this.address = null;
        this.email = null;
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

    public String getRegion() {
        return getAddress().getRegion();
    }

    public void setRegion(String region) {
        getAddress().setRegion(region);

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
