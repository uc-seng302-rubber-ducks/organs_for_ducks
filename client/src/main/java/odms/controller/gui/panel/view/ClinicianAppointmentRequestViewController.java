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
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    @FXML
    private TableView<Appointment> clinicianAppointmentsRequestView  = new TableView<>();

    private ClinicianAppointmentRequestLogicController logicController;
    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentUserIdColumn = new TableColumn<>();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");
    @FXML
    private TableColumn<Appointment, AppointmentStatus> clinicianAppointmentStatusColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentCategory> clinicianAppointmentCategoryColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentDateColumn = new TableColumn<>();

    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());


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
            if (selectedAppointment != null) {
                displayAppointmentDetails(selectedAppointment);
            }
        });
    }

    /**
     * Displays the appointment details in the text area
     * @param appointment The selected appointment to be displayed in more detail
     */
    private void displayAppointmentDetails(Appointment appointment) {
        appointmentRequestDescription.setText(appointment.getRequestDescription()); //appointment.displayDetails does not exist in this branch. Replace getDescription with it when possible
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
        Appointment selectedAppointment = clinicianAppointmentRequestView.getSelectionModel().getSelectedItem();

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

    }


}
