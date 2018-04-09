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
public class Clinician {

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

    public Clinician() {
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
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
        //this.workAddress = workAddress;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String name) {
        this.firstName = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String name) {
        this.middleName = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String name) {
        this.lastName = name;
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


    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
