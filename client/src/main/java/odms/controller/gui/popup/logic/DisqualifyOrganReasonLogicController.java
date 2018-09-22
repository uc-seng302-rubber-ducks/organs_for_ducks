package odms.controller.gui.popup.logic;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.commons.utils.AttributeValidation;
import odms.controller.gui.popup.utils.AlertWindowFactory;

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
     * and updates the database.
     *
     * @param eligibleDate date which user is eligible to donate the organ again
     * @param description reason for disqualifying the organ
     */
    public void confirm(Organs disqualifiedOrgan, LocalDate eligibleDate, String description, String  staffId) {

        OrgansWithDisqualification organsWithDisqualification = new OrgansWithDisqualification(disqualifiedOrgan, description, LocalDate.now(), staffId);
        organsWithDisqualification.setEligibleDate(eligibleDate);
        organsWithDisqualification.setCurrentlyDisqualified(true);
        disqualifications.add(organsWithDisqualification);

        stage.close();
    }

    public boolean validateEligibleOrganDate(LocalDate date) {
        return AttributeValidation.validateEligibleOrganDate(date);
    }

    /**
     * Validates a description string for disqualified organs
     * @param description of the disqualified organ
     * @return true if the description is empty, true otherwise
     */
    public boolean validateDescription(String description) {
        return !description.isEmpty();
    }

    /**
     * Alerts user with a alert window containing the given message
     *
     * @param message message to display to the user.
     */
    private void alertUser(String message) {
        Platform.runLater(() -> AlertWindowFactory.generateError(message));
    }


    /**
     * closes the Disqualify Organ Reason view.
     */
    public void cancel() {
        stage.close();
    }

}
