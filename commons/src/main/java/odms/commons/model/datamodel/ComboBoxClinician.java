package odms.commons.model.datamodel;

/**
 * Small class to put into a combo box. The toString method displays the name but the underlying value is the class.
 * This allows the id to be accessed
 */
public class ComboBoxClinician {

    private String name;
    private String id;

    /**
     * Empty constructor for the comboBoxClinicians
     */
    public ComboBoxClinician() {
        //This class is only every constructed from an sql result, so is done incrementally
    }

    /**
     * Constructor for combo-box clinician
     * @param name Name of clinician
     * @param id id of clinician
     */
    public ComboBoxClinician(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
