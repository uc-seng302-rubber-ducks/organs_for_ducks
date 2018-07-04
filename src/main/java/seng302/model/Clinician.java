package seng302.model;


import com.google.gson.annotations.Expose;
import seng302.model._abstract.Listenable;
import seng302.model._abstract.Undoable;
import seng302.model._enum.EventTypes;
import seng302.model.datamodel.Address;
import seng302.model.datamodel.ContactDetails;
import seng302.utils.PasswordManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class to model the data structure for a clinician
 */
public class Clinician extends Undoable<Clinician> implements Listenable {

    @Expose
    private String staffId;
    @Expose
    private ContactDetails workContactDetails;
    @Expose
    private byte[] password;

    @Expose
    private LocalDateTime dateCreated;
    @Expose
    private LocalDateTime dateLastModified;

    @Expose
    private String firstName;
    @Expose
    private String middleName;
    @Expose
    private String lastName;
    @Expose
    private byte[] salt;
    private transient PropertyChangeSupport pcs;

    //TODO make all updates to the clinician add to this 22/6
    private transient List<Change> changes;

    public Clinician() {
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
        changes = new ArrayList<>();
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Constructor for Clinician
     *
     * @param staffId     clinician staff id
     * @param password    clinician password
     * @param firstName   clinician first name
     * @param middleName  clinician middle name
     * @param lastName    clinician last name
     */
    public Clinician(String staffId, String password, String firstName, String middleName, String lastName) {
        this.staffId = staffId;
        setPassword(password);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.workContactDetails = new ContactDetails();
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
        changes = new ArrayList<>();
        this.pcs = new PropertyChangeSupport(this);

    }


    /**
     * Constructor for Clinician
     *
     * @param name             clinician name
     * @param staffId          clinician staff id
     * @param password         clinician password
     * @param dateCreated      clinician date created
     * @param dateLastModified clinician date last modified
     */
    public Clinician(String name, String staffId, String password, LocalDateTime dateCreated, LocalDateTime dateLastModified) {
        this.firstName = name;
        this.staffId = staffId;
        this.workContactDetails = new ContactDetails();
        setPassword(password);
        this.dateCreated = dateCreated;
        this.dateLastModified = dateLastModified;
        changes = new ArrayList<>();
        this.pcs = new PropertyChangeSupport(this);

    }

    /**
     * Constructor for Clinician
     *
     * @param name        clinician name
     * @param staffId     clinician staff id
     * @param password    clinician password
     */
    public Clinician(String name, String staffId, String password) {
        this.staffId = staffId;
        this.workContactDetails = new ContactDetails();
        this.firstName = name;
        this.middleName = "";
        this.lastName = "";
        setPassword(password);
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
        changes = new ArrayList<>();
        this.pcs = new PropertyChangeSupport(this);
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.saveStateforUndo();
        this.firstName = name;
        addChange(new Change("set first name to " + name));
        setDateLastModified(LocalDateTime.now());
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        this.saveStateforUndo();
        this.middleName = name;
        addChange(new Change("set middle name to " + name));
        setDateLastModified(LocalDateTime.now());
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.saveStateforUndo();
        this.lastName = name;
        addChange(new Change("set last name to " + lastName));
        setDateLastModified(LocalDateTime.now());
    }


    public String getFullName() {
        String fullName;

        if (middleName != null && lastName != null) {
            fullName = firstName + " " + middleName + " " + lastName;

        } else if (middleName != null) {
            fullName = firstName + " " + middleName;

        } else if (lastName != null) {
            fullName = firstName + " " + lastName;

        } else {
            fullName = firstName;
        }

        return fullName;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.saveStateforUndo();
        this.staffId = staffId;
        addChange(new Change("set staff id to " + staffId));
        setDateLastModified(LocalDateTime.now());
    }

    public ContactDetails getWorkContactDetails() {
        return workContactDetails;
    }

    public void setWorkContactDetails(ContactDetails workContactDetails) {
        this.workContactDetails = workContactDetails;
    }

    public String getStreetNumber() {
        return workContactDetails.getStreetNumber();
    }

    public void setStreetNumber(String streetNumber) {
        this.saveStateforUndo();
        workContactDetails.setStreetNumber(streetNumber);
        addChange(new Change("set street number to " + streetNumber));
        setDateLastModified(LocalDateTime.now());
    }

    public String getStreetName() {
        return workContactDetails.getStreetName();
    }

    public void setStreetName(String streetName) {
        this.saveStateforUndo();
        workContactDetails.setStreetName(streetName);
        addChange(new Change("set street name to " + streetName));
        setDateLastModified(LocalDateTime.now());
    }

    public String getNeighborhood() {
        return workContactDetails.getNeighborhood();
    }

    public void setNeighborhood(String neighborhood) {
        this.saveStateforUndo();
        workContactDetails.setNeighborhood(neighborhood);
        addChange(new Change("set neighborhood to " + neighborhood));
        setDateLastModified(LocalDateTime.now());
    }

    public String getCity() {
        return workContactDetails.getCity();
    }

    public void setCity(String city) {
        this.saveStateforUndo();
        workContactDetails.setCity(city);
        addChange(new Change("set city to " + city));
        setDateLastModified(LocalDateTime.now());
    }

    public String getZipCode() {
        return workContactDetails.getZipCode();
    }

    public void setZipCode(String zipCode) {
        this.saveStateforUndo();
        workContactDetails.setZipCode(zipCode);
        addChange(new Change("set zip code to " + zipCode));
        setDateLastModified(LocalDateTime.now());
    }

    public String getCountry() {
        return workContactDetails.getCountry();
    }

    public void setCountry(String country) {
        this.saveStateforUndo();
        workContactDetails.setCountry(country);
        addChange(new Change("set country to " + country));
        setDateLastModified(LocalDateTime.now());
    }

    public String getRegion() {
        return workContactDetails.getRegion();
    }

    public void setRegion(String region) {
        this.saveStateforUndo();
        workContactDetails.setRegion(region);
        addChange(new Change("set region to " + region));
        setDateLastModified(LocalDateTime.now());
    }

    /**
     * Private setter as no one should be able to retrieve password outside of the class
     *
     * @return hash of the password
     */
    private byte[] getPassword() {
        return password;
    }


    /**
     * updates the password by hashing it and storing the new salt
     *
     * @param password plaintext password to be hashed
     */
    public void setPassword(String password) {
        this.salt = PasswordManager.getNextSalt();
        this.password = PasswordManager.hash(password, salt);
    }


    public byte[] getSalt() {
        return salt;
    }


    /**
     * A function to check the supplied password against the stored hash.
     *
     * @param password Password to be checked
     * @return correctness of the password
     */
    public boolean isPasswordCorrect(String password) {
        return PasswordManager.isExpectedPassword(password, salt, getPassword());
    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public void addChange(Change change) {
        changes.add(change);
        this.fire(new PropertyChangeEvent(this, EventTypes.CLINICIAN_UPDATE.name(), new Object(),
                new Object()));
    }

    private void saveStateforUndo() {
        Memento<Clinician> memento = new Memento<>(Clinician.clone(this));
        getUndoStack().push(memento);
    }

    @Override
    public void setDeleted(boolean deleted) {
        super.setDeleted(deleted);
        addChange(new Change("Deleted clinician"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clinician clinician = (Clinician) o;
        return staffId.equals(clinician.staffId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(staffId);
    }

    @Override
    public String toString() {
        return "Clinician{" +
                "name='" + getFullName() + '\'' +
                ", staffId='" + staffId + '\'' +
                ", workContactDetails=" + workContactDetails + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }

    @Override
    public void undo() {
        if (getUndoStack().isEmpty()) {
            return;
        }
        getRedoStack().push(new Memento<>(clone(this))); // put current state onto redo stack
        Memento<Clinician> memento = getUndoStack().pop(); //Get the top of the undo stack
        this.changeInto(memento.getState()); //Change current state to be top undo state
        addChange(new Change("undo"));
    }

    @Override
    public void redo() {
        if (getRedoStack().isEmpty()) {
            return;
        }
        getUndoStack().push(new Memento<>(clone(this)));
        Memento<Clinician> memento = getRedoStack().pop();
        this.changeInto(memento.getState());
        addChange(new Change("redo"));
    }


    public static Clinician clone(Clinician clinician) {
        Clinician newClinician = new Clinician();
        newClinician.staffId = clinician.staffId;
        newClinician.password = clinician.password;
        newClinician.firstName = clinician.firstName;
        newClinician.middleName = clinician.middleName;
        newClinician.lastName = clinician.lastName;

        Address workAddress = new Address(clinician.getStreetNumber(), clinician.getStreetName(), clinician.getNeighborhood(), clinician.getCity(), clinician.getRegion(), clinician.getZipCode(), clinician.getCountry());
        newClinician.workContactDetails = new ContactDetails("", "", workAddress, "");;

        newClinician.dateCreated = clinician.dateCreated;
        newClinician.dateLastModified = clinician.dateLastModified;

        return newClinician;
    }

    /**
     * changes the attributes of the clinician into that of another clinician
     *
     * @param clinician Clinician object to turn into
     */
    private void changeInto(Clinician clinician) {
        this.staffId = clinician.staffId;
        this.password = clinician.password;
        this.firstName = clinician.firstName;
        this.middleName = clinician.middleName;
        this.lastName = clinician.lastName;
        this.workContactDetails = clinician.workContactDetails;
        this.dateCreated = clinician.dateCreated;
        this.dateLastModified = clinician.dateLastModified;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (this.pcs == null) {
            this.pcs = new PropertyChangeSupport(this);
        }
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void fire(PropertyChangeEvent event) {
        this.pcs.firePropertyChange(event);
    }
}
