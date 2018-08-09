package odms.commons.model.datamodel;

import odms.commons.model._enum.Organs;

import java.time.LocalDateTime;

public class AvailableOrganDetail {
    private Organs organ;
    private String donorNhi;
    private LocalDateTime momentOfDeath;
    private String region;

    public AvailableOrganDetail(Organs organ, String nhi, LocalDateTime momentOfDeath, String region) {
        this.organ = organ;
        this.donorNhi = nhi;
        this.momentOfDeath = momentOfDeath;
        this.region = region;
    }

    public Organs getOrgan() {
        return organ;
    }

    public String getDonorNhi() {
        return donorNhi;
    }

    public LocalDateTime getMomentOfDeath() {
        return momentOfDeath;
    }

    public String getRegion() {
        return region;
    }

    public void setMomentOfDeath(LocalDateTime momentOfDeath) {
        this.momentOfDeath = momentOfDeath;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
