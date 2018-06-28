package seng302.model;

import com.google.gson.annotations.Expose;
import javafx.collections.FXCollections;
import seng302.model._abstract.Listenable;
import seng302.model._abstract.Undoable;
import seng302.model._enum.EventTypes;
import seng302.utils.PasswordManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * class to model data structure for an Administrator.
 *
 * @author acb116
 */
public class Administrator extends Undoable<Administrator> implements Listenable {
    @Expose
    private String userName;
    @Expose
    private String firstName;
    @Expose
    private String middleName;
    @Expose
    private String lastName;
    @Expose
    private byte[] password;
    @Expose
    private byte[] salt;
    @Expose
    private LocalDateTime dateCreated;
    @Expose
    private LocalDateTime dateLastModified;
    private transient List<Change> changes;
    private transient PropertyChangeSupport pcs;


    /**
     * Constructor to create a default Administrator
     */
    public Administrator() {
        this.dateCreated = LocalDateTime.now();
        this.dateLastModified = LocalDateTime.now();
        this.changes = FXCollections.observableArrayList();
        this.pcs = new PropertyChangeSupport(this);
    }

    /**
     * Constructor to create an Administrator with their personal details.
     *
     * @param userName   Administrator user name
     * @param firstName  Administrator first name
     * @param middleName Administrator middle name
     * @param lastName   Administrator last name
     * @param password   Administrator password
     */
    public Administrator(String userName, String firstName, String middleName, String lastName, String password) {
        this.changes = FXCollections.observableArrayList();
        this.pcs = new PropertyChangeSupport(this);
        this.userName = userName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        setPassword(password);
        this.dateCreated = LocalDateTime.now();
        this.dateLastModified = LocalDateTime.now();
    }


    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
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

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        if (!userName.equals(this.userName)) {
            this.saveStateforUndo();
            this.userName = userName;
            addChange(new Change("Updated username to " + userName));
            setDateLastModified(LocalDateTime.now());
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!firstName.equals(this.firstName)) {
            this.saveStateforUndo();
            this.firstName = firstName;
            addChange(new Change("Updated first name to " + firstName));
            setDateLastModified(LocalDateTime.now());
        }
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        if (!middleName.equals(this.middleName)) {
            this.saveStateforUndo();
            this.middleName = middleName;
            addChange(new Change("Updated middle name to " + middleName));
            setDateLastModified(LocalDateTime.now());
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!lastName.equals(this.lastName)) {
            this.saveStateforUndo();
            this.lastName = lastName;
            addChange(new Change("Updated last name to " + lastName));
            setDateLastModified(LocalDateTime.now());
        }
    }

    public String getFullName() {
        String fullName;

        if (middleName != null && lastName != null) {
            fullName = firstName + " " + middleName + " " + lastName;

        } else if (middleName != null) {
            fullName = firstName + " " + middleName;

        } else if (lastName != null) {
            fullName = firstName + " " + lastName;

        } else if (firstName.isEmpty()) {
            fullName = "";
        } else {
            fullName = firstName;
        }

        return fullName;
    }

    private byte[] getPassword() {
        return password;
    }

    public void setPassword(String password) {
        salt = PasswordManager.getNextSalt();
        this.password = PasswordManager.hash(password, salt);
        addChange(new Change("Update password"));

    }

    public List<Change> getChanges() {
        return changes;
    }

    public void setChanges(List<Change> changes) {
        this.changes = changes;
    }

    public void addChange(Change change) {
        changes.add(change);
        this.fire(
                new PropertyChangeEvent(this, EventTypes.USER_UPDATE.name(), new Object(), new Object()));
    }

    public PropertyChangeSupport getPcs() {
        return pcs;
    }

    public void setPcs(PropertyChangeSupport pcs) {
        this.pcs = pcs;
    }

    /**
     * Takes an attempt as a password and then checks it against the actual password
     *
     * @param passwordAttempt password guess
     * @return correctness of the guess
     */
    public boolean isPasswordCorrect(String passwordAttempt) {
        return PasswordManager.isExpectedPassword(passwordAttempt, salt, getPassword());
    }


    /**
     * EWWWW gross but please forgive me. dont want the search to break just yet. the generic search requires a region
     * so here we are
     *
     * @return does the needful
     */
    public String getRegion() {
        return "";
    }

    private void saveStateforUndo() { //New
        Memento<Administrator> memento = new Memento<>(Administrator.clone(this));
        getUndoStack().push(memento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Administrator administrator = (Administrator) o;
        return userName.equals(administrator.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userName);
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "userName='" + userName + '\'' +
                ", name='" + getFullName() + '\'' +
                ", dateCreated=" + dateCreated.toString() +
                ", dateLastModified=" + dateLastModified.toString() +
                '}';
    }

    @Override
    public void undo() {
        if (getUndoStack().isEmpty()) { //DO nothing if stack is empty
            return;
        }
        getRedoStack().push(new Memento<>(clone(this))); // put current state onto redo stack
        Memento<Administrator> memento = getUndoStack().pop(); //Get the top of the undo stack
        this.changeInto(memento.getState()); //Change current state to be top undo state
        //addChange(new Change("undo")); //I think this should be here because it exists in clinician?
    }

    @Override
    public void redo() {
        if (getRedoStack().isEmpty()) {
            return;
        }
        getUndoStack().push(new Memento<>(clone(this)));
        Memento<Administrator> memento = getRedoStack().pop();
        this.changeInto(memento.getState());
    }


    /**
     * Could this and changeInto be combined somehow?
     */
    public static Administrator clone(Administrator admin) {
        Administrator newAdmin = new Administrator();
        newAdmin.userName = admin.userName;
        newAdmin.firstName = admin.firstName;
        newAdmin.middleName = admin.middleName;
        newAdmin.lastName = admin.lastName;
        newAdmin.password = admin.password;
        newAdmin.salt = admin.salt;
        newAdmin.dateCreated = admin.dateCreated;
        newAdmin.dateLastModified = admin.dateLastModified;
        return newAdmin;
    }

    /**
     * Changes the administrators attributes into that of another administrator.
     *
     * @param admin The administrator to turn into
     */
    private void changeInto(Administrator admin) {
        this.userName = admin.userName;
        this.firstName = admin.firstName;
        this.middleName = admin.middleName;
        this.lastName = admin.lastName;
        this.password = admin.password;
        this.salt = admin.salt;
        this.dateCreated = admin.dateCreated;
        this.dateLastModified = admin.dateLastModified;
    }
}


