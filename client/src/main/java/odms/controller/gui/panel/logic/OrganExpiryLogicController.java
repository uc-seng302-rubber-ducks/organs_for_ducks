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

    /**
     * Updates the expiry reason object to contain the reason for expiry, the time of manual expiry
     * and the name and id of the clinician/admin who expired it
     *
     * @param reason the given reason for why a donated organ was manually expired
     */
    public void setExpiryReason(String reason) {
        expiryReason.setReason(reason);
        expiryReason.setId(appController.getUsername());
        expiryReason.setName(appController.getName());
        if (expiryReason.getTimeOrganExpired() == null) {
            expiryReason.setTimeOrganExpired(LocalDateTime.now());
        }
    }
}
