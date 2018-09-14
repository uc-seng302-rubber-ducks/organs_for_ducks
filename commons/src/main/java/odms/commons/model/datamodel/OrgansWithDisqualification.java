package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDate;

public class OrgansWithDisqualification {

    private Organs organType;
    private String reason;
    private LocalDate date;
    private String staffId;

    public OrgansWithDisqualification(Organs organType, ExpiryReason disqualificationDetails) {
        this.organType = organType;
        this.reason = disqualificationDetails.getReason();
        this.date = disqualificationDetails.getTimeOrganExpired().toLocalDate();
        this.staffId = disqualificationDetails.getId();
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
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
