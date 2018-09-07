package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import odms.bridge.AppointmentsBridge;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;

import java.io.IOException;
import java.time.LocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClinicianAppointmentRequestViewController {

    @FXML
    private DatePicker appointmentRequestDate;

    @FXML
    private TextArea appointmentRequestDescription;

    @FXML
    private ComboBox<AppointmentCategory> appointmentRequestCategory;

    @FXML
    private TextField appointmentRequestTime;

    @FXML
    private Label appointmentRequestStatus;

    @FXML
    private Label appointmentRequestUserNhi;

    @FXML
    private TableView<Appointment> clinicianAppointmentsRequestView  = new TableView<>();

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentUserIdColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentStatus> clinicianAppointmentUserNameColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, LocalDateTime> clinicianAppointmentDateColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentCategory> clinicianAppointmentCategoryColumn = new TableColumn<>();


    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;


    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> populateTable());

        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician);
        initAppointmentTable();
    }

    /**
     * Populates the table view of appointments for the specified clinician
     * Changes the default sorting order to sort by the appointment status
     */
    private void initAppointmentTable() {
        clinicianAppointmentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestingUserId"));
        clinicianAppointmentUserNameColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus")); //Somehow get the user's names
        clinicianAppointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        clinicianAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        logicController.updateTable(0);
        populateTable();
        setOnClickBehaviour();
    }

    private void populateTable() {
        clinicianAppointmentsRequestView.setItems(availableAppointments);
    }

    /**
     * Launches a pop-up for the clinician to accept an appointment and pick a time
     */
    @FXML
    public void clinicianAcceptAppointmentBtn() {
        logicController.launchAcceptedPopup();
    }

    /**
     * Launches a pop-up for the clinician to reject an appoint and enter a reason why
     */
    @FXML
    public void clinicianRejectAppointmentBtn() {
        logicController.launchRejectedPopup();
    }

    /**
     * Binds a table view row to show all details for that appointment
     */
    private void setOnClickBehaviour() {
        clinicianAppointmentsRequestView.getSelectionModel().selectedItemProperty().addListener(a ->
        displayAppointmentDetails(clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem()));
    }

    /**
     * Displays the appointment details in separated fields
     *
     * @param appointment The selected appointment to be displayed in more detail
     */
    private void displayAppointmentDetails(Appointment appointment) {
        if (appointment != null) {
            appointmentRequestUserNhi.setText(appointment.getRequestingUserId());
            appointmentRequestStatus.setText(appointment.getAppointmentStatus().toString());
            appointmentRequestCategory.setValue(appointment.getAppointmentCategory());
            appointmentRequestDate.setValue(appointment.getRequestedDate().toLocalDate());
            appointmentRequestTime.setText(getAppointmentTime(appointment.getRequestedDate()));
            appointmentRequestDescription.setText(appointment.getRequestDescription());
        } else {
            appointmentRequestUserNhi.setText("");
            appointmentRequestStatus.setText("");
            appointmentRequestCategory.setValue(null);
            appointmentRequestDate.setValue(null);
            appointmentRequestTime.clear();
            appointmentRequestDescription.clear();
        }
    }

    /**
     * Extracts the appointment time and formats it to be human readable
     *
     * @param dateTime The local date time object of the appointment
     * @return The appointment time as a String
     */
    private String getAppointmentTime(LocalDateTime dateTime) {
        String appointmentTime;
        String minute = String.format("%02d", dateTime.getMinute());
        String hour = String.format("%02d", dateTime.getHour());
        appointmentTime = hour + ":" + minute;
        return appointmentTime;
    }

    /**
     * @see AvailableOrgansLogicController goPrevPage()
     */
    @FXML
    private void goToPreviousPage() {
        logicController.goToPreviousPage();
    }

    /**
     * @see AvailableOrgansLogicController goNextPage()
     */
    @FXML
    private void goToNextPage() {
        logicController.goToNextPage();
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
        return clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem();
    }


}
