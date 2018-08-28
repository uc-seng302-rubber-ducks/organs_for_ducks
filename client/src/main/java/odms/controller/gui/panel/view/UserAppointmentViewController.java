package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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

    public void init(AppController appController, User user) {
        logicController = new UserAppointmentLogicController(appController, user);
        initUserAppointmentsTableView();
    }

    private void initUserAppointmentsTableView() {
        // populate table
        setOnClickBehaviour();
    }


    @FXML
    public void requestNewAppointment() {
        logicController.requestNewAppointment();
    }

    private void setOnClickBehaviour() {


    }
}
