package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.User;
import odms.commons.model._enum.Organs;
import odms.controller.gui.popup.logic.DisqualifyOrganReasonLogicController;

public class DisqualifyOrganReasonViewController {
    @FXML
    private DatePicker eligibleDateInput;

    @FXML
    private TextArea disqualifyOrganDescriptionInput;


    private DisqualifyOrganReasonLogicController logicController;
    private Organs disqualifiedOrgan;
    private String staffId;


    /**
     * Initializes the AppointmentPickerViewController
     *
     * @param user          Current user
     * @param stage         The applications stage.
     */
    public void init(Organs disqualifiedOrgan, User user, Stage stage, String staffId) {
        this.disqualifiedOrgan = disqualifiedOrgan;
        this.staffId = staffId;
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
        logicController.confirm(disqualifiedOrgan, eligibleDateInput.getValue(), disqualifyOrganDescriptionInput.getText(), staffId);
    }

}
