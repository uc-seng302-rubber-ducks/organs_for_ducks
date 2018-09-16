package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDate;

public class OrgansWithDisqualification {

    private Integer disqualifiedId;
    private Organs organType;
    private String reason;
    private LocalDate disqualificationDate;
    private LocalDate eligibleDate;
    private String staffId;

    public OrgansWithDisqualification(Organs organType, ExpiryReason disqualificationDetails) {
        this.organType = organType;
        this.reason = disqualificationDetails.getReason();
        this.disqualificationDate = disqualificationDetails.getTimeOrganExpired().toLocalDate();
        this.staffId = disqualificationDetails.getId();
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
}
