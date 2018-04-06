package seng302.Model;


import org.joda.time.DateTime;

import java.util.Date;
import java.util.Objects;

/**
 * Class to model the data structure for a clinician
 *
 * @author Josh Burt
 *
 */
public class Clinician {

    private String firstName;
    private String middleName;
    private String lastName;
    private int staffId;
    private String workAddress;
    private String region;
    private String password;
    private DateTime dateCreated;
    private DateTime dateLastModified;

    public Clinician() {
        dateCreated = DateTime.now();
        dateLastModified = DateTime.now();
    }


    public Clinician(String name, int staffId, String workAddress, String region, String password, DateTime dateCreated, DateTime dateLastModified) {
        this.firstName = name;
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.region = region;
        this.password = password;
        this.dateCreated = dateCreated;
        this.dateLastModified = dateLastModified;

    }

    public Clinician(String name, int staffId, String region, String password) {
        this.firstName = name;
        this.staffId = staffId;
        //this.workAddress = workAddress;
        this.region = region;
        this.password = password;
        dateCreated = DateTime.now();
        dateLastModified = DateTime.now();
    }

    public DateTime getDateCreated() {
        return dateCreated;
    }

    public DateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(DateTime dateLastModified) {
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

    public int getStaffId() {
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
}
