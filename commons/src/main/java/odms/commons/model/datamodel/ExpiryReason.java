package odms.commons.model.datamodel;

import java.time.LocalDateTime;

/**
 * Stores all info for organ expiry.
 */
public class ExpiryReason {
    private String clinicianId;
    private LocalDateTime timeOrganExpired;
    private String reason;

    public ExpiryReason(String clinicianId, LocalDateTime dateTime, String reason) {
        this.clinicianId = clinicianId;
        this.timeOrganExpired = dateTime;
        this.reason = reason;
    }

    public ExpiryReason() {
        this.clinicianId = "";
        this.timeOrganExpired = null;
        this.reason = "";
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
}
