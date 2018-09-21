package odms.controller.gui.panel.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.Entry;
import com.calendarfx.view.EntryViewBase;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import odms.commons.model.Appointment;
import odms.commons.model.Clinician;
import odms.commons.model._enum.AppointmentCategory;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.utils.AppointmentTableCellFactory;
import odms.commons.utils.AttributeValidation;
import odms.controller.AppController;
import odms.controller.gui.panel.logic.AvailableOrgansLogicController;
import odms.controller.gui.panel.logic.ClinicianAppointmentRequestLogicController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.widget.CalendarWidget;
import odms.controller.gui.widget.CalendarWidgetFactory;
import odms.socket.ServerEventNotifier;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;

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
    private ComboBox<LocalTime> appointmentRequestTime;

    @FXML
    private Label appointmentRequestStatus;

    @FXML
    private Label appointmentRequestUserNhi;

    @FXML
    private Button rejectAppointmentButton;

    @FXML
    private Button acceptAppointmentButton;

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
    private ObservableList<LocalTime> availableTimes = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");

    /**
     * Initialises the panel
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            populateTable(false);
            populateCalendar();
        });
        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician, availableTimes);
        appointmentRequestDescription.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters

        initAppointmentTable();
        logicController.refreshClinicianAvailableTimes(LocalDate.now());
        populateClinicianTimes();
        datePickerListener(appointmentRequestDate);
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
     * Changes the title bar to add/remove an asterisk when a change was detected on the date picker.
     *
     * @param dp The current date picker.
     */
    private void datePickerListener(DatePicker dp) {
        dp.valueProperty().addListener((observable, oldValue, newValue) -> {
                populateClinicianTimes();
        });
    }

    /**
     * Compares the appointment status value so that when applied to the table view, pending appointments will be
     * displayed at the top of the table
     */
    private Comparator<AppointmentStatus> statusComparator = Comparator.comparingInt(AppointmentStatus::getDbValue);

    /**
     * Populates the table view of appointments for the specified clinician
     * Changes the default sorting order to sort by the appointment status
     */
    private void initAppointmentTable() {
        clinicianAppointmentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("requestingUserId"));
        clinicianAppointmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentStatus"));
        clinicianAppointmentDateColumn.setCellValueFactory(foo -> new SimpleStringProperty(foo.getValue().getRequestedDate().format(formatter)));
        clinicianAppointmentCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentCategory"));
        clinicianAppointmentStatusColumn.setCellFactory(cell -> AppointmentTableCellFactory.generateAppointmentTableCell());

        logicController.updateTable(0);
        populateTable(true);
        setTableOnClickBehaviour();
        clinicianAppointmentStatusColumn.setSortType(TableColumn.SortType.ASCENDING);
        clinicianAppointmentStatusColumn.setComparator(statusComparator);
    }

    /**
     * Creates a sorted list to change the default ordering of the table view and then populates the table
     * with all of the clinicians appointments
     */
    private void populateTable(boolean listen) {
        SortedList<Appointment> sortedAppointments = new SortedList<>(availableAppointments);
        sortedAppointments.comparatorProperty().bind(clinicianAppointmentsRequestView.comparatorProperty());
        clinicianAppointmentsRequestView.setItems(availableAppointments);
        if (listen)
            Platform.runLater(() -> clinicianAppointmentsRequestView.getSortOrder().add(clinicianAppointmentStatusColumn));
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
                    entry.titleProperty().addListener(((observable, oldValue, newValue) -> entry.getUserObject().setTitle(newValue)));
                    entry.intervalProperty().addListener((observable, oldValue, newValue) -> entry.getUserObject().setRequestedDate(entry.getInterval().getStartDateTime()));
                    entry.getProperties().put("quiet", true);
                    calendarView.addEntry(entry);
                }
            }
            calendarView.getSelections().addListener((SetChangeListener<Entry<?>>) change -> {
                if (calendarView.getSelections().size() == 1 && change.getElementAdded() != null) {
                    Appointment appointment = (Appointment) change.getElementAdded().getUserObject();
                    populateClinicianTimes();
                    displayAppointmentDetails(appointment);
                } else {
                    displayAppointmentDetails(null);
                }
            });
        }
    }

    /**
     * Binds a table view row to show all details for that appointment
     */
    private void setTableOnClickBehaviour() {
        clinicianAppointmentsRequestView.getSelectionModel().selectedItemProperty().addListener(a -> {
            Appointment selectedAppointment = clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem();
            displayAppointmentDetails(selectedAppointment);
            if (selectedAppointment != null) {


                if (selectedAppointment.getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_USER) {
                    selectedAppointment.setAppointmentStatus(AppointmentStatus.CANCELLED_BY_USER_SEEN);
                    AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(selectedAppointment.getAppointmentId(),
                            AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue());
                }

                if (selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED ||
                        selectedAppointment.getAppointmentStatus() == AppointmentStatus.ACCEPTED_SEEN) {
                    rejectAppointmentButton.setText("Cancel Appointment");
                    acceptAppointmentButton.setText("Update Appointment");
                } else {
                    rejectAppointmentButton.setText("Reject Appointment");
                    acceptAppointmentButton.setText("Accept Appointment");
                }
            }
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
            appointmentRequestTime.setValue(appointment.getRequestedDate().toLocalTime());
            appointmentRequestDescription.setText(appointment.getRequestDescription());
        } else {
            appointmentDetailsNhiLabel.setText("NHI: ");
            appointmentRequestUserNhi.setText("");
            appointmentRequestStatus.setText("");
            appointmentRequestCategory.setValue(null);
            appointmentRequestDate.setValue(null);
            appointmentRequestTime.setValue(null);
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

    /**
     * @see ClinicianAppointmentRequestLogicController rejectAppointment(), cancelAppointment()
     */
    @FXML
    private void rejectAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();

        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment");
            return;
        }

        AppointmentStatus status = selectedAppointment.getAppointmentStatus();

        if (status == AppointmentStatus.PENDING) {
            logicController.rejectAppointment(selectedAppointment);

        } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
            logicController.cancelAppointment(selectedAppointment);
        }
    }

    /**
     * @see ClinicianAppointmentRequestLogicController acceptAppointment(), updateAppointment()
     */
    @FXML
    private void acceptAppointment() {
        Appointment selectedAppointment = getSelectedAppointment();
        if (selectedAppointment == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to accept");
            return;
        }
        boolean valid = true;

        if (appointmentRequestTime.getSelectionModel().getSelectedItem() == null){
            valid = false;
            AlertWindowFactory.generateInfoWindow("please pick a time");
        } else {

            if (!AttributeValidation.validateTimeString(appointmentRequestTime.getValue().toString())) {
                appointmentRequestTime.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
                valid = false;
            }
        }

        if (!AttributeValidation.validateDateOfAppointment(appointmentRequestDate.getValue())) {
            appointmentRequestDate.setStyle("-fx-background-color: rgba(100%, 0%, 0%, 0.25); -fx-border-color: RED");
            valid = false;
        }

        AppointmentStatus status = selectedAppointment.getAppointmentStatus();

        if (valid) {
            if (status == AppointmentStatus.PENDING) {
                logicController.updateAppointment(selectedAppointment, appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getValue().toString(), appointmentRequestDescription.getText(), true);
            } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
                logicController.updateAppointment(selectedAppointment, appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getValue().toString(), appointmentRequestDescription.getText(), false);
            } else {
                AlertWindowFactory.generateInfoWindow("This appointment is no longer available");
            }
        }


    }

    /**
     * @see ClinicianAppointmentRequestLogicController refreshClinicianAvailableTimes
     */
    @FXML
    private void populateClinicianTimes(){
        if (appointmentRequestDate.getValue() != null && getSelectedAppointment() != null) {
            LocalTime localTime = getSelectedAppointment().getRequestedDate().toLocalTime();
            logicController.refreshClinicianAvailableTimes(appointmentRequestDate.getValue());
            availableTimes.add(localTime);
            appointmentRequestTime.setItems(availableTimes);
        }

    }

    /**
     * Grabs and returns the selected appointment from the table
     *
     * @return the currently selected appointment
     */
    private Appointment getSelectedAppointment() {
        if (clinicianAppointmentsRequestView.isVisible()) {
            return clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem();
        } else {
            if (calendarView.getSelections().iterator().hasNext()) {
                return (Appointment) calendarView.getSelections().iterator().next().getUserObject();
            } else {
                return null;
            }
        }
    }

    /**
     * Removes the property change listener on logout so user appointment events do not trigger the
     * clinician tables to update.
     */
    public void shutdownPropertyChangeListener() {
        ServerEventNotifier.getInstance().removePropertyChangeListener(logicController);
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
