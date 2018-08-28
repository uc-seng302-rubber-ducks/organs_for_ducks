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
