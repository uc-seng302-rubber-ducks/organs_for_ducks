package odms.commons.model.datamodel;


import odms.commons.model._enum.Organs;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Attaches an organ with its registration date to allow it to be shown in a table view
 */
public class OrgansWithDates {

    private LocalDate latestRegistration;
    private Organs organName;

    public OrgansWithDates(Organs organName, LocalDate latestRegistration) {
        this.latestRegistration = latestRegistration;
        this.organName = organName;
    }

    public LocalDate getLatestRegistration() {
        return latestRegistration;
    }

    public void setLatestRegistration(LocalDate latestRegistration) {
        this.latestRegistration = latestRegistration;
    }

    public Organs getOrganName() {
        return organName;
    }

    public void setOrganName(Organs organName) {
        this.organName = organName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgansWithDates that = (OrgansWithDates) o;
        return organName == that.organName;
    }

    @Override
    public int hashCode() {

        return Objects.hash(organName);
    }
}
