package odms.commons.model.datamodel;

/**
 * Small class to put into a combo box. The toString method displays the name but the underlying value is the class.
 * This allows the id to be accessed
 */
public class ComboBoxClinician {

    private String name;
    private int id;

    /**
     * Constructor for the comboBoxClinicians
     * @param name Clinician's name
     * @param id Clinician's id
     */
    public ComboBoxClinician(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }
}
