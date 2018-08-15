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
    private String id;
    private String name;
    private LocalDateTime expiryTime;
    private ExpiryReason expiryReason;

    public OrgansWithExpiry(Organs organType, ExpiryReason expiry, LocalDateTime momentOfDeath) {
        this.organType = organType;
        this.expiryReason = expiry;
        if (expiry.getTimeOrganExpired() != null) {
            reason = expiry.getReason();
            id = expiry.getId();
            expiryTime = expiry.getTimeOrganExpired();
            this.name = expiry.getName();

        } else {
            reason = "";
            name = "";
            id = "";
            expiryTime = null;
        }
        this.progressTask = new ProgressTask(this, momentOfDeath);
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

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
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


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
