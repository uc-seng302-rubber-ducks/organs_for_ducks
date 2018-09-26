package odms.commons.model.datamodel;

import java.util.Objects;

/**
 * Small class to put into a combo box. The toString method displays the name but the underlying value is the class.
 * This allows the id to be accessed
 */
public class ComboBoxClinician {

    private String name;
    private String id;

    /**
     * Constructor for the comboBoxClinicians
     * @param name Clinician's name
     * @param id Clinician's id
     */
    public ComboBoxClinician(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComboBoxClinician other = (ComboBoxClinician) o;
        return this.id.equals(other.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
