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
    private transient User attachedUser;


    /**
     * Constructor for emergency contact
     */
    public EmergencyContact(String name, String relationship, String cell) {
        super("", cell, new Address("", "", "", "", "", "", ""), "");
        this.name = name;
        this.relationship = relationship;
    }


    //TODO: Simplify constructor to take less arguments 17/05/18
    public EmergencyContact(String ename, String eCellPhone, String homePhoneNumber, Address address, String email, String relationship) {
        name = ename;
        super.setAddress(address);
        super.setHomePhoneNumber(homePhoneNumber);
        super.setCellPhoneNumber(eCellPhone);
        super.setEmail(email);
        this.relationship = relationship;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        //Memento<User> memento = new Memento<>();
        //User clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setOldObject(clone);
        this.name = name;
        //clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setNewObject(clone);
        //super.getAttachedUser().getUndoStack().push(memento);
    }

    public String getHomePhoneNumber() {
        return super.getHomePhoneNumber();
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        //Memento<User> memento = new Memento<>();
        //User clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setOldObject(clone);
        super.setHomePhoneNumber(homePhoneNumber);
        //clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setNewObject(clone);
        //super.getAttachedUser().getUndoStack().push(memento);
    }

    public String getCellPhoneNumber() {
        return super.getCellPhoneNumber();
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        //Memento<User> memento = new Memento<>();
        //User clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setOldObject(clone);
        super.setCellPhoneNumber(cellPhoneNumber);
        //clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setNewObject(clone);
        //super.getAttachedUser().getUndoStack().push(memento);
    }

    public Address getAddress() {
        return super.getAddress();
    }

    public void setAddress(Address address) {
        //Memento<User> memento = new Memento<>();
        //User clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setOldObject(clone);
        super.setAddress(address);
        //clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
        //memento.setNewObject(clone);
        //super.getAttachedUser().getUndoStack().push(memento);
    }

    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        //Memento<User> memento = new Memento<>();
        //User clone = super.getAttachedUser().clone();
        //clone.setContact(copy(this));
//        memento.setOldObject(clone);
        super.setEmail(email);
//        clone = super.getAttachedUser().clone();
//        clone.setContact(copy(this));
//        memento.setNewObject(clone);
//        super.getAttachedUser().getUndoStack().push(memento);
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
//        Memento<User> memento = new Memento<>();
//        User clone = super.getAttachedUser().clone();
//        clone.setContact(copy(this));
//        memento.setOldObject(clone);
        this.relationship = relationship;
//        clone = super.getAttachedUser().clone();
//        clone.setContact(copy(this));
//        memento.setNewObject(clone);
//        super.getAttachedUser().getUndoStack().push(memento);
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
                "Relationship: %s\n", name, super.getHomePhoneNumber(), super.getCellPhoneNumber(), super.getAddress(), getRegion(), super.getEmail(), relationship);
    }

    public static EmergencyContact copy(EmergencyContact contact) {
        if (contact == null) {
            return null;
        }

        EmergencyContact newContact = new EmergencyContact(contact.getName(), contact.getRelationship(), contact.getCellPhoneNumber());
        newContact.setAddress(contact.getAddress());
        newContact.setHomePhoneNumber(contact.getHomePhoneNumber());
        newContact.setEmail(contact.getEmail());
        newContact.setRelationship(contact.getRelationship());

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
                checkStrings(this.getHomePhoneNumber(), otherContact.getHomePhoneNumber()) &&
                checkStrings(this.getCellPhoneNumber(), otherContact.getCellPhoneNumber())
                && checkStrings(this.getEmail(), otherContact.getEmail()) &&
                //checkStrings(address, otherContact.address) &&
                checkStrings(relationship, otherContact.relationship);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, super.getHomePhoneNumber(), super.getCellPhoneNumber(), super.getAddress(), super.getEmail(), relationship);
    }

}
