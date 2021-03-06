package odms.controller.gui.window;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import odms.bridge.AppointmentsBridge;
import odms.bridge.ClinicianBridge;
import odms.commons.config.ConfigPropertiesSession;
import odms.commons.exception.ApiException;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.AppointmentStatus;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.Organs;
import odms.commons.model._enum.UserType;
import odms.commons.model.dto.UserOverview;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.StatusBarController;
import odms.controller.gui.UnsavedChangesAlert;
import odms.controller.gui.panel.TransplantWaitListController;
import odms.controller.gui.panel.view.AvailableOrgansViewController;
import odms.controller.gui.panel.view.ClinicianAppointmentRequestViewController;
import odms.controller.gui.popup.DeletedUserController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.controller.gui.widget.CountableLoadingTableView;
import odms.socket.ServerEventNotifier;
import utils.StageIconLoader;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.YEARS;
import static odms.commons.utils.PhotoHelper.deleteTempDirectory;
import static odms.commons.utils.PhotoHelper.displayImage;

/**
 * Class for the functionality of the Clinician view of the application
 */
public class ClinicianController implements PropertyChangeListener, UserLauncher {

    //<editor-fold desc="FXML declarations">
    @FXML
    private Button undoButton;
    @FXML
    private Button backButton;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Label fNameLabel;
    @FXML
    private Label mNameLabel;
    @FXML
    private Label lNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private Label zipLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private TextField searchTextField;
    @FXML
    private CountableLoadingTableView<UserOverview> searchTableView;

    @FXML
    private Label searchCountLabel;
    @FXML
    private AnchorPane filterAnchorPane;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private TextField regionSearchTextField;
    @FXML
    private CheckBox donorFilterCheckBox;
    @FXML
    private CheckBox receiverFilterCheckBox;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private Button expandButton;
    @FXML
    private TransplantWaitListController transplantWaitListTabPageController;
    @FXML
    private AvailableOrgansViewController availableOrgansViewController;
    @FXML
    private ClinicianAppointmentRequestViewController appointmentRequestViewController;
    @FXML
    private Button redoButton;
    @FXML
    private MenuItem deleteClinician;
    @FXML
    private MenuItem logoutMenuClinician;
    @FXML
    private ImageView profileImage;
    @FXML
    private StatusBarController statusBarPageController;

    @FXML Tab appointmentsTab;

    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Clinician clinician;
    private Collection<UserOverview> users;
    private ArrayList<Stage> openStages;
    private FilteredList<UserOverview> fListUsers;
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private ClinicianBridge clinicianBridge;
    private StackPane notificationBadge = new StackPane();

    //Initiliase table columns as class level so it is accessible for sorting in pagination methods
    private TableColumn<UserOverview, String> lNameColumn;
    private boolean filterVisible = false;
    private static final int ROWS_PER_PAGE = 30;
    private int startIndex = 0;
    private int endIndex;
    private int searchCount;
    private Collection<PropertyChangeListener> parentListeners;

    private boolean admin = false;
    private AppointmentsBridge appointmentsBridge;

    /**
     * Initializes the controller class for the clinician overview.
     *
     * @param stage         The applications stage.
     * @param appController the applications controller.
     * @param clinician     The current clinician.
     * @param parentListeners The listeners of the parent controller that created this
     * @param fromAdmin     If the user opening the clinician is from an admin
     */
    public void init(Stage stage, AppController appController, Clinician clinician, boolean fromAdmin,
                     Collection<PropertyChangeListener> parentListeners) {
        this.appController = appController;
        this.clinicianBridge = appController.getClinicianBridge();
        this.stage = stage;
        this.clinician = clinician;
        this.admin = fromAdmin;
        this.appointmentsBridge = appController.getAppointmentsBridge();
        openStages = new ArrayList<>();
        stage.setMaximized(true);

        ServerEventNotifier.getInstance().addPropertyChangeListener(this);
        setDefaultFilters();
        stage.setResizable(true);
        showClinician(clinician);
        searchTableView.setWaiting(true);
        appController.getUserBridge().getUsers(0, ROWS_PER_PAGE, "", "", "", appController.getToken(), searchTableView);
        searchCount = appController.getUserOverviews().size();
        initSearchTable();
        searchTableView.countProperty().addListener(((observable, oldValue, newValue) -> {
            updateCountLabel((int) newValue);
        }));
        transplantWaitListTabPageController.init(appController, this);
        statusBarPageController.init();
        availableOrgansViewController.init(this);
        appointmentRequestViewController.init(appController, clinician);

        if (clinician.getStaffId().equals("0")) {
            deleteClinician.setDisable(true);
        }

        if (fromAdmin) {
            logoutMenuClinician.setText("Go Back");
            logoutMenuClinician.setOnAction(e -> goBack());
            try {
                // ༼ つ ◕ ◕ ༽つ FIX APP ༼ つ ◕ ◕ ༽つ
                clinician.setProfilePhotoFilePath(appController.getClinicianBridge().getProfilePicture(clinician.getStaffId(), appController.getToken()));
            } catch (IOException e) {
                ClassLoader classLoader = getClass().getClassLoader();
                File inFile = new File(classLoader.getResource("default-profile-picture.jpg").getFile());
                clinician.setProfilePhotoFilePath(inFile.getPath());
            }
        } else {
            logoutMenuClinician.setText("Log Out");
            logoutMenuClinician.setOnAction(e -> logout());
        }

        EventHandler<WindowEvent> closeEvent = stage.getOnCloseRequest();
        stage.setOnCloseRequest(e -> {
            availableOrgansViewController.shutdownThreads();
            try  {
                closeEvent.handle(e);
            } catch (NullPointerException ee) {
                Log.warning(ee.toString());
            }
        });

        displayImage(profileImage, clinician.getProfilePhotoFilePath());
        if (ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equals("false")) {
            if (clinician.getProfilePhotoFilePath() == null || clinician.getProfilePhotoFilePath().equals("")) {
                URL url = getClass().getResource("/default-profile-picture.jpg");
                displayImage(profileImage, url);
            } else {
                displayImage(profileImage, clinician.getProfilePhotoFilePath());
            }
        }

        showAppointmentNotifications();
        checkForCanceledAppointments();
    }

    /**
     * Finds the nmber of pending appointments for a clinician and shows it to them
     *
     * Will show 9+ for notifications over 10 due to size constraints
     *
     */
    private void showAppointmentNotifications() {

        int notificationsPending = appointmentsBridge.getPendingAppointments(clinician.getStaffId(),appController.getToken());
        String notifications;
        Text numberOfNotifications = new Text();
        if(notificationsPending <= 0 ){
            return;
        } else if(notificationsPending > 9){
            notifications = "9+";
            numberOfNotifications.setFont(new Font(8));
        } else {
            notifications = String.valueOf(notificationsPending);
        }
        Circle notificationCircle = new Circle(0, 0, 10);
        notificationCircle.setFill(Color.RED);
        numberOfNotifications.setText(notifications);
        numberOfNotifications.setBoundsType(TextBoundsType.VISUAL);

        notificationBadge.getChildren().add(notificationCircle);
        notificationBadge.getChildren().add(numberOfNotifications);
        appointmentsTab.setGraphic(notificationBadge);
    }

    /**
     * Asks the server if there are any canceled appointments for the clinician and notifies them if there are
     */
    private void checkForCanceledAppointments() {
        boolean hasCanceled = appController.getAppointmentsBridge().checkAppointmentStatusExists(clinician.getStaffId(), UserType.CLINICIAN, AppointmentStatus.CANCELLED_BY_USER);
        if (hasCanceled) {
            String message = "You have appointments that have been cancelled. Please check your list of appointments.";
            AlertWindowFactory.generateAlertWindow(message);
        }
    }

    /**
     * Closes the clinician window
     */
    @FXML
    private void goBack() {
        checkSave();
        stage.close();
        availableOrgansViewController.shutdownThreads();
        appointmentRequestViewController.shutdownPropertyChangeListener();
        ServerEventNotifier.getInstance().removePropertyChangeListener(this);
        Log.info("Successfully closed update user window for Clinician StaffID: " + clinician.getStaffId());
    }

    private void setDefaultFilters() {
        allCheckBox.setSelected(true);
    }

    /**
     * initialises the clinicians details
     * @param clinician clinician to show the details of
     */
    public void showClinician(Clinician clinician) {
        this.clinician = clinician;
        staffIdLabel.setText(clinician.getStaffId());
        fNameLabel.setText(clinician.getFirstName());
        mNameLabel.setText(clinician.getMiddleName());
        lNameLabel.setText(clinician.getLastName());
        addressLabel.setText(clinician.getWorkContactDetails().getAddress().getStringAddress());
        cityLabel.setText(clinician.getCity());
        regionLabel.setText(clinician.getRegion());
        zipLabel.setText(clinician.getZipCode());
        countryLabel.setText(clinician.getCountry());
        if (clinician.getFirstName() == null) {
            stage.setTitle("Clinician: Admin");
        } else if (clinician.getLastName() == null) {
            stage.setTitle("Clinician: " + clinician.getFirstName());
        } else {
            stage.setTitle("Clinician: " + clinician.getFirstName() + " " + clinician.getLastName());
        }
        undoButton.setDisable(clinician.getUndoStack().empty());
        redoButton.setDisable(clinician.getRedoStack().empty());
        if (!clinician.getChanges().isEmpty()) {
            statusBarPageController.updateStatus(clinician.getStaffId() + " " + clinician.getChanges().get(clinician.getChanges().size() - 1).getChange());

        }
        if (ConfigPropertiesSession.getInstance().getProperty("testConfig", "false").equals("false")) {
            if (clinician.getProfilePhotoFilePath() == null || clinician.getProfilePhotoFilePath().equals("")) {
                URL url = getClass().getResource("/default-profile-picture.jpg");
                displayImage(profileImage, url);
            } else {
                File inFile = new File(clinician.getProfilePhotoFilePath());
                Image image = new Image("file:" + inFile.getPath(), 200, 200, false, true);
                profileImage.setImage(image);
            }
        }
    }

    /**
     * initialises the search table, abstracted from main init function for clarity
     */
    @FXML
    private void initSearchTable() {
        TableColumn<UserOverview, String> fNameColumn;
        TableColumn<UserOverview, String> dobColumn;
        TableColumn<UserOverview, String> dodColumn;
        TableColumn<UserOverview, String> ageColumn;
        TableColumn<UserOverview, HashSet<Organs>> organsColumn;
        TableColumn<UserOverview, String> regionColumn;

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, appController.getUserOverviews().size());

        fNameColumn = new TableColumn<>("First name");
        fNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFirstName()));

        lNameColumn = new TableColumn<>("Last name");
        lNameColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLastName()));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));

        dodColumn = new TableColumn<>("Date of Death");
        dodColumn.setCellValueFactory(new PropertyValueFactory<>("dod"));

        ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(c -> new SimpleStringProperty(Long.toString(YEARS.between(c.getValue().getDob(), LocalDateTime.now()))));

        regionColumn = new TableColumn<>("Region");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        organsColumn = new TableColumn<>("Organs");
        organsColumn.setCellValueFactory(new PropertyValueFactory<>("donating"));

        //add more columns as wanted/needed

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //set table columns and contents
        searchTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        if (appController.getUserOverviews().isEmpty()) {
            return;
        }
        searchTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        displaySearchTable();
        //set on-click behaviour
    }

    @FXML
    private void displaySearchTable() {
        //set up lists
        //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
        ObservableList<UserOverview> oListUsers = FXCollections.observableList(new ArrayList<>(appController.getUserOverviews()));

        fListUsers = new FilteredList<>(oListUsers);
        fListUsers = filter(fListUsers);
        FilteredList<UserOverview> squished = new FilteredList<>(fListUsers);

        SortedList<UserOverview> sListUsers = new SortedList<>(squished);
        sListUsers.comparatorProperty().bind(searchTableView.comparatorProperty());

        searchTableView.setItems(sListUsers);
        searchTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 && !searchTableView.getSelectionModel().getSelectedItems().isEmpty()) {
                UserOverview user = searchTableView.getSelectionModel().getSelectedItem();
                launchUser(user.getNhi());
            }
        });
        searchTableView.refresh();
    }

    /**
     * @param userNhi A summary of the user to be launched
     */
    public void launchUser(String userNhi) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            User user = appController.getUserBridge().getUser(userNhi);
            root = userLoader.load();
            Stage userStage = new Stage();
            userStage.setScene(new Scene(root));
            openStages.add(userStage);
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            userController.init(AppController.getInstance(), user, userStage, true, parentListeners);
            StageIconLoader stageIconLoader = new StageIconLoader();
            userStage = stageIconLoader.addStageIcon(userStage);
            userStage.show();
            Log.info("Clinician " + clinician.getStaffId()
                    + " successfully launched user overview window");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " Failed to load user overview window", e);
            AlertWindowFactory.generateError(e);
        }
    }

    /**
     * applies a change listener to the input text box and filters a filtered list accordingly
     *
     * @param fListUsers list to be filtered
     * @return filtered list with filter applied
     */
    private FilteredList<UserOverview> filter(FilteredList<UserOverview> fListUsers) {
        setTextFieldListener(searchTextField);
        setTextFieldListener(regionSearchTextField);
        setCheckBoxListener(donorFilterCheckBox);
        setCheckBoxListener(receiverFilterCheckBox);
        setCheckBoxListener(allCheckBox);
        genderComboBox.valueProperty()
                .addListener(observable -> {
                    startIndex = 0;
                    pause.setOnFinished(e -> search());
                    pause.playFromStart();
                });

        return fListUsers;
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param inputTextField textfield to add the listener to
     */
    private void setTextFieldListener(TextField inputTextField) {
        inputTextField.textProperty()
                .addListener(observable -> {
                    startIndex = 0;
                    pause.setOnFinished(e -> search());
                    pause.playFromStart();
                });
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param checkBox   checkBox object to add the listener to
     */
    private void setCheckBoxListener(CheckBox checkBox) {
        checkBox.selectedProperty()
                .addListener(observable -> {
                    startIndex = 0;
                    pause.setOnFinished(e -> search());
                    pause.playFromStart();
                });
    }

    /**
     * Sends a request to the server to obtain the user overviews for the search table
     */
    private void search() {
        appController.getUserOverviews().clear();
        appController.getUserBridge().getUsers(startIndex, ROWS_PER_PAGE, searchTextField.getText(), regionSearchTextField.getText(), genderComboBox.getValue(), appController.getToken(), searchTableView);
        searchTableView.setWaiting(true);
        appController.setUserOverviews(appController.getUserOverviews().stream().filter(p -> (p.getDonating().isEmpty() != donorFilterCheckBox.isSelected() &&
                p.getReceiving().isEmpty() != receiverFilterCheckBox.isSelected()) || allCheckBox.isSelected()).collect(Collectors.toSet()));
        displaySearchTable();
    }

    /**
     * Undoes the last action and redisplays the clinician.
     */
    @FXML
    private void undo() {
        clinician.undo();
        undoButton.setDisable(clinician.getUndoStack().empty());
        showClinician(clinician);
        Log.info("Clinician " + clinician.getStaffId() + " executed undo clinician");
    }

    /**
     * Redoes the last action and redisplays the clinician.
     */
    @FXML
    public void redo() {
        clinician.redo();
        redoButton.setDisable(clinician.getRedoStack().empty());
        showClinician(clinician);
        Log.info("Clinician " + clinician.getStaffId() + " executed redo clinician");
    }

    /**
     * Save all changes for the current clinician
     */
    @FXML
    void save() {
        appController.updateClinicians(clinician);
        try {
            appController.saveClinician(clinician);
        } catch (IOException e) {
            AlertWindowFactory.generateError(e);
            return;
        }
        clinician.getUndoStack().clear();
        clinician.getRedoStack().clear();
        undoButton.setDisable(true);
        redoButton.setDisable(true);
    }

    /**
     * Returns the user to the login screen
     * When fired, it also deleted the temp folder.
     */
    @FXML
    private void logout() {
        checkSave();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            StageIconLoader stageIconLoader = new StageIconLoader();
            newStage = stageIconLoader.addStageIcon(newStage);
            newStage.show();
            stage.close();
            availableOrgansViewController.shutdownThreads();
            appointmentRequestViewController.shutdownPropertyChangeListener();
            ServerEventNotifier.getInstance().removePropertyChangeListener(this);
            LoginController loginController = loader.getController();
            loginController.init(AppController.getInstance(), newStage);
            deleteTempDirectory();
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched login window after logout");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch login window after logout", e);
        }
    }

    /**
     * Popup that prompts the clinician to save any unsaved changes before logging out or exiting the application
     */
    private void checkSave() {
        boolean noChanges = clinician.getUndoStack().isEmpty();
        if (!noChanges) {
            Optional<ButtonType> result = UnsavedChangesAlert.getAlertResult();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                appController.updateClinicians(clinician);
                try {
                    appController.saveClinician(clinician);
                } catch (IOException e) {
                    AlertWindowFactory.generateError(e);
                    return;
                }
            } else {
                Clinician revertClinician = clinician.getUndoStack().firstElement().getState();
                appController.updateClinicians(revertClinician);
            }
        }

        clinician.getUndoStack().clear();
        clinician.getRedoStack().clear();
    }

    /**
     * Opens an edit window for the clinicians personal details
     */
    @FXML
    void edit() {
        FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
        Parent root;
        try {
            root = updateLoader.load();
            UpdateClinicianController updateClinicianController = updateLoader.getController();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            updateClinicianController.init(clinician, appController, stage, false, newStage);
            newStage.initModality(Modality.APPLICATION_MODAL); // background window is no longer selectable
            StageIconLoader stageIconLoader = new StageIconLoader();
            newStage = stageIconLoader.addStageIcon(newStage);
            newStage.showAndWait();
            showClinician(clinician);
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched update clinician window");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch update clinician window", e);
        }
    }

    /**
     * Callback method to change the divider position to show advanced filtering options in the GUI
     */
    @FXML
    private void expandFilter() {
        double dividerPos = filterVisible ? 44 : 150;
        filterAnchorPane.setMinHeight(dividerPos);
        filterAnchorPane.setMaxHeight(dividerPos);
        filterVisible = !filterVisible;
        expandButton.setText(filterVisible ? "▲" : "▼");
    }

    /**
     * Callback method to refresh the tables and current clinician
     */
    @FXML
    public void refreshTables() {
        transplantWaitListTabPageController.displayWaitListTable();
        displaySearchTable();
    }

    /**
     * Loads the recently deleted users window
     */
    @FXML
    public void loadRecentlyDeleted() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            deletedUserController.init(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            StageIconLoader stageIconLoader = new StageIconLoader();
            stage = stageIconLoader.addStageIcon(stage);
            stage.showAndWait();
            Log.info("Clinician " + clinician.getStaffId() + " successfully launched delete user window");
        } catch (IOException e) {
            Log.severe("Clinician " + clinician.getStaffId() + " failed to launch delete user window", e);
        }
    }

    /**
     * Deletes the clinician profile after confirmation.
     */
    @FXML
    private void deleteClinician() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this clinician?");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            appController.deleteClinician(clinician);
            clinician.setDeleted(true);
            if (!admin) {
                logout();
            } else {
                stage.close();
                availableOrgansViewController.shutdownThreads();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //clinician controller watches user model
        //refresh view/tables etc. on change
        Log.info("refresh listener fired in clinician controller");
        UpdateNotificationEvent event;
        try {
            event = (UpdateNotificationEvent) evt;
        } catch (ClassCastException ex) {
            return;
        }
        if (event == null) {
            return;
        }

        if (event.getType().equals(EventTypes.USER_UPDATE)) {
            search();
            refreshTables();
            transplantWaitListTabPageController.populateWaitListTable();
            transplantWaitListTabPageController.displayWaitListTable();
            availableOrgansViewController.search();
        } else if (event.getType().equals(EventTypes.CLINICIAN_UPDATE) && clinician.getStaffId().equals(event.getOldIdentifier())){
            String newStaffId = event.getNewIdentifier();
            try {
                this.clinician = clinicianBridge.getClinician(newStaffId, appController.getToken());
                if (clinician != null) {
                    showClinician(clinician); //TODO: fix when we solve the database race 7/8/18 jb
                }
            } catch (ApiException ex) {
                Log.warning("failed to retrieve updated clinician. response code: " + ex.getResponseCode(), ex);
                AlertWindowFactory.generateError(("could not refresh clinician from the server. Please check your connection before trying again."));
            }
        } else if (event.getType().equals(EventTypes.APPOINTMENT_UPDATE)) {
            showAppointmentNotifications();
        }
    }

    @FXML
    private void clinicianSearchNextPage() {
        if (appController.getUserOverviews().size() < ROWS_PER_PAGE) {
            return;
        }

        startIndex += ROWS_PER_PAGE;
        updateCountLabel(searchTableView.getCount());
        search();
    }

    @FXML
    private void clinicianSearchPrevPage() {
        if (startIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startIndex -= ROWS_PER_PAGE;
        updateCountLabel(searchTableView.getCount());
        search();
    }

    private void updateCountLabel(int countValue) {
        endIndex = Math.min(startIndex + ROWS_PER_PAGE, countValue);
        searchCountLabel.setText("Showing results " + (searchCount != 0 ? startIndex : startIndex + 1) + " - " + (endIndex) + " of " + countValue);
    }
}
