package seng302.Model;


import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Class to model the data structure for a clinician
 *
 * @author Josh Burt
 *
 */
public class Clinician {

    private String name;
    private String staffId;
    private String workAddress;
    private String region;
    private String password;
    private LocalDateTime dateCreated;
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
        this.name = name;
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
}
