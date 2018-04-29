package seng302.Model;


import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class to model the data structure for a clinician
 *
 * @author Josh Burt
 *
 */
public class Clinician extends Undoable<Clinician> {

    @Expose
    private String name;
    @Expose
    private String staffId;
    @Expose
    private String workAddress;
    @Expose
    private String region;
    @Expose
    private String password;
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

    public Clinician() {
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
    }

    public Clinician(String staffId, String password, String firstName, String middleName, String lastName, String workAddress, String region) {
        this.staffId = staffId;
        this.password = password;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.workAddress = workAddress;
        this.region = region;
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();

        this.name = firstName; // todo: remove 'name'
    }


    public Clinician(String name, String staffId, String workAddress, String region, String password, LocalDateTime dateCreated, LocalDateTime dateLastModified) {
        this.name = name;
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.region = region;
        this.password = password;
        this.dateCreated = dateCreated;
        this.dateLastModified = dateLastModified;

    }

    public Clinician(String name, String staffId, String workAddress, String region, String password) {
        this.name = name;
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.region = region;
        this.password = password;
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.name = name;
        memento.setNewObject(this.clone());
      getUndoStack().push(memento);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.firstName = name;
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
        memento.setNewObject(this.clone());
      getUndoStack().push(memento);
    }


    public String getFullName() {
        String fullName;

        if (middleName != null && lastName != null) {
            fullName = firstName + " " + middleName  + " " + lastName;

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
        memento.setNewObject(this.clone());
      getUndoStack().push(memento);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Memento<Clinician> memento = new Memento<>();
        memento.setOldObject(this.clone());
        this.password = password;
        memento.setNewObject(this.clone());
      getUndoStack().push(memento);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clinician clinician = (Clinician) o;
        return staffId == clinician.staffId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(staffId);
    }

    @Override
    public String toString() {
        return "Clinician{" +
                "name='" + name + '\'' +
                ", staffId='" + staffId + '\'' +
                ", workAddress='" + workAddress + '\'' +
                ", region='" + region + '\'' +
                ", password='" + password + '\'' +
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
    }

    @Override
    public void redo() {
      if (getRedoStack().isEmpty()) {
            return;
        }
      Memento<Clinician> memento = getRedoStack().pop();
        this.changeInto(memento.getNewObject());
      getUndoStack().push(memento);
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
}
