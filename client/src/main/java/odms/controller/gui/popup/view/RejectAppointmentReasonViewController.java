package odms.controller.gui.popup.view;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import odms.bridge.AppointmentsBridge;
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

    }

    /**
     * Confirms the rejection of the appointment.
     */
    @FXML
    private void confirmRejectionReason() {
        logicController.rejectAppointment(appointment, reasonTextArea.getText(), new AppointmentsBridge(AppController.getInstance().getClient()));
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
