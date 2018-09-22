package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;
import odms.commons.utils.Log;

import java.time.LocalDate;
import java.util.Objects;

public class OrgansWithDisqualification {

    private Integer disqualifiedId;
    private Organs organType;
    private String reason;
    private LocalDate disqualificationDate;
    private LocalDate eligibleDate;
    private String staffId;
    private boolean isCurrentlyDisqualified = true;

    public OrgansWithDisqualification(Organs organType, String reason, LocalDate disqualificationDate, String staffId) {
        this.organType = organType;
        this.reason = reason;
        this.disqualificationDate = disqualificationDate;
        this.staffId = staffId;
    }

    public Integer getDisqualifiedId() {
        return disqualifiedId;
    }

    public void setDisqualifiedId(Integer disqualifiedId) {
        this.disqualifiedId = disqualifiedId;
    }

    public Organs getOrganType() {
        return organType;
    }

    public void setOrganType(Organs organType) {
        this.organType = organType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDate getDate() {
        return disqualificationDate;
    }

    public void setDate(LocalDate date) {
        this.disqualificationDate = date;
    }

    public LocalDate getEligibleDate() {
        return eligibleDate;
    }

    public void setEligibleDate(LocalDate eligibleDate) {
        this.eligibleDate = eligibleDate;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public boolean isCurrentlyDisqualified() {
        return isCurrentlyDisqualified;
    }

    public void setCurrentlyDisqualified(boolean currentlyDisqualified) {
        isCurrentlyDisqualified = currentlyDisqualified;
    }

    @Override
    public String toString() {
        return "OrgansWithDisqualification{" +
                "organType=" + organType +
                ", reason='" + reason + '\'' +
                ", disqualificationDate=" + disqualificationDate +
                ", eligibleDate=" + eligibleDate +
                ", staffId='" + staffId + '\'' +
                '}';
    }

    @Override
    public int hashCode() {

        return Objects.hash(disqualifiedId, organType, reason, disqualificationDate, staffId, eligibleDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgansWithDisqualification organ = (OrgansWithDisqualification) o;
        if (disqualifiedId == null || organ.disqualifiedId == null) {
            Log.warning("Trying to compare OrgansWithDisqualifications when at least one does not have a unique id. Comparison failed.");
            return false;
        }
        return disqualifiedId.equals(organ.disqualifiedId);

    }

}
