package seng302.Model;

/**
 * Class for the Emergency Contact details for a user
 */
public class EmergencyContact {

    //declaring attributes
    private String name;
    private String homePhoneNumber;
    private String cellPhoneNumber;
    private String address;
    private String region;
    private String Email;
    private String Relationship;
    private transient User attachedUser;

    /**
     * Constructor for emergency contact
     * @param Ename Emergency contact name
     * @param ECellPhone Emergency contact phone number
     */
    public EmergencyContact(String Ename, String ECellPhone, User attachedUser) {
        name = Ename;
        homePhoneNumber = null;
        cellPhoneNumber = ECellPhone;
        address = null;
        region = null;
        Email = null;
        Relationship = null;
        this.attachedUser = attachedUser;
    }

    public EmergencyContact(String Ename, String ECellPhone, String homePhoneNumber, String region, String address, String email, String relationship, User attachedUser) {
        name = Ename;
        this.homePhoneNumber = homePhoneNumber;
        this.cellPhoneNumber = ECellPhone;
        this.address = address;
        this.Email = email;
        this.Relationship = relationship;
        this.region = region;
        this.attachedUser = attachedUser;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        this.region = region;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        Email = email;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
        ;
    }

    public String getRelationship() {
        return Relationship;
    }

    public void setRelationship(String relationship) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        Relationship = relationship;
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
                "Relationship: %s\n", name, homePhoneNumber, cellPhoneNumber, address, region, Email, Relationship);
    }

    /**
     * Checks for equality between two contacts
     * @param other other contact object
     * @return true if they are equal
     */
    @Override
    public boolean equals(Object other) {
        EmergencyContact otherContact = (EmergencyContact) other;

         return checkStrings(name, otherContact.name) &&
                 checkStrings(homePhoneNumber, otherContact.homePhoneNumber) &&
                 checkStrings(cellPhoneNumber, otherContact.cellPhoneNumber)
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

        EmergencyContact newContact = new EmergencyContact(contact.name, contact.cellPhoneNumber,
            contact.attachedUser);
        newContact.address = contact.address;
        newContact.region = contact.region;
        newContact.homePhoneNumber = contact.homePhoneNumber;
        newContact.Email = contact.Email;
        newContact.Relationship = contact.Relationship;

        return newContact;
    }

}
