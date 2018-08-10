package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

/**
 * Used to populate the donation table
 */
public class OrgansWithExpiry {

    private Organs organType;
    private String progressBar;
    private boolean hasExpired;
    private String expiryReason;

    public OrgansWithExpiry(Organs organ) {
        this.organType = organ;
        this.progressBar = "temporary placeholder";
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

    public String getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(String progressBar) {
        this.progressBar = progressBar;
    }
}
