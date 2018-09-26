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
import odms.controller.gui.panel.DonationTabPageController;
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
    private Stage stage;
    private DonationTabPageController controller;


    /**
     * Initializes the DisqualifyOrganReasonViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     * @param staffId       StaffID of the user disqualifying the organ
     * @param disqualifiedOrgans all the disqualified organs of the user
     * @param controller   Parent controller this window is tied to
     * @param disqualifiedOrgan organ being disqualified
     */
    public void init(Organs disqualifiedOrgan, User user, Stage stage, String staffId, ObservableList<OrgansWithDisqualification> disqualifiedOrgans, DonationTabPageController controller) {
        stage.setResizable(false);
        this.stage = stage;
        this.disqualifiedOrgan = disqualifiedOrgan;
        this.staffId = staffId;
        this.logicController = new DisqualifyOrganReasonLogicController(user, disqualifiedOrgans);

        disqualifyOrganDescriptionInput.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
        disqualifyOrganDescriptionRulesLabel.setText("Must not be empty.\n" +
                                                     "Max 255 characters.\n" +
                                                     "Alphanumeric, apostrophe, and hyphen characters only.");
        this.controller = controller;

    }

    /**
     * pre-populates disqualify organ reason
     * pop-up if its being used to update disqualified organs.
     *
     * @param organsWithDisqualification the disqualified organ
     */
    public void updateMode(OrgansWithDisqualification organsWithDisqualification){
        eligibleDateInput.setValue(organsWithDisqualification.getEligibleDate());
        disqualifyOrganDescriptionInput.setText(organsWithDisqualification.getReason());
    }

    /**
     * closes the Disqualify Organ Reason view.
     */
    @FXML
    public void disqualifyOrganCancel() {
        stage.close();
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
        boolean validDate = validateEligibleTime(); //Split up so the second one will run if the first one is false.
        boolean validDescription = validateDescription();
        if (validDate && validDescription) {
            logicController.confirm(disqualifiedOrgan, eligibleDateInput.getValue(), disqualifyOrganDescriptionInput.getText(), staffId);
            controller.refreshCurrentlyDonating();
            stage.close();
        }
    }

}
