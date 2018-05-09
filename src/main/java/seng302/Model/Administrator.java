package seng302.Model;

import com.google.gson.annotations.Expose;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * class to model data structure for an Administrator.
 *
 * @author acb116
 */
public class Administrator {
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

    /**
     * Constructor to create a default Administrator
     */
    public Administrator(){
        userName = "default";
        this.firstName = null;
        this.middleName = null;
        this.lastName = null;
        password = setPassword("admin");
        dateCreated = LocalDateTime.now();
        dateLastModified = LocalDateTime.now();
    }

    /**
     * Constructor to create an Administrator with their personal details.
     * @param userName Administrator user name
     * @param firstName Administrator first name
     * @param middleName Administrator middle name
     * @param lastName Administrator last name
     * @param password Administrator password
     */
    public Administrator(String userName, String firstName, String middleName, String lastName, byte[] password){
        this.userName = userName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    private byte[] getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.salt = PasswordManager.getNextSalt;
        this.password = PasswordManager.hash(password, salt);
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void isExpectedPassword(String testPassword){
        return PasswordManager.isExpectedPassword(testPassword, this.salt, getPassword());
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
                ", password='" + password + '\'' +
                ", dateCreated=" + dateCreated +
                ", dateLastModified=" + dateLastModified +
                '}';
    }

}
