package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import odms.commons.model.Appointment;
import odms.commons.model.User;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.UserAppointmentLogicController;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserAppointmentViewController {

    private ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());
    private UserAppointmentLogicController logicController;

    @FXML
    private TableView<Appointment> userAppointmentsTableView;

    @FXML
    private TableColumn<Appointment, LocalDateTime> userAppointmentDateColumn;

    @FXML
    private TableColumn<Appointment, String> userAppointmentClinicianIdColumn;

    @FXML
    private TableColumn<Appointment, AppointmentCategory> userAppointmentCategoryColumn;

    @FXML
    private TableColumn<Appointment, AppointmentStatus> userAppointmentStatusColumn;

    /**
     * Initialises the panel
     *
     * @param appController Main controller
     * @param user          User that the panel belongs to
     */
    public void init(AppController appController, User user) {
        logicController = new UserAppointmentLogicController(appController, user);
        initUserAppointmentsTableView();
    }

    /**
     * Populates the table view of appointments for the specified user
     * Changes the default sorting order to sort by the appointment status
     */
    private void initUserAppointmentsTableView() {
        userAppointmentDateColumn.setCellValueFactory(new PropertyValueFactory<>("requestedDate"));
        userAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        userAppointmentClinicianIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestedClinician"));
        userAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        populateTable();
        // TODO sort by status 28/08/2018
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
        logicController.launchAppointmentPicker(false);
    }

    /**
     * Binds a double click to each row in the table to launch a pop-up with more details about the selected appointment
     */
    private void setOnClickBehaviour() {
        userAppointmentsTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && userAppointmentsTableView.getSelectionModel().getSelectedItem() != null) {
                logicController.launchAppointmentPicker(true);
            }
        });
    }
}
