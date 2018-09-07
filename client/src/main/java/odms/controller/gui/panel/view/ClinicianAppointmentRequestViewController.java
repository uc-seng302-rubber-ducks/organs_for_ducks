package odms.controller.gui.panel.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.bridge.AppointmentsBridge;
import odms.commons.model.Appointment;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;

import java.io.IOException;
import java.time.LocalDateTime;

public class ClinicianAppointmentRequestViewController {

    @FXML
    public TableColumn<Appointment, String> categoryTableColumn;

    @FXML
    private TableColumn<Appointment, LocalDateTime> dateTimeTableColumn;

    @FXML
    private TableColumn<Appointment, String> nameTableColumn;

    @FXML
    private TableColumn<Appointment, String> nhiTableColumn;

    @FXML
    private TableView<Appointment> clinicianAppointmentRequestView;

    @FXML
    private TextArea appointmentRequestDescription;

    @FXML
    private ComboBox appointmentRequestCategory;

    @FXML
    private TextField appointmentRequestTime;

    @FXML
    private Label appointmentRequestUserName;

    @FXML
    private Label appointmentRequestUserNhi;

    private ObservableList<Appointment> availableAppointments;

    private ClinicianAppointmentRequestLogicController logicController;


    /**
     * Initialises the panel
     */
    @FXML
    public void init() {
        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments);
    }

    /**
     *
     */
    private void initAppointmentTable() {

    }

    /**
     * @see AvailableOrgansLogicController goPrevPage()
     */
    @FXML
    private void goToPreviousPage() {
        logicController.goPrevPage();
    }

    /**
     * @see AvailableOrgansLogicController goNextPage()
     */
    @FXML
    private void goToNextPage() {
        logicController.goNextPage();
    }

    @FXML
    private void rejectAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/appointmentRejection.fxml"));
        Stage rejectionStage = new Stage();
        Parent root;
        try {
            root = loader.load();
            RejectAppointmentReasonViewController rejectionController = loader.getController();
            rejectionStage.setScene(new Scene(root));

            rejectionController.init(selectedAppointment, rejectionStage);
            rejectionStage.show();
        } catch (IOException e) {
            Log.severe("failed to load login window FXML", e);
        }
    }

    @FXML
    private void acceptAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();
        if (AttributeValidation.validateTimeString(appointmentRequestTime.getText())) {
            logicController.acceptAppointment(selectedAppointment, appointmentRequestTime.getText(), new AppointmentsBridge(AppController.getInstance().getClient()));
        } else {
            appointmentRequestTime.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
        }
    }

    /**
     * Grabs and returns the selected appointment from the table
     *
     * @return the currently selected appointment
     */
    private Appointment getSelectedAppointment() {
        return clinicianAppointmentRequestView.getSelectionModel().getSelectedItem();
    }


}
