package odms.commons.model.datamodel;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Class to form a key-pair for a MedicalProcedure Map.
 * The keys are name and date, though more may be added.
 */
public class ProcedureKey {
    private String name;
    private LocalDate date;

    public ProcedureKey(String procedureName, LocalDate procedureDate) {
        name = procedureName;
        date = procedureDate;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProcedureKey that = (ProcedureKey) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date);
    }
}
