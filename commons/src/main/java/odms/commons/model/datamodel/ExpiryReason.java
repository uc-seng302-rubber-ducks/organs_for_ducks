package odms.commons.model.datamodel;

import java.time.LocalDateTime;

public class ExpiryReason {
    private String clinicianId;
    private LocalDateTime timeOfExpiry;
    private String reason;

    public ExpiryReason(String clinicianId, LocalDateTime dateTime, String reason) {
        this.clinicianId = clinicianId;
        this.timeOfExpiry = dateTime;
        this.reason = reason;
    }

    public String getClinicianId() {
        return clinicianId;
    }

    public LocalDateTime getTimeOfExpiry() {
        return timeOfExpiry;
    }

    public String getReason() {
        return reason;
    }
}
