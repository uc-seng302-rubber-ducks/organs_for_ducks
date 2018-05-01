package seng302.Model;

public class EmergencyContact {

    //declaring attributes
    private String name;
    private String HomePhoneNumber;
    private String CellPhoneNumber;
    private String address;
    private String region;
    private String Email;
    private String Relationship;
    private transient User attachedUser;


    public EmergencyContact(String Ename, String ECellPhone, User attachedUser) {
        name = Ename;
        HomePhoneNumber = null;
        CellPhoneNumber = ECellPhone;
        address = null;
        region = null;
        Email = null;
        Relationship = null;
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
        return HomePhoneNumber;
    }

    public void setHomePhoneNumber(String homePhoneNumber) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        ;
        HomePhoneNumber = homePhoneNumber;
        clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setNewObject(clone);
        attachedUser.getUndoStack().push(memento);
    }

    public String getCellPhoneNumber() {
        return CellPhoneNumber;
    }

    public void setCellPhoneNumber(String cellPhoneNumber) {
        Memento<User> memento = new Memento<>();
        User clone = attachedUser.clone();
        clone.setContact(copy(this));
        memento.setOldObject(clone);
        CellPhoneNumber = cellPhoneNumber;
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

        EmergencyContact newContact = new EmergencyContact(contact.name, contact.CellPhoneNumber,
            contact.attachedUser);
        newContact.address = contact.address;
        newContact.region = contact.region;
        newContact.HomePhoneNumber = contact.HomePhoneNumber;
        newContact.Email = contact.Email;
        newContact.Relationship = contact.Relationship;

        return newContact;
    }

}
