package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

/**
 * Used to populate the donation table
 */
public class OrgansWithExpiry {

    private Organs organType;
    private String progressBar;

    public OrgansWithExpiry(Organs organ) {
        this.organType = organ;
        this.progressBar = "temporary placeholder";
    }

    public Organs getOrganType() {
        return organType;
    }

    public String getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(String progressBar) {
        this.progressBar = progressBar;
    }
}
