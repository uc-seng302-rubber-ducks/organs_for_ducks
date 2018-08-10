package odms.controller.gui.panel.logic;

import odms.commons.model.Clinician;
import odms.commons.model.datamodel.AvailableOrganDetail;

public class OrganExpiryLogicController {
    private AvailableOrganDetail organDetail;
    private Clinician clinician;

    public OrganExpiryLogicController(AvailableOrganDetail organDetail, Clinician clinician) {
        this.organDetail = organDetail;
        this.clinician = clinician;
    }

    public void setExpiryReason(String reason) {
        //organDetail.setExpiryReason(new ExpiryReason(clinician.getStaffId(), LocalDateTime.now(), reason));
    }
}
