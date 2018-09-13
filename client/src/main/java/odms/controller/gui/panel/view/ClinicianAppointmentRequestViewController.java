package odms.controller.gui.panel.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;
import odms.socket.ServerEventNotifier;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ClinicianAppointmentRequestViewController {

    @FXML
    private TableView<Appointment> clinicianAppointmentsRequestView;

    @FXML
    private DatePicker appointmentRequestDate;

    @FXML
    private TextArea appointmentRequestDescription;

    @FXML
    private ComboBox<AppointmentCategory> appointmentRequestCategory;

    @FXML
    private ComboBox<LocalTime> appointmentRequestTime;

    @FXML
    private Label appointmentRequestStatus;

    @FXML
    private Label appointmentRequestUserNhi;

    @FXML
    private Button rejectAppointmentButton;

    @FXML
    private Button acceptAppointmentButton;

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentUserIdColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentStatus> clinicianAppointmentStatusColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentCategory> clinicianAppointmentCategoryColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentDateColumn = new TableColumn<>();

    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());
    private ObservableList<LocalTime> availableTimes = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");


    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> populateTable());
        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician, availableTimes);
        initAppointmentTable();
        logicController.refreshClincianAvaliableTimes(AppController.getInstance().getAppointmentsBridge(), LocalDate.now());
        populateClinicianTimes();
    }

    /**
     * Populates the table view of appointments for the specified clinician
     * Changes the default sorting order to sort by the appointment status
     */
    private void initAppointmentTable() {
        clinicianAppointmentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestingUserId"));
        clinicianAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        clinicianAppointmentDateColumn.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getRequestedDate().format(formatter)));
        clinicianAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        logicController.updateTable(0);
        populateTable();
        setOnClickBehaviour();
    }

    private void populateTable() {
        clinicianAppointmentsRequestView.setItems(availableAppointments);
    }

    /**
     * Binds a table view row to show all details for that appointment
     */
    private void setOnClickBehaviour() {
        clinicianAppointmentsRequestView.getSelectionModel().selectedItemProperty().addListener(a -> {
            Appointment selectedAppointment = clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem();
            displayAppointmentDetails(selectedAppointment);
            if (selectedAppointment != null) {


                if (selectedAppointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_USER) {
                    selectedAppointment.setAppointmentStatus(AppointmentStatus.CANCELLED_BY_USER_SEEN);
                    AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(selectedAppointment.getAppointmentId(),
                            AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue());
                }

                if (selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED ||
                        selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED_SEEN) {
                    rejectAppointmentButton.setText("Cancel Appointment");
                    acceptAppointmentButton.setText("Update Appointment");
                } else {
                    rejectAppointmentButton.setText("Reject Appointment");
                    acceptAppointmentButton.setText("Accept Appointment");
                }
            }
        });
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
            appointmentRequestTime.setValue(appointment.getRequestedDate().toLocalTime());
            appointmentRequestDescription.setText(appointment.getRequestDescription());
        } else {
            appointmentRequestUserNhi.setText("");
            appointmentRequestStatus.setText("");
            appointmentRequestCategory.setValue(null);
            appointmentRequestDate.setValue(null);
            appointmentRequestTime.setValue(null);
            appointmentRequestDescription.clear();
        }
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

    /**
     * @see ClinicianAppointmentRequestLogicController rejectAppointment()
     */
    @FXML
    private void rejectAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();

        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to reject");
            return;
        }

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

         // logicController.rejectAppointment(selectedAppointment);
    }

    /**
     * @see ClinicianAppointmentRequestLogicController rejectAppointment()
     */
    @FXML
    private void acceptAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();
        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to accept");
            return;
        }

        if (AttributeValidation.validateTimeString(appointmentRequestTime.getSelectionModel().getSelectedItem().toString())) {
            logicController.acceptAppointment(selectedAppointment, appointmentRequestTime.getSelectionModel().getSelectedItem().toString(), AppController.getInstance().getAppointmentsBridge());
        } else {
            appointmentRequestTime.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
        }

        //         logicController.acceptAppointment(selectedAppointment);
    }

    /**
     *
     */
    @FXML
    private void populateClinicianTimes(){
        if (appointmentRequestDate.getValue() != null) {
            logicController.refreshClincianAvaliableTimes(AppController.getInstance().getAppointmentsBridge(), appointmentRequestDate.getValue());
            appointmentRequestTime.setItems(availableTimes);
            //TODO change text field to combo box 1-9-2018
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

    /**
     * Removes the property change listener on logout so user appointment events do not trigger the
     * clinician tables to update.
     */
    public void shutdownPropertyChangeListener() {
        ServerEventNotifier.getInstance().removePropertyChangeListener(logicController);
    }
}
