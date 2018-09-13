package odms.controller.gui.panel.view;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.AttributeValidation;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.socket.ServerEventNotifier;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

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
    private TextField appointmentRequestTime;

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
    private ClinicianAppointmentRequestLogicController logicController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> populateTable());
        appointmentRequestDescription.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters

        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician);
        initAppointmentTable();
    }

    /**
     * Compares the appointment status value so that when applied to the table view, pending appointments will be
     * displayed at the top of the table
     */
    private Comparator<AppointmentStatus> statusComparator = Comparator.comparingInt(AppointmentStatus::getDbValue);

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
        clinicianAppointmentStatusColumn.setSortType(TableColumn.SortType.ASCENDING);
        clinicianAppointmentStatusColumn.setComparator(statusComparator);
    }

    /**
     * Creates a sorted list to change the default ordering of the table view and then populates the table
     * with all of the clinicians appointments
     */
    private void populateTable() {
        SortedList<Appointment> sortedAppointments = new SortedList<>(availableAppointments);
        sortedAppointments.comparatorProperty().bind(clinicianAppointmentsRequestView.comparatorProperty());
        clinicianAppointmentsRequestView.setItems(availableAppointments);
        Platform.runLater(() -> clinicianAppointmentsRequestView.getSortOrder().add(clinicianAppointmentStatusColumn));
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
            appointmentRequestTime.setText(appointment.getRequestedDate().toLocalTime().toString());
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
     * @see ClinicianAppointmentRequestLogicController rejectAppointment(), cancelAppointment()
     */
    @FXML
    private void rejectAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();

        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment");
            return;
        }

        AppointmentStatus status = selectedAppointment.getAppointmentStatus();

        if (status == AppointmentStatus.PENDING) {
            logicController.rejectAppointment(selectedAppointment);

        } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
            logicController.cancelAppointment(selectedAppointment);
        }
    }

    /**
     * @see ClinicianAppointmentRequestLogicController acceptAppointment(), updateAppointment()
     */
    @FXML
    private void acceptAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();
        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to accept");
            return;
        }

        boolean valid = true;
        if (!AttributeValidation.validateTimeString(appointmentRequestTime.getText())) {
            appointmentRequestTime.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
            valid = false;
        }

        if (!AttributeValidation.validateDateOfAppointment(appointmentRequestDate.getValue())) {
            appointmentRequestDate.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
            valid = false;
        }

        AppointmentStatus status = selectedAppointment.getAppointmentStatus();

        if (valid) {
            if (status == AppointmentStatus.PENDING) {
                logicController.updateAppointment(selectedAppointment, appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getText(), appointmentRequestDescription.getText(), true);
            } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
                logicController.updateAppointment(selectedAppointment, appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getText(), appointmentRequestDescription.getText(), false);
            } else {
                AlertWindowFactory.generateInfoWindow("This appointment is no longer available");
            }
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
