package odms.controller.gui.panel.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import odms.commons.model.Appointment;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;

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


}
