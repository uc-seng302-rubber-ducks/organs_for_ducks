package odms.controller.gui.popup.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model.datamodel.OrgansWithDisqualification;
import odms.controller.gui.popup.logic.RemoveDisqualificationLogicController;

public class RemoveDisqualificationViewController {

    @FXML
    private Label removeDisqualificationDescriptionRulesLabel;

    @FXML
    private TextArea removeDisqualificationDescriptionTextField;

    @FXML
    private Label removeDisqualificationDescriptionErrorLabel;

    private RemoveDisqualificationLogicController logicController;
    private OrgansWithDisqualification disqualifiedOrgan;
    private Stage stage;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public void init(OrgansWithDisqualification disqualifiedOrgan, User user, Stage stage, ObservableList<OrgansWithDisqualification> disqualifiedOrgans) {
        stage.setResizable(false);
        this.stage = stage;
        this.disqualifiedOrgan = disqualifiedOrgan;
        this.logicController = new RemoveDisqualificationLogicController(user, disqualifiedOrgans);

        removeDisqualificationDescriptionTextField.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
        removeDisqualificationDescriptionRulesLabel.setText("Must not be empty.\n" +
                "Max 255 characters.\n" +
                "Alphanumeric, apostrophe, and hyphen characters only.");

    }

    /**
     * closes the Disqualify Organ Reason view.
     */
    @FXML
    public void removeDisqualificationCancel() {
        stage.close();
    }


    /**
     * Validates the description text field. If it not valid, set the error message and return false
     * @return Boolean describing if the input is valid
     */
    private boolean validateDescription() {
        if (!logicController.validateDescription(removeDisqualificationDescriptionTextField.getText())) {
            removeDisqualificationDescriptionErrorLabel.setText("Invalid description.");
            return false;
        } else {
            removeDisqualificationDescriptionErrorLabel.setText("");
            return true;
        }
    }

    @FXML
    public void removeDisqualificationConfirm() {
        if (validateDescription()) {
            logicController.confirm(disqualifiedOrgan, removeDisqualificationDescriptionTextField.getText());
            stage.close();
        }
    }
}
