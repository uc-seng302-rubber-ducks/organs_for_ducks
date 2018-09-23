package odms.controller.gui.popup.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.controller.gui.popup.logic.DisqualifyOrganReasonLogicController;

public class DisqualifyOrganReasonViewController {
    @FXML
    private DatePicker eligibleDateInput;

    @FXML
    private TextArea disqualifyOrganDescriptionInput;

    @FXML
    private Label eligibleDateErrorLabel;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private Label disqualifyOrganDescriptionRulesLabel;


    private DisqualifyOrganReasonLogicController logicController;
    private Organs disqualifiedOrgan;
    private String staffId;


    /**
     * Initializes the DisqualifyOrganReasonViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public void init(Organs disqualifiedOrgan, User user, Stage stage, String staffId, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        stage.setResizable(false);
        this.disqualifiedOrgan = disqualifiedOrgan;
        this.staffId = staffId;
        this.logicController = new DisqualifyOrganReasonLogicController(user, stage, disqualifiedOrgans);

        disqualifyOrganDescriptionInput.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
        disqualifyOrganDescriptionRulesLabel.setText("Must not be empty.\n" +
                                                     "Max 255 characters.\n" +
                                                     "Alphanumeric, apostrophe, and hyphen characters only.");

    }

    public void updateMode(OrgansWithDisqualification organsWithDisqualification){
        eligibleDateInput.setValue(organsWithDisqualification.getEligibleDate());
        disqualifyOrganDescriptionInput.setText(organsWithDisqualification.getReason());
    }


    @FXML
    public void disqualifyOrganCancel() {
        logicController.cancel();
    }

    /**
     * Validates the date in the eligible date field. If it is not valid, set the error message and return false.
     * @return Boolean describing if the input date is valid
     */
    private boolean validateEligibleTime() {
        if (!logicController.validateEligibleOrganDate(eligibleDateInput.getValue())) {
            eligibleDateErrorLabel.setText("Date must be after today's date");
            return false;
        } else {
            eligibleDateErrorLabel.setText("");
            return true;
        }
    }

    /**
     * Validates the description text field. If it not valid, set the error message and return false
     * @return Boolean describing if the input is valid
     */
    private boolean validateDescription() {
        if (!logicController.validateDescription(disqualifyOrganDescriptionInput.getText())) {
            descriptionErrorLabel.setText("Invalid description.");
            return false;
        } else {
            descriptionErrorLabel.setText("");
            return true;
        }
    }

    /**
     * validates and processes input from the pop up
     * when user has selected confirm to disqualify an organ.
     */
    @FXML
    public void disqualifyOrganConfirm() {
        //This is the best way I can think of editing the labels without the logic controller knowing about them, but keeping the least amount of logic in the view-controller
        boolean validDate = validateEligibleTime(); //Split up so the second one will run if the first one is false.
        boolean validDescription = validateDescription();
        if (validDate && validDescription) {
            logicController.confirm(disqualifiedOrgan, eligibleDateInput.getValue(), disqualifyOrganDescriptionInput.getText(), staffId);
        }
    }

}
