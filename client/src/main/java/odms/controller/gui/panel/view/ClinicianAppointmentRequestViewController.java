package odms.controller.gui.panel.view;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import odms.commons.config.ConfigPropertiesSession;
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
import odms.controller.gui.widget.CalendarEntryFactory;
import odms.controller.gui.widget.CalendarWidget;
import odms.controller.gui.widget.CalendarWidgetFactory;
import odms.controller.gui.widget.CountableLoadingTableView;
import odms.socket.ServerEventNotifier;
import utils.Converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ClinicianAppointmentRequestViewController implements Converter {


    @FXML
    private CountableLoadingTableView<Appointment> clinicianAppointmentsRequestView;

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
    private Control appointmentRequestUserNhi;

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

    @FXML
    private GridPane formPane;

    private CalendarWidget calendarView;


    private ObservableList<Appointment> availableAppointments = FXCollections.observableList(new ArrayList<>());
    private ObservableList<LocalTime> availableTimes = FXCollections.observableList(new ArrayList<>());
    private ClinicianAppointmentRequestLogicController logicController;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd hh:mm");
    private ChangeListener<String> titleChangeListener;
    private Entry<Appointment> selectedEntry;
    private ObjectProperty<Appointment> selectedAppointment;

    private static final double TITLE_CONTROL_FORM_PREF_WIDTH = 218.0;
    private static final double TITLE_CONTROL_PREF_HEIGHT = 15.0;
    private static final double TITLE_CONTROL_TOP_ANCHOR = 109.0;
    private static final double TITLE_CONTROL_RIGHT_ANCHOR = 49;

    /**
     * Initialises the panel
     * @param appController the instance of appController
     * @param clinician clinician to initialise the tab with
     */
    public void init(AppController appController, Clinician clinician) {
        availableAppointments.addListener((ListChangeListener<? super Appointment>) observable -> {
            clinicianAppointmentsRequestView.setWaiting(false);
            populateTable(false);
            populateCalendar();
        });
        logicController = new ClinicianAppointmentRequestLogicController(availableAppointments, appController, clinician, availableTimes, clinicianAppointmentsRequestView);
        appointmentRequestDescription.setTextFormatter(new TextFormatter<String>(change ->
                change.getControlNewText().length() <= 255 ? change : null)); // limits user input to 255 characters
        for (AppointmentCategory category : AppointmentCategory.values()) {
            appointmentRequestCategory.getItems().add(category);
        }

        initAppointmentTable();

        populateClinicianTimes();
        datePickerListener(appointmentRequestDate);
        initCalendar();
        selectedAppointmentProperty().addListener((observable, oldValue, newValue) -> displayAppointmentDetails(newValue));
    }

    /**
     * Initialises the calendarView and sets the right anchors for the calendarView
     */
    private void initCalendar() {
        if (ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equals("false")) {
            calendarView = CalendarWidgetFactory.createCalendar();
            calendarViewPane.getChildren().add(calendarView);
            AnchorPane.setTopAnchor(calendarView, 0.0);
            AnchorPane.setBottomAnchor(calendarView, 0.0);
            AnchorPane.setLeftAnchor(calendarView, 0.0);
            AnchorPane.setRightAnchor(calendarView, 0.0);
            populateCalendar();
        }
    }

    /**
     * Changes the title bar to add/remove an asterisk when a change was detected on the date picker.
     *
     * @param dp The current date picker.
     */
    private void datePickerListener(DatePicker dp) {
        dp.valueProperty().addListener((observable, oldValue, newValue) -> populateClinicianTimes());
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
        clinicianAppointmentsRequestView.setWaiting(true);
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
        clinicianAppointmentsRequestView.refresh();
    }

    /**
     * Populates the calendarFX calendar with entries of the given appointments
     */
    private void populateCalendar() {
        if (calendarView != null) {
            calendarView.getCalendarSources().forEach(cs -> cs.getCalendars().forEach(Calendar::clear));
            for (Appointment appointment : availableAppointments) {
                if (appointment.getAppointmentStatus().equals(AppointmentStatus.ACCEPTED) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.ACCEPTED_SEEN) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.PENDING) ||
                        appointment.getAppointmentStatus().equals(AppointmentStatus.UPDATED)) {

                    Entry<Appointment> entry = CalendarEntryFactory.generateEntry(appointment);
                    calendarView.addEntry(entry);
                    if (selectedEntry != null && entry.getUserObject().getAppointmentId().equals(selectedEntry.getUserObject().getAppointmentId())) {
                        selectedEntry = entry;
                        calendarView.getSelections().add(entry);
                    }
                }
            }

            addListenerToCalendarView(calendarView);
        }
    }

    /**
     * Adds a listener to the calendarView provided to show the appointment details of the selected entry
     *
     * @param calendarView calendar view to add the listener to.
     */
    private void addListenerToCalendarView(CalendarView calendarView) {
        calendarView.getSelections().addListener((SetChangeListener<Entry<?>>) change -> {
            if (calendarView.getSelections().size() == 1 && change.getElementAdded() != null) {
                selectedEntry = (Entry<Appointment>) change.getElementAdded();
                titleChangeListener = (observable, oldValue, newValue) -> selectedEntry.setTitle(newValue);
                Appointment appointment = selectedEntry.getUserObject();
                setSelectedAppointment(appointment);
                populateClinicianTimes();
                if (getSelectedAppointment().getAppointmentCategory().equals(AppointmentCategory.PERSONAL))
                    ((TextField) appointmentRequestUserNhi).textProperty().addListener(titleChangeListener);
                rejectAppointmentButton.setText("Cancel Appointment");
                acceptAppointmentButton.setText("Update Appointment");
            } else {
                if (getSelectedAppointment() != null && getSelectedAppointment().getAppointmentCategory().equals(AppointmentCategory.PERSONAL))
                    ((TextField) appointmentRequestUserNhi).textProperty().removeListener(titleChangeListener);
                selectedEntry = null;
                setSelectedAppointment(null);
            }
        });
    }

    /**
     * Binds a table view row to show all details for that appointment
     */
    private void setTableOnClickBehaviour() {
        clinicianAppointmentsRequestView.getSelectionModel().selectedItemProperty().addListener(a -> {
            setSelectedAppointment(clinicianAppointmentsRequestView.getSelectionModel().getSelectedItem());
            if (getSelectedAppointment() != null) {


                if (getSelectedAppointment().getAppointmentStatus() == AppointmentStatus.CANCELLED_BY_USER) {
                    getSelectedAppointment().setAppointmentStatus(AppointmentStatus.CANCELLED_BY_USER_SEEN);
                    AppController.getInstance().getAppointmentsBridge().patchAppointmentStatus(getSelectedAppointment().getAppointmentId(),
                            AppointmentStatus.CANCELLED_BY_USER_SEEN.getDbValue());
                }

                if (getSelectedAppointment().getAppointmentStatus() == AppointmentStatus.ACCEPTED ||
                        getSelectedAppointment().getAppointmentStatus() == AppointmentStatus.ACCEPTED_SEEN) {
                    rejectAppointmentButton.setText("Cancel Appointment");
                    acceptAppointmentButton.setText("Update Appointment");
                } else {
                    rejectAppointmentButton.setText("Reject Appointment");
                    acceptAppointmentButton.setText("Accept Appointment");
                }

                if (calendarView != null) {
                    LocalDate date = getSelectedAppointment().getRequestedDate().toLocalDate();
                    for (CalendarSource cs : calendarView.getCalendarSources()) {
                        for (Calendar c : cs.getCalendars()) {
                            for (List<Entry<?>> list : c.findEntries(date, date, ZoneId.of("NZ")).values()) {
                                for (Entry<?> entry : list) {
                                    if ((entry.getUserObject()).equals(getSelectedAppointment())) {
                                        calendarView.getSelections().clear();
                                        calendarView.getSelections().add(entry);

                                    }
                                }
                            }
                        }
                    }
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
                startEdit();
                ((TextField) appointmentRequestUserNhi).setText(appointment.getTitle());
            } else {
                appointmentDetailsNhiLabel.setText("NHI: ");
                stopEdit();
                ((Label) appointmentRequestUserNhi).setText(appointment.getRequestingUserId());
            }
            appointmentRequestStatus.setText(appointment.getAppointmentStatus().toString());
            appointmentRequestCategory.setValue(appointment.getAppointmentCategory());
            appointmentRequestDate.setValue(appointment.getRequestedDate().toLocalDate());
            appointmentRequestTime.setValue(appointment.getRequestedDate().toLocalTime());
            appointmentRequestDescription.setText(appointment.getRequestDescription());
        } else {
            stopEdit();
            appointmentDetailsNhiLabel.setText("NHI: ");
            ((Label) appointmentRequestUserNhi).setText("");
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
        if (getSelectedAppointment() == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment");
            return;
        }

        AppointmentStatus status = getSelectedAppointment().getAppointmentStatus();

        if (status == AppointmentStatus.PENDING) {
            logicController.rejectAppointment(getSelectedAppointment());

        } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN) {
            logicController.cancelAppointment(getSelectedAppointment());
        }
    }

    /**
     * @see ClinicianAppointmentRequestLogicController acceptAppointment(), updateAppointment()
     */
    @FXML
    private void acceptAppointment() {

        if (getSelectedAppointment() == null) {
            AlertWindowFactory.generateInfoWindow("You must select an appointment to accept");
            return;
        }
        boolean valid = true;
        resetAppointmentFields();
        if (appointmentRequestTime.getSelectionModel().getSelectedItem() == null){
            valid = false;
            AlertWindowFactory.generateInfoWindow("please pick a time");
        } else {

            if (!AttributeValidation.validateTimeString(appointmentRequestTime.getValue().toString()) || appointmentRequestTime.getValue().toString().equals("00:00")) {
                appointmentRequestTime.getStyleClass().add("invalid");
                valid = false;
            }
        }

        if (!AttributeValidation.validateDateOfAppointment(appointmentRequestDate.getValue())) {
            appointmentRequestDate.getStyleClass().add("invalid");
            valid = false;
        }

        AppointmentStatus status = getSelectedAppointment().getAppointmentStatus();

        if (valid) {
            if (status == AppointmentStatus.PENDING) {
                logicController.updateAppointment(getSelectedAppointment(), appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getValue().toString(), appointmentRequestDescription.getText(), true);
            } else if (status == AppointmentStatus.ACCEPTED || status == AppointmentStatus.ACCEPTED_SEEN || status == AppointmentStatus.UPDATED) {
                logicController.updateAppointment(getSelectedAppointment(), appointmentRequestCategory.getValue(),
                        appointmentRequestDate.getValue(), appointmentRequestTime.getValue().toString(), appointmentRequestDescription.getText(), false);
            } else {
                AlertWindowFactory.generateInfoWindow("This appointment is no longer available");
            }
        }

        if (calendarView != null) {
            calendarView.getSelections().clear();
        }


    }

    private void resetAppointmentFields() {


        appointmentRequestDate.getStyleClass().remove("invalid");
        appointmentRequestTime.getStyleClass().remove("invalid");
    }

    /**
     * @see ClinicianAppointmentRequestLogicController refreshClinicianAvailableTimes
     */
    @FXML
    private void populateClinicianTimes(){
        if (appointmentRequestDate.getValue() != null && getSelectedAppointment() != null) {
            LocalTime localTime = getSelectedAppointment().getRequestedDate().toLocalTime();
            logicController.refreshClinicianAvailableTimes(appointmentRequestDate.getValue(), getSelectedAppointment());
            availableTimes.add(localTime);
            appointmentRequestTime.setItems(availableTimes);
            availableTimes.addListener((ListChangeListener<LocalTime>) c -> appointmentRequestTime.setValue(getSelectedAppointment().getRequestedDate().toLocalTime()));
        }

    }

    /**
     * Grabs and returns the selected appointment from the table
     *
     * @return the currently selected appointment
     */
    private Appointment getSelectedAppointment() {
        return selectedAppointmentProperty().get();
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

    /**
     * @see Converter
     */
    @Override
    public void startEdit() {
        if (appointmentRequestUserNhi instanceof Label) {
            TextField temp = new TextField();
            temp.setText(((Label) appointmentRequestUserNhi).getText());
            temp.setPrefWidth(TITLE_CONTROL_FORM_PREF_WIDTH);
            temp.setPrefHeight(TITLE_CONTROL_PREF_HEIGHT);
            GridPane.setRowIndex(temp, GridPane.getRowIndex(appointmentRequestUserNhi));
            GridPane.setColumnIndex(temp, GridPane.getColumnIndex(appointmentRequestUserNhi));
            formPane.getChildren().replaceAll(node -> node.equals(appointmentRequestUserNhi) ? temp : node);
            appointmentRequestUserNhi = temp;
            AnchorPane.setTopAnchor(appointmentRequestUserNhi, TITLE_CONTROL_TOP_ANCHOR);
            AnchorPane.setRightAnchor(appointmentRequestUserNhi, TITLE_CONTROL_RIGHT_ANCHOR);
        }
    }

    /**
     * @see Converter
     */
    @Override
    public void stopEdit() {
        if (appointmentRequestUserNhi instanceof TextField) {
            Label temp = new Label(((TextField) appointmentRequestUserNhi).getText());
            temp.setPrefWidth(TITLE_CONTROL_FORM_PREF_WIDTH);
            temp.setPrefHeight(TITLE_CONTROL_PREF_HEIGHT);
            GridPane.setRowIndex(temp, GridPane.getRowIndex(appointmentRequestUserNhi));
            GridPane.setColumnIndex(temp, GridPane.getColumnIndex(appointmentRequestUserNhi));
            formPane.getChildren().replaceAll(node -> node.equals(appointmentRequestUserNhi) ? temp : node);
            appointmentRequestUserNhi = temp;
            AnchorPane.setTopAnchor(appointmentRequestUserNhi, TITLE_CONTROL_TOP_ANCHOR);
            AnchorPane.setRightAnchor(appointmentRequestUserNhi, TITLE_CONTROL_RIGHT_ANCHOR);
        }
    }

    /**
     * @return The selected appointment property of this window
     */
    public ObjectProperty<Appointment> selectedAppointmentProperty() {
        if (selectedAppointment == null) {
            selectedAppointment = new SimpleObjectProperty<>(null);
        }
        return selectedAppointment;
    }

    /**
     * @param selectedAppointment Appointment to set the selected appointment property to
     */
    public void setSelectedAppointment(Appointment selectedAppointment) {
        selectedAppointmentProperty().set(selectedAppointment);
    }
}
