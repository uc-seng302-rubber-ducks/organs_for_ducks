package seng302.model;

import seng302.model.datamodel.Address;
import seng302.model.datamodel.ContactDetails;

import java.util.Objects;

/**
 * Class for the Emergency Contact details for a user
 */
public class EmergencyContact extends ContactDetails {

    //declaring attributes
    private String name;
    private String relationship;


    /**
     * Constructor for emergency contact
     */
    public EmergencyContact(String name, String relationship, String cell) {
        super(null, cell, new Address("", "", "", "", "", "", ""), null);
        this.name = name;
        this.relationship = relationship;
    }


    //TODO: Simplify constructor to take less arguments 17/05/18
    public EmergencyContact(String ename, String eCellPhone, String homePhoneNumber, Address address, String email, String relationship) {
        name = ename;
        this.homePhoneNumber = homePhoneNumber;
        this.cellPhoneNumber = eCellPhone;
        this.email = email;
        this.relationship = relationship;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.name = name;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getHomePhoneNumber() {
        return homePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.homePhoneNumber = homePhoneNumber;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.cellPhoneNumber = cellPhoneNumber;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.address = address;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.email = email;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.relationship = relationship;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public User getAttachedUser() {
        return attachedUser;
    }

    public void setAttachedUser(User attachedUser) {
        this.attachedUser = attachedUser;
    }

    public String toString() {
        return String.format("Emergency Contact Details: \n" +
                "Name: %s\n" +
                "Home Phone: %s\n" +
                "Cell Phone: %s\n" +
                "Address: %s\n" +
                "Region: %s\n" +
                "Email: %s\n" +
                "Relationship: %s\n", name, homePhoneNumber, cellPhoneNumber, address, getRegion(), email, relationship);
    }

    public static EmergencyContact copy(EmergencyContact contact) {
        if (contact == null) {
            return null;
        }

        EmergencyContact newContact = new EmergencyContact(contact.getName(), contact.getRelationship(), contact.getCellPhoneNumber());
        newContact.address = contact.address;
        newContact.homePhoneNumber = contact.homePhoneNumber;
        newContact.email = contact.email;
        newContact.relationship = contact.relationship;

        return newContact;
    }

    /**
     * Compares strings
     *
     * @param string1 The first string.
     * @param string2 the second string.
     * @return a boolean.
     */
    private boolean checkStrings(String string1, String string2) {
        return string1 != null && string2 != null && string1.equalsIgnoreCase(string2);
    }

    /**
     * Checks for equality between two contacts
     *
     * @param other other contact object
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object other) {

        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        EmergencyContact otherContact = (EmergencyContact) other;
        return checkStrings(name, otherContact.name) &&
                checkStrings(homePhoneNumber, otherContact.homePhoneNumber) &&
                checkStrings(cellPhoneNumber, otherContact.cellPhoneNumber)
                && checkStrings(email, otherContact.email) &&
                //checkStrings(address, otherContact.address) &&
                checkStrings(relationship, otherContact.relationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, homePhoneNumber, cellPhoneNumber, address, email, relationship);
    }

}
