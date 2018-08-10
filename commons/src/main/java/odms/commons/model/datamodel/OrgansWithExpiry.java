package odms.commons.model.datamodel;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressTask;

import java.time.LocalDateTime;

/**
 * Used to populate the donation table
 */
public class OrgansWithExpiry {

    private Organs organType;
    private Service progressTask;
    private boolean hasExpired;
    private String expiryReason;

    public OrgansWithExpiry(Organs organ, LocalDateTime momentOfDeath) {
        this.organType = organ;
        this.progressTask = new Service() {
            @Override
            protected Task createTask() {
                return new ProgressTask(momentOfDeath, organ);
            }
        };
        this.hasExpired = false;
        this.expiryReason = "";
    }

    public Organs getOrganType() {
        return organType;
    }

    public void setOrganType(Organs organType) {
        this.organType = organType;
    }

    public boolean isHasExpired() {
        return hasExpired;
    }

    public void setHasExpired(boolean hasExpired) {
        this.hasExpired = hasExpired;
    }

    public String getExpiryReason() {
        return expiryReason;
    }

    public void setExpiryReason(String expiryReason) {
        this.expiryReason = expiryReason;
    }
}
