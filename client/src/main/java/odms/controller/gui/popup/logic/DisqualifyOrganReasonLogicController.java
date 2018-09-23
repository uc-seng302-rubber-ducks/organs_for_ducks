package odms.controller.gui.popup.logic;

import javafx.collections.ObservableList;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.AttributeValidation;

import java.time.LocalDate;

public class DisqualifyOrganReasonLogicController {

    private User user;
    private Stage stage;
    private ObservableList<OrgansWithDisqualification> disqualifications;

    /**
     * Initializes the DisqualifyOrganReasonLogicController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public DisqualifyOrganReasonLogicController(User user, Stage stage, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        this.user = user;
        this.stage = stage;
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

        OrgansWithDisqualification organsWithDisqualification = new OrgansWithDisqualification(disqualifiedOrgan, description, LocalDate.now(), staffId);
        organsWithDisqualification.setEligibleDate(eligibleDate);
        organsWithDisqualification.setCurrentlyDisqualified(true);
        for (int i = 0; i < disqualifications.size(); i++) {
            if (disqualifications.get(i).getOrganType().equals(disqualifiedOrgan)) {
                disqualifications.remove(i);
                disqualifications.add(i, organsWithDisqualification);
                updateMode = true; //we know that its update if disqualifiedOrgan already exist in the disqualified Organ table.
                break;
            }
        }

        if(!updateMode) {
            disqualifications.add(organsWithDisqualification);
            user.getDonorDetails().getOrgans().remove(organsWithDisqualification.getOrganType());

        }

        stage.close();
    }

    /**
     * checks that Eligible Organ Date is after
     * today's date. Eligible date is optional field.
     *
     * @param date date to allow organs to be eligible for
     *                     donation again
     * @return true if appointment date is
     * after the current date, false otherwise.
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


    /**
     * closes the Disqualify Organ Reason view.
     */
    public void cancel() {
        stage.close();
    }

}
