package odms.controller.gui.panel.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.UserAppointmentLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserAppointmentViewController {

    private ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());
    private UserAppointmentLogicController logicController;

    @FXML
    private TableView<Appointment> userAppointmentsTableView;

    @FXML
    private TableColumn<Appointment, String> userAppointmentDateColumn;

    @FXML
    private TableColumn<Appointment, String> userAppointmentClinicianIdColumn;

    @FXML
    private TableColumn<Appointment, AppointmentCategory> userAppointmentCategoryColumn;

    @FXML
    private TableColumn<Appointment, AppointmentStatus> userAppointmentStatusColumn;

    @FXML
    private Label userAppointmentDetailsLabel;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    /**
     * Initialises the panel
     *
     * @param user          User that the panel belongs to
     */
    public void init(User user) {
        appointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            populateTable();
        });

        logicController = new UserAppointmentLogicController(appointments, user);
        initUserAppointmentsTableView();
    }

    /**
     * Populates the table view of appointments for the specified user
     * Changes the default sorting order to sort by the appointment status
     */
    private void initUserAppointmentsTableView() {
        userAppointmentDateColumn.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getRequestedDate().format(formatter)));
        userAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        userAppointmentClinicianIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestedClinicianId"));
        userAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        userAppointmentStatusColumn.setCellFactory(cell -> AppointmentTableCellFactory.generateAppointmentTableCell());

        logicController.updateTable(0);
        populateTable();
        userAppointmentStatusColumn.setSortType(TableColumn.SortType.ASCENDING);
        setOnClickBehaviour();
    }

    private void populateTable() {
        userAppointmentsTableView.setItems(appointments);
    }

    /**
     * Launches a pop-up for the user to request a new appointment
     */
    @FXML
    public void requestNewAppointment() {
        logicController.launchAppointmentPicker();
    }

    /**
     * Attempts to cancel the selected appointment
     */
    @FXML
    public void cancelAppointment() {
        if (userAppointmentsTableView.getSelectionModel().getSelectedItem() == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to delete");
            return;
        }

        logicController.cancelAppointment(userAppointmentsTableView.getSelectionModel().getSelectedItem());
    }

    /**
     * Binds a table view row selection to show all details for that appointment
     */
    private void setOnClickBehaviour() {
        userAppointmentsTableView.getSelectionModel().selectedItemProperty().addListener(a -> {
            Appointment selectedAppointment = userAppointmentsTableView.getSelectionModel().getSelectedItem();
            displayAppointmentDetails(selectedAppointment);

            if (selectedAppointment != null && selectedAppointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_CLINICIAN) {
                selectedAppointment.setAppointmentStatus(AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN);
                AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(selectedAppointment.getAppointmentId(),
                        AppointmentStatus.CANCELLED_BY_CLINICIAN_SEEN.getDbValue());
            }
        });
    }

    /**
     * Displays the appointment details in a text area
     *
     * @param appointment The selected appointment to be displayed in more detail
     */
    private void displayAppointmentDetails(Appointment appointment) {
        if (appointment != null) {
            userAppointmentDetailsLabel.setText(displayDetails(appointment));
        } else {
            userAppointmentDetailsLabel.setText("");
        }
    }

    /**
     * Formats the given appointment's details into multiple lines so it is more readable.
     *
     * @param appointment The appointment to be viewed
     * @return A string containing details of the appointment
     */
    private String displayDetails(Appointment appointment) {
        String newLines = "\n\n\n";
        String details = appointment.getAppointmentStatus().toString() + newLines;
        details += appointment.getRequestedDate().toLocalDate().toString() + newLines;
        details += appointment.getRequestedDate().toLocalTime().toString() + newLines;
        details += appointment.getRequestedClinicianId() + newLines;
        details += appointment.getAppointmentCategory().toString() + newLines;
        details += appointment.getRequestDescription();

        return details;
    }

    /**
     * @see UserAppointmentLogicController goToPreviousPage()
     */
    @FXML
    public void goToPreviousPage() {
        logicController.goToPreviousPage();
    }

    /**
     * @see UserAppointmentLogicController goToNextPage()
     */
    @FXML
    public void goToNextPage() {
        logicController.goToNextPage();
    }
}
