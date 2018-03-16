package seng302.Model;


/**
 * Class to model the data structure for a clinician
 *
 * @author Josh Burt
 *
 */
public class Clinician {

    private String name;
    private int staffId;
    private String workAddress;
    private String resion;
    private String password;

    public Clinician() {
    }


    public Clinician(String name, int staffId, String workAddress, String resion, String password) {
        this.name = name;
        this.staffId = staffId;
        this.workAddress = workAddress;
        this.resion = resion;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getResion() {
        return resion;
    }

    public void setResion(String resion) {
        this.resion = resion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
