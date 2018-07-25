package odms.commons.model.datamodel;

import odms.commons.model._abstract.Deletable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Medication extends Deletable {

    private String medName;

    private List<LocalDateTime> medicationTimes;

    public Medication(String medName) {
        this(medName, new ArrayList<>());
    }

    public Medication(String medName, List<LocalDateTime> medicationTimes) {
        this.medName = medName;
        this.medicationTimes = medicationTimes;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public List<LocalDateTime> getMedicationTimes() {
        return medicationTimes;
    }

    public void setMedicationTimes(List<LocalDateTime> medicationTimes) {
        this.medicationTimes = medicationTimes;
    }

    public void addMedicationTime(LocalDateTime time) {
        this.medicationTimes.add(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medication that = (Medication) o;
        return Objects.equals(medName, that.medName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medName) + Objects.hash(medicationTimes) + 5;
    }
}
