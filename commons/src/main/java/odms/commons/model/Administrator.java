package odms.commons.model;

import com.google.gson.annotations.Expose;
import javafx.collections.FXCollections;
import odms.commons.model._abstract.IgnoreForUndo;
import odms.commons.model._abstract.Listenable;
import odms.commons.model._abstract.Undoable;
import odms.commons.model._enum.EventTypes;
import odms.commons.utils.PasswordManager;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * class to model data structure for an Administrator.
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
    private  String password;
    private  String salt;
    @Expose
    private LocalDateTime dateCreated;
    @Expose
    private LocalDateTime dateLastModified;
    private transient List<Change> changes; //NOSONAR
    private transient PropertyChangeSupport pcs; //NOSONAR
    //The two above have sonarlint disabled as they need to not be serailised


    /**
     * Constructor to create a default Administrator
     */
    public Administrator() {
        this.dateCreated = LocalDateTime.now();
        this.dateLastModified = LocalDateTime.now();
        this.changes = FXCollections.observableArrayList();
        this.pcs = new PropertyChangeSupport(this);
        this.firstName = "";
        this.middleName = "";
        this.lastName = "";
        setPassword("");
        this.userName = "";
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

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
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
            this.saveStateForUndo();
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
            this.saveStateForUndo();
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
            this.saveStateForUndo();
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
            this.saveStateForUndo();
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        salt = PasswordManager.getNextSalt();
        this.password = PasswordManager.hash(password, salt);
        addChange(new Change("Update password"));
    }

    public void setPassword(String password, String salt) {
        this.salt = salt;
        this.password = password;
        addChange(new Change("Update password"));
    }

    /**
     * Contract pre conditions:
     * Password must already be changed to the desired password
     * @param salt new salt
     */
    public void setSalt(String salt){
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
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

    @Override
    public void setDeleted(boolean deleted) {
        super.setDeleted(deleted);
        addChange(new Change("Deleted administrator"));
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
     * Saves the administrator's current state and pushes it onto the undo stack
     */
    private void saveStateForUndo() {
        //attempt to find out who called this method
        //if the caller is annotated with IgnoreForUndo, skip the memento/cloning process.
        try {
            //index 2 = direct caller - the setter methods
            //index 3 = level above that, i.e. whatever uses the setters
            Class callerClass = Class.forName(Thread.currentThread().getStackTrace()[3].getClassName());
            if (callerClass.isAnnotationPresent(IgnoreForUndo.class)) {
                return;
            }
        } catch (ClassNotFoundException ex) {
            //oh well, carry on as normal
        }
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
        addChange(new Change("undo")); //I think this should be here because it exists in clinician?
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
     * Returns the clone of the administrator
     * @param admin administrator to clone
     * @return a deep copy of the administrator given
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


