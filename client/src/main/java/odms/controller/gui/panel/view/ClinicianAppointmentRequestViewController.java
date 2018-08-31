package odms.controller.gui.panel.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import odms.commons.model.Appointment;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;

public class ClinicianAppointmentRequestViewController {

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

    private ObservableList<Appointment> availableAppointments;

    private ClinicianAppointmentRequestLogicController logicController = new ClinicianAppointmentRequestLogicController(availableAppointments);


    /**
     * Initialises the panel
     */
    @FXML
    public void init() {

    }

    /**
     *
     */
    private void initAppointmentTable() {

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

    @FXML
    private void rejectAppointment() {

    }

    @FXML
    private void acceptAppointment() {

    }


}
