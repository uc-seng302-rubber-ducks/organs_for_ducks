package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.controller.gui.popup.logic.DisqualifyOrganReasonLogicController;

public class DisqualifyOrganReasonViewController {
    @FXML
    private DatePicker eligibleDateInput;

    @FXML
    private TextArea disqualifyOrganDescriptionInput;


    private DisqualifyOrganReasonLogicController logicController;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public void init(User user, Stage stage) {
        this.logicController = new DisqualifyOrganReasonLogicController(user, stage);

        disqualifyOrganDescriptionInput.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
    }


    @FXML
    public void cancel() {
        logicController.cancel();
    }

    @FXML
    public void confirm() {
        logicController.confirm(eligibleDateInput.getValue(), disqualifyOrganDescriptionInput.getText());
    }

}
