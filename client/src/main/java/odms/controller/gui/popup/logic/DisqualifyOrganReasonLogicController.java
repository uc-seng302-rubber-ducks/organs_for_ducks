package odms.controller.gui.popup.logic;

import javafx.application.Platform;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.LocalDate;

import static odms.commons.utils.AttributeValidation.validateEligibleOrganDate;

public class DisqualifyOrganReasonLogicController {

    private User user;
    private Stage stage;

    /**
     * Initializes the DisqualifyOrganReasonLogicController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public DisqualifyOrganReasonLogicController(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
    }

    /**
     * Confirms the disqualification of an organ
     * and updates the database.
     *
     * @param eligibleDate date which user is eligible to donate the organ again
     * @param description reason for disqualifying the organ
     */
    public void confirm(LocalDate eligibleDate, String description) {
        String message = "";
        if(!validateEligibleOrganDate(eligibleDate)){
            message = "Invalid date entered!";
        }

        if (description.isEmpty()) {
            message += "A description must be provided.\n";
        }

        if (message.isEmpty()) {
            stage.close();
        } else {
            alertUser(message);
        }

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
