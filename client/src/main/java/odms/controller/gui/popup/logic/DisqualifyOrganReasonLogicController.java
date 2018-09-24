package odms.controller.gui.popup.logic;

import javafx.collections.ObservableList;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.AttributeValidation;

import java.time.LocalDate;

public class DisqualifyOrganReasonLogicController {

    private User user;
    private ObservableList<OrgansWithDisqualification> disqualifications;

    /**
     * Initializes the DisqualifyOrganReasonLogicController
     *
     * @param user          Current user
     */
    public DisqualifyOrganReasonLogicController(User user, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        this.user = user;
        this.disqualifications = disqualifiedOrgans;
    }

    /**
     * Confirms the disqualification of an organ
     *
     * @param eligibleDate date which user is eligible to donate the organ again
     * @param description reason for disqualifying the organ
     */
    public void confirm(Organs disqualifiedOrgan, LocalDate eligibleDate, String description, String  staffId) {
        Boolean updateMode = false;
        user.saveStateForUndo();

        OrgansWithDisqualification organsWithDisqualification = new OrgansWithDisqualification(disqualifiedOrgan, description, LocalDate.now(), staffId);
        organsWithDisqualification.setEligibleDate(eligibleDate);
        organsWithDisqualification.setCurrentlyDisqualified(true);
        for (int i = 0; i < disqualifications.size(); i++) {
            if (disqualifications.get(i).getOrganType().equals(disqualifiedOrgan)) {
                disqualifications.remove(i);
                disqualifications.add(i, organsWithDisqualification);
                user.getUndoStack().pop();
                user.getUndoStack().pop();
                updateMode = true; //we know that its update if disqualifiedOrgan already exist in the disqualified Organ table.
                break;
            }
        }

        if(!updateMode) {
            user.getUndoStack().pop();
            disqualifications.add(organsWithDisqualification);
            user.getDonorDetails().getOrgans().remove(organsWithDisqualification.getOrganType());

        }
    }

    /**
     * Validates the date that the organ will be eligible again
     * @param date the organ will be valid on
     * @return true if the date is valid
     */
    public boolean validateEligibleOrganDate(LocalDate date) {
        return AttributeValidation.validateEligibleOrganDate(date);
    }

    /**
     * Validates a description string for disqualified organs
     * @param description of the disqualified organ
     * @return true if the description is empty, true otherwise
     */
    public boolean validateDescription(String description) {
        return AttributeValidation.checkRequiredString(description);
    }

}
