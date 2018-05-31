package seng302.model;


import com.google.gson.annotations.Expose;
import seng302.model._abstract.Listenable;
import seng302.model._abstract.Undoable;
import seng302.model._enum.EventTypes;
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
 *
 * @author Josh Burt
 */
public class Clinician extends Undoable<Clinician> implements Listenable {

    @Expose
    private String staffId;
    @Expose
    private String workAddress;
    @Expose
    private String region;
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
     * @param workAddress clinician work address
     * @param region      clinician region
     */
    public Clinician(String staffId, String password, String firstName, String middleName, String lastName, String workAddress, String region) {
        this.staffId = staffId;
        setPassword(password);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.region = region;
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
     * @param workAddress      clinician work address
     * @param region           clinician region
     * @param password         clinician password
     * @param dateCreated      clinician date created
     * @param dateLastModified clinician date last modified
     */
    public Clinician(String name, String staffId, String workAddress, String region, String password, LocalDateTime dateCreated, LocalDateTime dateLastModified) {
        this.firstName = name;
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.region = region;
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
     * @param workAddress clinician work address
     * @param region      clinician region
     * @param password    clinician password
     */
    public Clinician(String name, String staffId, String workAddress, String region, String password) {
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.region = region;
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
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.firstName = name;
        addChange(new Change("set first name to " + name));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.middleName = name;
        addChange(new Change("set middle name to " + name));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.lastName = name;
        addChange(new Change("set last name to " + lastName));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
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
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.staffId = staffId;
        addChange(new Change("set staff id to " + staffId));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
    }


    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.workAddress = workAddress;
        addChange(new Change("set work address to " + workAddress));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.region = region;
        addChange(new Change("set region to " + region));
        memento.setNewObject(this.clone());
        getUndoStack().push(memento);
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
                ", workAddress='" + workAddress + '\'' +
                ", region='" + region + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }

    @Override
    public void undo() {
        if (getUndoStack().isEmpty()) {
            return;
        }
        Memento<Clinician> memento = getUndoStack().pop();
        this.changeInto(memento.getOldObject());
        getRedoStack().push(memento);
        addChange(new Change("undo"));
    }

    @Override
    public void redo() {
        if (getRedoStack().isEmpty()) {
            return;
        }
        Memento<Clinician> memento = getRedoStack().pop();
        this.changeInto(memento.getNewObject());
        getUndoStack().push(memento);
        addChange(new Change("redo"));
    }

    @Override
    public Clinician clone() {
        Clinician newClinician = new Clinician();
        newClinician.staffId = this.staffId;
        newClinician.password = this.password;
        newClinician.firstName = this.firstName;
        newClinician.middleName = this.middleName;
        newClinician.lastName = this.lastName;
        newClinician.workAddress = this.workAddress;
        newClinician.region = this.region;
        newClinician.dateCreated = this.dateCreated;
        newClinician.dateLastModified = this.dateLastModified;

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
        this.workAddress = clinician.workAddress;
        this.region = clinician.region;
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