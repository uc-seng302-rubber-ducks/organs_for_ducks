package odms.commons.model.datamodel;

import odms.commons.model._abstract.Listenable;
import odms.commons.model._enum.Organs;
import odms.commons.utils.ProgressTask;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.time.LocalDateTime;

/**
 * Used to populate progress bar
 */
public class OrgansWithExpiry implements Listenable {

    private PropertyChangeSupport pcs;
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
            expiryTime = calculateExpiryDate(momentOfDeath, organType);

        } else {
            reason = "";
            staffId = "";
            expiryTime = null;
            this.progressTask = new ProgressTask(this, momentOfDeath);
            this.pcs = new PropertyChangeSupport(this);
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

    /**
     * Returns an expiry date for an organ given a time of death and organ type
     *
     * @param timeOfDeath LocalDateTime of when the donor died
     * @param organType Organs enum of the type of organ
     * @return LocalDateTime of when the organ will expire
     */
    private LocalDateTime calculateExpiryDate(LocalDateTime timeOfDeath, Organs organType) {
        long expireTime = organType.getUpperBoundSeconds();
        return timeOfDeath.plusSeconds(expireTime);
    }

    public void setDone(boolean done) {
        if (done) {
            fire(new PropertyChangeEvent(this, "done", false, true));
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    @Override
    public void fire(PropertyChangeEvent event) {
        pcs.firePropertyChange(event);
    }
}
