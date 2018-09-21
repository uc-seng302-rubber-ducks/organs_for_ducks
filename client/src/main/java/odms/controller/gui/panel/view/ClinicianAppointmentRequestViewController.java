package odms.controller.gui.panel.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.EntryViewBase;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.AttributeValidation;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.view.RejectAppointmentReasonViewController;
import odms.controller.gui.widget.CalendarWidget;
import odms.controller.gui.widget.CalendarWidgetFactory;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
    private TableColumn<Appointment, String> clinicianAppointmentUserIdColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentStatus> clinicianAppointmentStatusColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, AppointmentCategory> clinicianAppointmentCategoryColumn = new TableColumn<>();

    @FXML
    private TableColumn<Appointment, String> clinicianAppointmentDateColumn = new TableColumn<>();

    @FXML
    private AnchorPane tableViewPane;

    @FXML
    private AnchorPane calendarViewPane;

    @FXML
    private Toggle calendarViewToggle;
    @FXML
    private Toggle tableViewToggle;
    @FXML
    private Label appointmentDetailsNhiLabel;

    private CalendarWidget calendarView;


    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");


    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            populateTable();
            populateCalendar();
        });

        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician);
        initAppointmentTable();
        initCalendar();
    }

    private void initCalendar() {
        calendarView = CalendarWidgetFactory.createCalendar();
        calendarViewPane.getChildren().add(calendarView);
        calendarView.getWeekPage().getDetailedWeekView().getWeekView().getWeekDayViews().forEach(wdc -> wdc.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof EntryViewBase) node.setOnMouseClicked(event -> {
                if (event.getButton().equals(MouseButton.PRIMARY) && ((EntryViewBase) node).getEntry().getUserObject() != null) {
                    displayAppointmentDetails((Appointment) ((EntryViewBase) node).getEntry().getUserObject());
                }
            });
        }));

        AnchorPane.setTopAnchor(calendarView,0.0);
        AnchorPane.setBottomAnchor(calendarView,0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
        populateCalendar();
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

    private void populateCalendar() {
        if (calendarView != null) {
            calendarView.getCalendarSources().forEach(cs -> cs.getCalendars().forEach(Calendar::clear));
            for (Appointment appointment : availableAppointments) {
                if (appointment.getAppointmentStatus().equals(AppointmentStatus.ACCEPTED) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.ACCEPTED_SEEN) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.PENDING) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.UPDATED)) {

                    Entry<Appointment> entry = new Entry<>();
                    if (appointment.getRequestingUserId() == null) {
                        entry.setTitle(appointment.getTitle());
                    } else {
                        entry.setTitle(appointment.getRequestingUserId());
                    }
                    entry.setUserObject(appointment);
                    entry.setInterval(appointment.getRequestedDate(), appointment.getRequestedDate().plusHours(1));
                    calendarView.addEntry(entry);
                }
            }

        }
    }

    /**
     * Binds a table view row to show all details for that appointment
     */
    private void setOnClickBehaviour() {
        clinicianAppointmentsRequestView.getSelectionModel().selectedItemProperty().addListener(a -> {
            Appointment selectedAppointment = clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem();
            displayAppointmentDetails(selectedAppointment);
        });
    }

    /**
     * Displays the appointment details in separated fields
     *
     * @param appointment The selected appointment to be displayed in more detail
     */
    private void displayAppointmentDetails(Appointment appointment) {
        if (appointment != null) {
            if (appointment.getRequestingUserId() == null) {
                appointmentDetailsNhiLabel.setText("Title: ");
                appointmentRequestUserNhi.setText(appointment.getTitle());
            } else {
                appointmentDetailsNhiLabel.setText("NHI: ");
                appointmentRequestUserNhi.setText(appointment.getRequestingUserId());
            }
            appointmentRequestStatus.setText(appointment.getAppointmentStatus().toString());
            appointmentRequestCategory.setValue(appointment.getAppointmentCategory());
            appointmentRequestDate.setValue(appointment.getRequestedDate().toLocalDate());
            appointmentRequestTime.setText(appointment.getRequestedDate().toLocalTime().toString());
            appointmentRequestDescription.setText(appointment.getRequestDescription());
        } else {
            appointmentDetailsNhiLabel.setText("NHI: ");
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

    @FXML
    private void rejectAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();

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
        Appointment selectedAppointment = getSelectedAppointment();
        if (AttributeValidation.validateTimeString(appointmentRequestTime.getText())) {
            logicController.acceptAppointment(selectedAppointment, appointmentRequestTime.getText(), AppController.getInstance().getAppointmentsBridge());
        } else {
            appointmentRequestTime.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
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

    @FXML
    private void tableCalendarViewToggle() {
        if(calendarViewToggle.isSelected()){
            calendarViewPane.setVisible(true);
            tableViewPane.setVisible(false);

        } else if(tableViewToggle.isSelected()) {
            calendarViewPane.setVisible(false);
            tableViewPane.setVisible(true);
        }
    }
}
