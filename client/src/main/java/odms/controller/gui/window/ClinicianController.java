package odms.controller.gui.window;

import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._abstract.TransplantWaitListViewer;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.Organs;
import odms.commons.model.dto.UserOverview;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.StatusBarController;
import odms.controller.gui.UnsavedChangesAlert;
import odms.controller.gui.panel.TransplantWaitListController;
import odms.controller.gui.popup.DeletedUserController;
import odms.controller.gui.popup.utils.AlertWindowFactory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import static odms.commons.utils.PhotoHelper.deleteTempDirectory;
import static odms.commons.utils.PhotoHelper.displayImage;

/**
 * Class for the functionality of the Clinician view of the application
 */
public class ClinicianController implements PropertyChangeListener, TransplantWaitListViewer {

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
    private Tooltip searchToolTip;
    @FXML
    private TableView<UserOverview> searchTableView;


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
    private Button redoButton;
    @FXML
    private MenuItem deleteClinician;
    @FXML
    private MenuItem logoutMenuClinician;

    @FXML
    private ImageView profileImage;
    @FXML
    private StatusBarController statusBarPageController;

    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Clinician clinician;
    private Collection<UserOverview> users;
    private ArrayList<Stage> openStages;
    private FilteredList<UserOverview> fListUsers;
    private PauseTransition pause = new PauseTransition(Duration.millis(300));


    //Initiliase table columns as class level so it is accessible for sorting in pagination methods
    private TableColumn<UserOverview, String> lNameColumn;
    private boolean filterVisible = false;
    private static final int ROWS_PER_PAGE = 30;
    private int startIndex = 0;
    private int endIndex;
    private int searchCount;

    private Collection<PropertyChangeListener> parentListeners;

    private boolean admin = false;

    /**
     * Initializes the controller class for the clinician overview.
     *
     * @param stage         The applications stage.
     * @param appController the applications controller.
     * @param clinician     The current clinician.
     */
    public void init(Stage stage, AppController appController, Clinician clinician, boolean fromAdmin,
                     Collection<PropertyChangeListener> parentListeners) {
        this.appController = appController;
        this.stage = stage;
        this.clinician = clinician;
        this.admin = fromAdmin;

        //add change listeners of parent controllers to the current clinician
        this.parentListeners = new ArrayList<>();
        if (parentListeners != null && !parentListeners.isEmpty()) {
            for (PropertyChangeListener listener : parentListeners) {
                clinician.addPropertyChangeListener(listener);
            }
            this.parentListeners.addAll(parentListeners);
        }
        stage.setResizable(true);
        showClinician(clinician);
        appController.getUserBridge().getUsers(0, 30, "", "", "", appController.getToken());
        searchCount = appController.getUserOverviews().size();
        initSearchTable();
        transplantWaitListTabPageController.init(appController, this);
        statusBarPageController.init(appController);

        if (clinician.getStaffId().equals("0")) {
            deleteClinician.setDisable(true);
        }

        setDefaultFilters();
        openStages = new ArrayList<>();

        if (fromAdmin) {
            logoutMenuClinician.setText("Go Back");
            logoutMenuClinician.setOnAction(e -> goBack());
        } else {
            logoutMenuClinician.setText("Log Out");
            logoutMenuClinician.setOnAction(e -> logout());
        }

        displayImage(profileImage, clinician.getProfilePhotoFilePath());

    }

    /**
     * Closes the clinician window
     */
    @FXML
    private void goBack() {
        checkSave();
        stage.close();
        Log.info("Successfully closed update user window for Clinician StaffID: " + clinician.getStaffId());
    }

    private void setDefaultFilters() {
        allCheckBox.setSelected(true);
    }

    /**
     * initialises the clinicians details
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
        if (clinician.getProfilePhotoFilePath() != null) {
            File inFile = new File(clinician.getProfilePhotoFilePath());
            Image image = new Image("file:" + inFile.getPath(), 200, 200, false, true);
            profileImage.setImage(image);
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
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

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
        //squished.filtered(user -> !user.isDeleted())
        sListUsers.comparatorProperty().bind(searchTableView.comparatorProperty());

        searchTableView.setItems(sListUsers);
        searchTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                UserOverview user = searchTableView.getSelectionModel().getSelectedItem();
                launchUser(user);
            }
        });
        searchTableView.refresh();
    }

    /**
     * @param userOverview A summary of the user to be launched
     */
    public void launchUser(UserOverview userOverview) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            User user = appController.getUserBridge().getUser(userOverview.getNhi());
            root = userLoader.load();
            Stage userStage = new Stage();
            userStage.setScene(new Scene(root));
            openStages.add(userStage);
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            parentListeners.add(this);
            userController.init(AppController.getInstance(), user, userStage, true, parentListeners);
            userStage.show();
            Log.info("Clinician " + clinician.getStaffId()
                    + " successfully launched user overview window");

            ArrayList<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            userController.init(AppController.getInstance(), user, userStage, true, listeners);
            userStage.show();
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
        appController.getUserBridge().getUsers(startIndex, ROWS_PER_PAGE, searchTextField.getText(), regionSearchTextField.getText(), genderComboBox.getValue(), appController.getToken());
        appController.setUserOverviews(appController.getUserOverviews().stream().filter(p -> (p.getDonating().isEmpty() != donorFilterCheckBox.isSelected() &&
                p.getReceiving().isEmpty() != receiverFilterCheckBox.isSelected()) || allCheckBox.isSelected()).collect(Collectors.toSet()));
        searchCount = appController.getUserOverviews().size();
        endIndex = Math.min(startIndex + ROWS_PER_PAGE, users.size());
        displaySearchTable();
        searchCountLabel.setText("Showing results " + (searchCount == 0 ? startIndex : startIndex + 1) + " - " + (endIndex) + " of " + searchCount);
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
    void logout() {
        checkSave();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loader.getController();
            loginController.init(AppController.getInstance(), newStage);
            try {
                deleteTempDirectory();
            } catch (IOException e){
                System.err.println(e);
            }
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
     * Callback method to refresh the tables in the view
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
            clinician.setDeleted(true);
            if (!admin) {
                appController.deleteClinician(clinician);
                logout();
            } else {
                stage.close();
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //clinician controller watches user model
        //refresh view/tables etc. on change
        if (evt.getPropertyName().equals(EventTypes.USER_UPDATE.name())) {
            refreshTables();
        }
    }

    @FXML
    private void clinicianSearchNextPage() {
        if (appController.getUserOverviews().size() < ROWS_PER_PAGE) {
            return;
        }

        startIndex += ROWS_PER_PAGE;
        search();
    }

    @FXML
    private void clinicianSearchPrevPage() {
        if (startIndex - ROWS_PER_PAGE < 0) {
            return;
        }

        startIndex -= ROWS_PER_PAGE;
        search();
    }
}
