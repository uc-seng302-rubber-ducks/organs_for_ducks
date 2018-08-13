package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressTask;

import java.time.LocalDateTime;

/**
 * Used to populate progress bar
 */
public class OrgansWithExpiry {

    private Organs organType;
    private transient ProgressTask progressTask; //NOSONAR
    private String reason;
    private String staffId;
    private LocalDateTime expiryTime;

    public OrgansWithExpiry(Organs organType, ExpiryReason expiry, LocalDateTime momentOfDeath) {
        this.organType = organType;
        ExpiryReason expiryReason = expiry;

        if (expiryReason.getTimeOrganExpired() != null) {
            reason = expiryReason.getReason();
            staffId = expiryReason.getClinicianId();
            expiryTime = expiryReason.getTimeOrganExpired();

        } else {
            reason = "";
            staffId = "";
            expiryTime = null;
            this.progressTask = new ProgressTask(this, momentOfDeath);
        }
    }

    public ProgressTask getProgressTask() {
        return progressTask;
    }

    public void setProgressTask(ProgressTask progressTask) {
        this.progressTask = progressTask;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }


    public Organs getOrganType() {
        return organType;
    }

    public void setOrganType(Organs organType) {
        this.organType = organType;
    }

    public String getExpiryReason() {
        return reason;
    }

    public void setExpiryReason(String expiryReason) {
        this.reason = expiryReason;
    }
}
