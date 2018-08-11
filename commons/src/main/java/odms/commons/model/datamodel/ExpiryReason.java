package odms.commons.model.datamodel;

import odms.commons.model.User;

import java.time.LocalDateTime;

/**
 * Stores all info for organ expiry.
 */
public class ExpiryReason {
    private String clinicianId;
    private LocalDateTime timeOrganExpired;
    private String reason;
    private OrgansWithExpiry organsWithExpiry;
    private User user;

    public ExpiryReason(String clinicianId, LocalDateTime dateTime, String reason, OrgansWithExpiry organsWithExpiry, User user) {
        this.clinicianId = clinicianId;
        this.timeOrganExpired = dateTime;
        this.reason = reason;
        this.organsWithExpiry = organsWithExpiry;
        organsWithExpiry.setExpiryReason(reason);
        organsWithExpiry.setHasExpired(true);
        this.user = user;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public LocalDateTime getTimeOrganExpired() {
        return timeOrganExpired;
    }

    public String getReason() {
        return reason;
    }

    public OrgansWithExpiry getOrgansWithExpiry() {
        return organsWithExpiry;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrgansWithExpiry(OrgansWithExpiry organsWithExpiry) {
        this.organsWithExpiry = organsWithExpiry;
    }
}
