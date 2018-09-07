package odms.controller.gui.panel.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.UserAppointmentLogicController;

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
    private TextArea userAppointmentDetailsTextArea;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    /**
     * Initialises the panel
     *
     * @param appController Main controller
     * @param user          User that the panel belongs to
     */
    public void init(AppController appController, User user) {
        appointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            populateTable();
        });

        logicController = new UserAppointmentLogicController(appointments, appController, user);
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
        logicController.cancelAppointment(userAppointmentsTableView.getSelectionModel().getSelectedItem());
    }

    /**
     * Binds a table view row selection to show all details for that appointment
     */
    private void setOnClickBehaviour() {
        userAppointmentsTableView.getSelectionModel().selectedItemProperty().addListener(a ->
                displayAppointmentDetails(userAppointmentsTableView.getSelectionModel().getSelectedItem()));
    }

    /**
     * Displays the appointment details in a text area
     *
     * @param appointment The selected appointment to be displayed in more detail
     */
    private void displayAppointmentDetails(Appointment appointment) {
        if (appointment != null) {
            userAppointmentDetailsTextArea.setText(appointment.displayDetails());
        } else {
            userAppointmentDetailsTextArea.clear();
        }
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
