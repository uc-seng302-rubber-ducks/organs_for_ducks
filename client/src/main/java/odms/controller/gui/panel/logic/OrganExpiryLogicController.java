package odms.controller.gui.panel.logic;

import odms.commons.model.datamodel.ExpiryReason;
import odms.controller.AppController;

import java.time.LocalDateTime;

public class OrganExpiryLogicController {
    private AppController appController;
    private ExpiryReason expiryReason;

    public OrganExpiryLogicController(AppController appController, ExpiryReason expiryReason) {
        this.appController = appController;
        this.expiryReason = expiryReason;
    }

    public void setExpiryReason(String reason) {
        expiryReason.setReason(reason);
        expiryReason.setClinicianId(appController.getUsername());
        expiryReason.setName(appController.getName());
        if (expiryReason.getTimeOrganExpired() == null) {
            expiryReason.setTimeOrganExpired(LocalDateTime.now());
        }
    }
}
