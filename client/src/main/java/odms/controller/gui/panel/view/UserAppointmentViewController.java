package odms.controller.gui.panel.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import odms.commons.model.Appointment;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.controller.gui.panel.logic.UserAppointmentLogicController;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserAppointmentViewController {

    private ObservableList<Appointment> appointments = FXCollections.observableList(new ArrayList<>());
    private UserAppointmentLogicController logicController = new UserAppointmentLogicController();

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

    @FXML
    public void init() {


    }

    private void initUserAppointmentsTableView() {

    }


    @FXML
    public void openAppointmentPicker() {

    }
}
