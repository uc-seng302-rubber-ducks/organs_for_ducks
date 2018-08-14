package odms.commons.model.datamodel;

import java.time.LocalDateTime;

/**
 * Stores all info for organ expiry.
 */
public class ExpiryReason {
    private String clinicianId;
    private LocalDateTime timeOrganExpired;
    private String reason;
    private String name;

    public ExpiryReason(String clinicianId, LocalDateTime dateTime, String reason, String name) {
        this.clinicianId = clinicianId;
        this.timeOrganExpired = dateTime;
        this.reason = reason;
        this.name = name;
    }

    public ExpiryReason() {
        this.clinicianId = "0";
        this.timeOrganExpired = null;
        this.reason = "";
        this.name = "";
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

    public String getName() {
        return name;
    }

}
