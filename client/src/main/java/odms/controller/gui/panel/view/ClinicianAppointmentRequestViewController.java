package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClinicianAppointmentRequestViewController {
    @FXML
    private Button clinicianRejectAppointmentBtn;

    @FXML
    private Button clinicianAcceptAppointmentBtn;

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
    private TableView<Appointment> clinicianAppointmentsRequestView;

    @FXML
    private TableColumn<Appointment, LocalDateTime> clinicianAppointmentDateColumn;

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentClinicianIdColumn;

    @FXML
    private TableColumn<Appointment, AppointmentCategory> clinicianAppointmentCategoryColumn;

    @FXML
    private TableColumn<Appointment, AppointmentStatus> clinicianAppointmentStatusColumn;


    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;


    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        System.out.println("Hello yes");
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            populateTable();
        });

        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician);
        initAppointmentTable();
    }

    /**
     * Populates the table view of appointments for the specified clinician
     * Changes the default sorting order to sort by the appointment status
     */
    private void initAppointmentTable() {
        clinicianAppointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        clinicianAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        clinicianAppointmentClinicianIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestedClinician"));
        clinicianAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        logicController.updateTable(0);
        populateTable();
        clinicianAppointmentStatusColumn.setSortType(TableColumn.SortType.ASCENDING);
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
        logicController.goPrevPage();
    }

    /**
     * @see AvailableOrgansLogicController goNextPage()
     */
    @FXML
    private void goToNextPage() {
        logicController.goNextPage();
    }


}
