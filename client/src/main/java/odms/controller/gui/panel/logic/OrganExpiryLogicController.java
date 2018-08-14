package odms.controller.gui.panel.logic;

import odms.commons.model.datamodel.OrgansWithExpiry;
import odms.controller.AppController;

import java.time.LocalDateTime;

public class OrganExpiryLogicController {
    private OrgansWithExpiry organsWithExpiry;
    private AppController appController;

    public OrganExpiryLogicController(AppController appController, OrgansWithExpiry organsWithExpiry) {
        this.organsWithExpiry = organsWithExpiry;
        this.appController = appController;
    }

    public void setExpiryReason(String reason) {
        organsWithExpiry.setExpiryReason(reason);
        organsWithExpiry.setStaffId(appController.getUsername());
        organsWithExpiry.setName(appController.getName());
        organsWithExpiry.setExpiryTime(LocalDateTime.now());
    }
}
