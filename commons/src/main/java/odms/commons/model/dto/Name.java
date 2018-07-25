package odms.commons.model.dto;

/**
 * minimal holder for first, middle, and last names to fit api
 * @see UserOverview
 */
public class Name {
    private String firstName;
    private String middleNames;
    private String lastName;

    public Name(String firstName, String middleNames, String lastName) {
        this.firstName = firstName;
        this.middleNames = middleNames;
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {

        this.firstName = firstName;
    }

    public void setMiddleNames(String middleNames) {
        this.middleNames = middleNames;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleNames() {
        return middleNames;
    }

    public String getLastName() {
        return lastName;
    }
}
