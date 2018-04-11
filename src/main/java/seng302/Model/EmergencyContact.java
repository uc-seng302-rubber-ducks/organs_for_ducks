package seng302.Model;

import javafx.scene.control.Cell;

public class EmergencyContact {

    //declaring attributes
    private String name;
    private String HomePhoneNumber;
    private String CellPhoneNumber;
    private String address;
    private String region;
    private String Email;
    private String Relationship;


    public EmergencyContact(String Ename, String ECellPhone){
        name = Ename;
        HomePhoneNumber = null;
        CellPhoneNumber = ECellPhone;
        address = null;
        region = null;
        Email = null;
        Relationship = null;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHomePhoneNumber() {
        return HomePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        HomePhoneNumber = homePhoneNumber;
    }

    public String getCellPhoneNumber() {
        return CellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        CellPhoneNumber = cellPhoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        Relationship = relationship;
    }

    public String toString() {
        return String.format("Emergency Contact Details: \n" +
                "Name: %s\n" +
                "Home Phone: %s\n" +
                "Cell Phone: %s\n" +
                "Address: %s\n" +
                "Region: %s\n" +
                "Email: %s\n" +
                "Relationship: %s\n", name, HomePhoneNumber, CellPhoneNumber, address, region, Email, Relationship);
    }

    @Override
    public boolean equals(Object other) {
        EmergencyContact otherContact = (EmergencyContact) other;

         return checkStrings(name, otherContact.name) &&
                 checkStrings(HomePhoneNumber, otherContact.HomePhoneNumber) &&
                 checkStrings(CellPhoneNumber, otherContact.CellPhoneNumber)
                 && checkStrings(Email, otherContact.Email) &&
                 checkStrings(address, otherContact.address) &&
                 checkStrings(region, otherContact.region) &&
                 checkStrings(Relationship, otherContact.Relationship);
    }

    /**
     * Compares strings
     * @param string1 The first string.
     * @param string2 the second string.
     * @return a boolean.
     */
    private boolean checkStrings(String string1, String string2) {
        if (string1 == null || string2 == null) {
             return (string1 == string2);
        } else {
            return string1.toLowerCase().equals(string2.toLowerCase());
        }
    }

    public static EmergencyContact copy(EmergencyContact contact) {
        if (contact == null) {
            return null;
        }

        EmergencyContact newContact = new EmergencyContact(contact.name, contact.CellPhoneNumber);
        newContact.address = contact.address;
        newContact.region = contact.region;
        newContact.HomePhoneNumber = contact.HomePhoneNumber;
        newContact.Email = contact.Email;
        newContact.Relationship = contact.Relationship;

        return newContact;
    }

}
