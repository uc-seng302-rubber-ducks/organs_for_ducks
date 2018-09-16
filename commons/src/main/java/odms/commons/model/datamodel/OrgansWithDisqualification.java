package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDate;

public class OrgansWithDisqualification {

    private Integer disqualifiedId;
    private Organs organType;
    private String reason;
    private LocalDate dateDisqualified;
    private LocalDate dateEligible;
    private String staffId;

    public OrgansWithDisqualification(Organs organType, String reason, LocalDate dateDisqualified, String staffId) {
        this.organType = organType;
        this.reason = reason;
        this.dateDisqualified = dateDisqualified;
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

    public LocalDate getDateDisqualified() {
        return dateDisqualified;
    }

    public void setDateDisqualified(LocalDate date) {
        this.dateDisqualified = date;
    }

    public LocalDate getDateEligible() {
        return dateEligible;
    }

    public void setDateEligible(LocalDate dateEligible) {
        this.dateEligible = dateEligible;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
}
