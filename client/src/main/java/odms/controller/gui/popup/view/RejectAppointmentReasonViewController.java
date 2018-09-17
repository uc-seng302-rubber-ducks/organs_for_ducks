package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.controller.AppController;
import odms.controller.gui.popup.logic.RejectAppointmentReasonLogicController;

public class RejectAppointmentReasonViewController {
    @FXML
    private Label apptPatient;

    @FXML
    private Text apptDescription;

    @FXML
    private Label apptDate;

    @FXML
    private TextArea reasonTextArea;

    @FXML
    private GridPane rejectionGridPane;

    private Appointment appointment;
    private RejectAppointmentReasonLogicController logicController;
    private Stage stage;

    public void init(Appointment appointment, Stage stage) {
        this.appointment = appointment;
        this.stage = stage;
        logicController = new RejectAppointmentReasonLogicController();
        apptPatient.setText(appointment.getRequestingUserId());
        apptDate.setText(appointment.getRequestedDate().toLocalDate().toString());
        apptDescription.setText(appointment.getRequestDescription());

        apptDescription.wrappingWidthProperty().bind(rejectionGridPane.widthProperty());
        reasonTextArea.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters

    }

    /**
     * Confirms the rejection of the appointment.
     */
    @FXML
    private void confirmRejectionReason() {
        logicController.rejectAppointment(appointment, reasonTextArea.getText(), AppController.getInstance().getAppointmentsBridge());
        stage.close();
    }

    /**
     * Cancels the rejection reason
     */
    @FXML
    private void cancelRejectionReason() {
        stage.close();
    }

}
