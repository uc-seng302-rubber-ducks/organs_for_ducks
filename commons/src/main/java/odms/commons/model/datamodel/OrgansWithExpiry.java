package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressBarService;

import java.time.LocalDateTime;

/**
 * Used to populate the donation table
 */
public class OrgansWithExpiry {

    private Organs organType;
    private transient ProgressBarService progressTask;
    private String expiryReason;

    public OrgansWithExpiry(Organs organ, LocalDateTime momentOfDeath) {
        this.organType = organ;
        this.progressTask = new ProgressBarService(momentOfDeath, organ);
        this.expiryReason = "";
    }

    public Organs getOrganType() {
        return organType;
    }

    public void setOrganType(Organs organType) {
        this.organType = organType;
    }

    public String getExpiryReason() {
        return expiryReason;
    }

    public void setExpiryReason(String expiryReason) {
        this.expiryReason = expiryReason;
    }
}
