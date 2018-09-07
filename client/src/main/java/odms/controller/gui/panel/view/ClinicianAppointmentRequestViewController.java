package odms.controller.gui.panel.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.utils.Log;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;

import java.io.IOException;

public class ClinicianAppointmentRequestViewController {

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

    private ClinicianAppointmentRequestLogicController logicController = new ClinicianAppointmentRequestLogicController(availableAppointments);


    /**
     * Initialises the panel
     */
    @FXML
    public void init() {

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
        Appointment selectedAppointment = clinicianAppointmentRequestView.getSelectionModel().getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/appointmentRejection.fxml"));
        Stage rejectionStage = new Stage();
        Parent root;
        try {
            root = loader.load();
            RejectAppointmentReasonViewController loginController = loader.getController();
            rejectionStage.setScene(new Scene(root));

            loginController.init(selectedAppointment, rejectionStage);
        } catch (IOException e) {
            Log.severe("failed to load login window FXML", e);
        }
    }

    @FXML
    private void acceptAppointment() {

    }


}
