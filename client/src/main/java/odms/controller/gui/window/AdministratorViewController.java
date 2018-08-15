package odms.controller.gui.window;

import com.sun.javafx.stage.StageHelper;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import odms.bridge.AdministratorBridge;
import odms.bridge.ClinicianBridge;
import odms.bridge.UserBridge;
import odms.commons.exception.ApiException;
import odms.commons.exception.InvalidFileException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._abstract.UserLauncher;
import odms.commons.model._enum.EventTypes;
import odms.commons.model._enum.Organs;
import odms.commons.model.dto.UserOverview;
import odms.commons.model.event.UpdateNotificationEvent;
import odms.commons.utils.CSVHandler;
import odms.commons.utils.DataHandler;
import odms.commons.utils.JsonHandler;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.FileSelectorController;
import odms.controller.gui.StatusBarController;
import odms.controller.gui.UnsavedChangesAlert;
import odms.controller.gui.panel.TransplantWaitListController;
import odms.controller.gui.panel.view.AvailableOrgansViewController;
import odms.controller.gui.popup.AlertUnclosedWindowsController;
import odms.controller.gui.popup.CountrySelectionController;
import odms.controller.gui.popup.DeletedUserController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.socket.ServerEventNotifier;
import odms.view.CLI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.util.*;

public class AdministratorViewController implements PropertyChangeListener, UserLauncher {

    //<editor-fold desc="FXML stuff">

    private static final int ROWS_PER_PAGE = 30;
    public static final String JSON = "*.json";
    public static final String FILE_NAME_NOT_FOUND = "File name not found";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";
    public static final String ERROR = "error";
    public static final String FAILED_TO_GET_USER_OVERVIEWS_FROM_SERVER = "failed to get user overviews from server";
    @FXML
    private TableView<UserOverview> userTableView;
    @FXML
    private Label adminLastNameLabel;
    @FXML
    private CheckBox allCheckBox;
    @FXML
    private TableView<Clinician> clinicianTableView;
    @FXML
    private TextField cliInputTextField;
    @FXML
    private TableView<Administrator> adminTableView;
    @FXML
    private TextArea adminCliTextArea;
    @FXML
    private AnchorPane filterAnchorPane;
    @FXML
    private Button adminRedoButton;
    @FXML
    private Label adminUsernameLable;
    @FXML
    private Button expandButton;
    @FXML
    private TextField adminSearchField;
    @FXML
    private Pagination searchTablePagination;
    @FXML
    private Button deleteAdminButton;
    @FXML
    private ComboBox<String> genderComboBox;
    @FXML
    private Label adminFirstnameLabel;
    @FXML
    private CheckBox donorFilterCheckBox;
    @FXML
    private Button adminUndoButton;
    @FXML
    private Label searchCountLabel;
    @FXML
    private Label adminMiddleNameLabel;
    @FXML
    private CheckBox receiverFilterCheckBox;
    @FXML
    private Label donorStatusLabel;
    @FXML
    private Label birthGenderLabel;
    @FXML
    private TextField regionSearchTextField;
    @FXML
    private Label fileNotFoundLabel;
    @FXML
    private RadioButton adminUserRadioButton;
    @FXML
    private RadioButton adminClinicianRadioButton;
    @FXML
    private RadioButton adminAdminRadioButton;
    @FXML
    private ToggleGroup adminSearchRadios;
    @FXML
    private Label regionLabel;
    @FXML
    private MenuItem deleteAdmin;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private AvailableOrgansViewController availableOrgansViewController;

    //</editor-fold>
    @FXML
    private TransplantWaitListController transplantWaitListTabPageController;
    @FXML
    private StatusBarController statusBarPageController;
    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -2;
    private boolean owner;
    private boolean filterVisible = false;
    private String messageAdmin = "Admin ";
    private DataHandler dataHandler = new JsonHandler();
    private UserBridge userBridge;
    private ClinicianBridge clinicianBridge;
    private AdministratorBridge adminBridge;
    private PauseTransition pause = new PauseTransition(Duration.millis(300));
    private Set<ClinicianController> clinicianControllers = new HashSet<>();
    private Set<AdministratorViewController> administratorViewControllers = new HashSet<>();
    private int userStartIndex = 0;
    private int clinicianStartIndex = 0;
    private int adminStartIndex = 0;

    /**
     * Initialises scene for the administrator view
     *
     * @param administrator administrator to view
     * @param appController appController instance to get data from
     * @param stage         stage to display on
     */
    public void init(Administrator administrator, AppController appController, Stage stage, boolean owner, Collection<PropertyChangeListener> parentListeners) {
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;
        this.owner = owner;
        this.userBridge = appController.getUserBridge();
        this.clinicianBridge = appController.getClinicianBridge();
        this.adminBridge = appController.getAdministratorBridge();
        statusBarPageController.init();
        displayDetails();
        transplantWaitListTabPageController.init(appController, this);
        stage.setTitle("Administrator");
        availableOrgansViewController.init(this);
        ServerEventNotifier.getInstance().addPropertyChangeListener(this);

        userBridge.getUsers(userStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText(), genderComboBox.getValue(), appController.getToken());
        clinicianBridge.getClinicians(clinicianStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText(), appController.getToken());

        adminUndoButton.setDisable(true);
        adminRedoButton.setDisable(true);
        if (administrator.getUserName().equals("default")) {
            deleteAdmin.setDisable(true);
        }

        adminCliTextArea.setEditable(false);
        adminCliTextArea.setFont(Font.font("DialogInput"));
        adminCliTextArea.setFocusTraversable(false);
        cliInputTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                sendInputToCLI();
                cliInputTextField.setText("");
            } else if (e.getCode() == KeyCode.UP) {
                if (pastCommandIndex >= 0) {
                    pastCommandIndex = pastCommandIndex == 0 ? 0
                            : pastCommandIndex - 1; // makes sure pastCommandIndex is never < 0
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                if (pastCommandIndex < pastCommands.size() - 1) {
                    pastCommandIndex++;
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                } else if (pastCommandIndex == pastCommands.size() - 1) {
                    pastCommandIndex++;
                    cliInputTextField.setText("");
                }
            }
        });
        progressIndicator.setVisible(false);
        addListeners();
        initClinicianSearchTable();
        initAdminSearchTable();
        initUserSearchTable();
        clinicianTableView.setVisible(false);
        adminTableView.setVisible(false);
    }

    /**
     * Sends the input to CLI and redirects the output stream to a new ByteArrayOutputStream and sends
     * the results to the textArea
     */
    private void sendInputToCLI() {
        PrintStream stdOut = System.out; //NOSONAR
        PrintStream stdErr = System.err; //NOSONAR
        //The above two lines have Sonar turned off as they reroute our the streams to allow our GUI CLI to work
        ByteArrayOutputStream areaOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(areaOut));
        System.setErr(new PrintStream(areaOut));
        pastCommands.add(cliInputTextField.getText());
        pastCommandIndex = pastCommands.size();
        CLI.parseInput(cliInputTextField.getText(), appController);
        adminCliTextArea.appendText("\n" + areaOut.toString().replaceAll("\\[[0-9]+m", ""));
        System.setOut(stdOut);
        System.setErr(stdErr);
    }

    /**
     * Utility method to add listeners to required fields
     */
    private void addListeners() {

        // listeners for each radio button which hides/shows the table views
        adminSearchRadios.getToggles().forEach(radio -> (radio).selectedProperty().addListener(((observable, oldValue, newValue) -> {
            adminTableView.setVisible(false);
            clinicianTableView.setVisible(false);
            userTableView.setVisible(false);

            if (adminUserRadioButton.isSelected()) {
                userTableView.setVisible(true);
                regionSearchTextField.setVisible(true);
                regionLabel.setVisible(true);
            } else if (adminClinicianRadioButton.isSelected()) {
                clinicianTableView.setVisible(true);
                regionSearchTextField.setVisible(true);
                regionLabel.setVisible(true);
            } else if (adminAdminRadioButton.isSelected()) {
                adminTableView.setVisible(true);
                regionSearchTextField.setVisible(false);
                regionLabel.setVisible(false);
            }

        })));

        userTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchUser(userTableView.getSelectionModel().getSelectedItem().getNhi());
            }
        });

        clinicianTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                try {
                    launchClinician(appController.getClinicianBridge().getClinician(
                            clinicianTableView.getSelectionModel().getSelectedItem().getStaffId(),
                            appController.getToken()));
                } catch (ApiException e) {
                    Log.severe("Clinician Could not be fetched", e);
                }
            }
        });

        adminTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchAdmin(adminTableView.getSelectionModel().getSelectedItem());
            }
        });

        InvalidationListener textFieldListener = observable -> {
            pause.setOnFinished(e -> {
                userStartIndex = 0;
                if (adminUserRadioButton.isSelected()) {
                    AdministratorViewController.this.populateUserSearchTable();
                } else if (adminClinicianRadioButton.isSelected()) {
                    AdministratorViewController.this.populateClinicianSearchTable();
                } else if (adminAdminRadioButton.isSelected()) {
                    AdministratorViewController.this.populateAdminSearchTable();
                }
            });
            pause.playFromStart();
        };
        adminSearchField.textProperty().addListener(textFieldListener);
        regionSearchTextField.textProperty().addListener(textFieldListener);
    }


    /**
     * Initialises table for the clinician table
     */
    private void initClinicianSearchTable() {

        TableColumn<Clinician, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME));

        TableColumn<Clinician, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME));

        TableColumn<Clinician, String> nhiColumn = new TableColumn<>("Staff Id");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        clinicianTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clinicianTableView.getColumns().clear();
        clinicianTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);

        populateClinicianSearchTable();
    }

    /**
     * Initialises the columns for the admin table
     */
    private void initAdminSearchTable() {
        TableColumn<Administrator, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME));

        TableColumn<Administrator, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME));

        TableColumn<Administrator, String> userNameColumn = new TableColumn<>("User Name");
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        adminTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        adminTableView.getColumns().clear();
        adminTableView.getColumns().addAll(userNameColumn, firstNameColumn, lastNameColumn);

        populateAdminSearchTable();

    }

    /**
     * initialises the search table, abstracted from main init function for clarity
     */
    private void initUserSearchTable() {
        TableColumn<UserOverview, String> fNameColumn;
        TableColumn<UserOverview, String> dobColumn;
        TableColumn<UserOverview, String> dodColumn;
        TableColumn<UserOverview, String> ageColumn;
        TableColumn<UserOverview, HashSet<Organs>> organsColumn;
        TableColumn<UserOverview, String> regionColumn;

        fNameColumn = new TableColumn<>("First name");
        fNameColumn.setCellValueFactory(new PropertyValueFactory<>(FIRST_NAME));

        TableColumn<UserOverview, String> lNameColumn = new TableColumn<>("Last name");
        lNameColumn.setCellValueFactory(new PropertyValueFactory<>(LAST_NAME));
        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        dobColumn = new TableColumn<>("Date of Birth");
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

        dodColumn = new TableColumn<>("Date of Death");
        dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

        ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        regionColumn = new TableColumn<>("Region");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        organsColumn = new TableColumn<>("Organs");
        organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

        userTableView.getColumns().clear();
        userTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);

        populateUserSearchTable();
    }

    /**
     * adds new data to the user search table
     */
    private void populateUserSearchTable() {
        populateUserSearchTable(0, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText(), "");
    }

    /**
     * Requests the filtered data from the server and populates the user overview table
     *
     * @param startIndex starting index to get the data from
     * @param count      amount of users to obtain
     * @param name       query for name, will obtain users that start with the name
     * @param region     search query for region
     * @param gender     search query for gender
     */
    private void populateUserSearchTable(int startIndex, int count, String name, String region, String gender) {
        appController.getUserOverviews().clear();
        userBridge.getUsers(startIndex, count, name, region, gender, appController.getToken());

        displayUserSearchTable();
    }

    /**
     * Displays the userOverviews from the appcontroller. Abstracted from populate user search table to prevent cyclic recursion
     */
    private void displayUserSearchTable() {
        ObservableList<UserOverview> oUsers = FXCollections.observableList(new ArrayList<>(appController.getUserOverviews()));
        SortedList<UserOverview> sUsers = new SortedList<>(oUsers);
        sUsers.comparatorProperty().bind(userTableView.comparatorProperty());

        if (!appController.getUserOverviews().isEmpty()) {
            userTableView.setItems(sUsers);
        } else {
            userTableView.setItems(null);
            Platform.runLater(() -> userTableView.setPlaceholder(new Label("No users match this criteria"))); // Do this to prevent threading issues when this method is not called on an FX thread;
        }

        setTableOnClickBehaviour(User.class, userTableView);
    }

    private void setTableOnClickBehaviour(Type type, TableView tv) {
        if (!appController.getUserOverviews().isEmpty()) {
            tv.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    if (type.equals(User.class)) {
                        launchUser(userTableView.getSelectionModel().getSelectedItem().getNhi());
                    } else if (type.equals(Clinician.class)) {
                        launchClinician(clinicianTableView.getSelectionModel().getSelectedItem());
                    } else if (type.equals(Administrator.class)) {
                        launchAdmin(adminTableView.getSelectionModel().getSelectedItem());
                    }
                }
            });
        } else {
            userTableView.setOnMouseClicked(null);
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
     * Imports admins from a file chosen from a fileselector
     */
    @FXML
    void importAdmins() {
        Log.info("Importing Admins");
        List<String> extensions = new ArrayList<>();
        extensions.add(JSON);
        FileSelectorController fileSelectorController = new FileSelectorController();
        String filename = fileSelectorController.getFileSelector(stage, extensions);
        if (filename == null) {
            Log.warning(FILE_NAME_NOT_FOUND);
            fileNotFoundLabel.setVisible(true);
            return;
        }
        importRoleJson(Administrator.class, filename);


    }

    /**
     * Imports clinicians from a file chosen from a file selector
     */
    @FXML
    void importClinicians() {
        List<String> extensions = new ArrayList<>();
        extensions.add(JSON);
        FileSelectorController fileSelectorController = new FileSelectorController();
        String filename = fileSelectorController.getFileSelector(stage, extensions);
        if (filename == null) {
            Log.warning(FILE_NAME_NOT_FOUND);
            fileNotFoundLabel.setVisible(true);
            return;
        }
        Log.info(messageAdmin + administrator.getUserName() + " Importing Clinician profiles");
        importRoleJson(Clinician.class, filename);
    }

    /**
     * Imports Users from a file chosen from a fileselector
     */
    @FXML
    void importUsers() {
        List<String> extensions = new ArrayList<>();
        extensions.add(JSON);
        extensions.add("*.csv");
        FileSelectorController fileSelectorController = new FileSelectorController();
        String filename = fileSelectorController.getFileSelector(stage, extensions);
        if (filename == null) {
            Log.warning(FILE_NAME_NOT_FOUND);
            fileNotFoundLabel.setVisible(true);
            return;
        }
        Log.info(messageAdmin + administrator.getUserName() + " Importing User profiles");
        if (filename.contains(".json")) {
            importRoleJson(User.class, filename);
        } else {
            importRoleCsv(User.class, filename);
        }
    }

    @FXML
    void selectCountries() {
        FXMLLoader countrySelectionLoader = new FXMLLoader(
                getClass().getResource("/FXML/countrySelectionView.fxml"));
        Parent root;
        try {
            root = countrySelectionLoader.load();
            CountrySelectionController countrySelectionController = countrySelectionLoader.getController();
            Stage countrySelectStage = new Stage();
            countrySelectionController.init(administrator, countrySelectStage, appController);
            countrySelectStage.setScene(new Scene(root));
            countrySelectStage.show();
            Log.info("successfully launched countrySelectionView pop-up window for admin user name: " + administrator.getUserName());
        } catch (IOException e) {
            Log.severe("failed to load countrySelectionView pop-up window admin user name: " + administrator.getUserName(), e);
        }
    }

    /**
     * attempts to import the given role from a file, and save it to the default file.
     * Currently contains handlers for administrator, clinician, and user
     *
     * @param role     class to be imported. e.g. Administrator.class
     * @param <T>      Type T (not used?)
     * @param filename name of file to be imported including path
     */
    private <T> void importRoleJson(Class<T> role, String filename) {
        if (!isAllWindowsClosed()) {
            launchAlertUnclosedWindowsGUI();
            return;
        }

        try {
            if (role.isAssignableFrom(Administrator.class)) {
                importAdminJson(filename);
            } else if (role.isAssignableFrom(Clinician.class)) {
                importClinicianJson(filename);
            } else if (role.isAssignableFrom(User.class)) {
                importUserJson(filename);
            }

        } catch (FileNotFoundException e) {
            Log.warning("Failed to load file " + filename, e);
            messageBoxPopup(ERROR);

        } catch (InvalidFileException e) {
            Log.warning("File " + filename + " is invalid", e);
            messageBoxPopup(ERROR);
        }
        refreshTables();
    }

    private void importUserJson(String filename) throws FileNotFoundException, InvalidFileException {
        Collection<User> existingUsers = appController.getUsers();
        Collection<User> newUsers = dataHandler.loadUsers(filename);

        //if imported contains any bad data, throw it out
        for (User user : newUsers) {
            if (user.getNhi() == null) {
                throw new InvalidFileException();
            }
        }

        for (User newUser : newUsers) {
            if (existingUsers.contains(newUser)) {
                appController.update(newUser);
            } else {
                appController.addUser(newUser);
            }
        }
        saveRole(User.class, appController, appController.getToken());
    }

    private void importClinicianJson(String filename) throws FileNotFoundException, InvalidFileException {
        Collection<Clinician> existingClinicians = appController.getClinicians();
        Collection<Clinician> newClinicians = dataHandler.loadClinicians(filename);

        //if imported contains any bad data, throw it out
        for (Clinician clinician : newClinicians) {
            if (clinician.getStaffId() == null) {
                throw new InvalidFileException();
            }
        }

        for (Clinician newClinician : newClinicians) {
            if (existingClinicians.contains(newClinician)) {
                appController.updateClinicians(newClinician);
            } else {
                appController.addClinician(newClinician);
            }
        }
        saveRole(Clinician.class, appController, appController.getToken());
        messageBoxPopup("confirm");
        try {
            dataHandler.saveClinicians(appController.getClinicians());
            Log.info("successfully imported " + newClinicians.size() + " Clinician profiles");
        } catch (IOException e) {
            Log.warning("failed to save newly loaded clinicians", e);
        }
    }

    private void importAdminJson(String filename) throws FileNotFoundException, InvalidFileException {
        Collection<Administrator> newAdmins = dataHandler.loadAdmins(filename);

        //if imported contains any bad data, throw it out
        for (Administrator admin : newAdmins) {
            if (admin.getUserName() == null) {
                throw new InvalidFileException();
            }
        }

        for (Administrator newAdmin : newAdmins) {
            new Thread(()-> appController.getAdministratorBridge().putAdmin(newAdmin, newAdmin.getUserName(),appController.getToken())).start();
        }
        saveRole(Administrator.class, appController, appController.getToken());
        messageBoxPopup("confirm");
    }

    /**
     * imports a given role from a CSV file
     *
     * @param role     currently only accepts User.class, others will do nothing
     * @param filename path to the selected .csv file
     */
    private void importRoleCsv(Type role, String filename) {
        if (!isAllWindowsClosed()) {
            launchAlertUnclosedWindowsGUI();
            return;
        }
        if (role.equals(User.class)) {
            progressIndicator.setVisible(true);
            progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            new Thread(() -> {
                Platform.setImplicitExit(false);
                Collection<User> newUsers = new ArrayList<>();
                CSVHandler csvHandler = new CSVHandler();
                try {
                    newUsers = csvHandler.loadUsers(filename);

                    //if imported contains any bad data, throw it out
                    for (User user : newUsers) {
                        if (user.getNhi() == null) {
                            throw new InvalidFileException();
                        }
                    }

                for (User user : newUsers) {
                    new Thread(() -> appController.getUserBridge().postUserSilently(user)).start();
                }
                saveRole(User.class, appController, appController.getToken());
            } catch (FileNotFoundException e) {
                Log.warning("Failed to load file " + filename, e);
                messageBoxPopup(ERROR);

            } catch (InvalidFileException e) {
                Log.warning(filename + "contained bad data", e);
                messageBoxPopup(ERROR);
            }
            Platform.runLater(this::refreshTables);
                final int numberImported = newUsers.size();
                final int malformed = csvHandler.getMalformed();
                Platform.runLater(() -> AlertWindowFactory.generateInfoWindow(numberImported + " Users Successfully imported. " +
                        +malformed + " malformed users discarded"));
                Platform.runLater(() -> progressIndicator.setVisible(false));
            }).start();
        }
    }


    /**
     * takes the current contents of the locally stored admins, clinicians, and users and saves them to the database.
     * uses PUT method to update or delete, and POST to append new data
     *
     * @param type       accepts Administrator.class, Clinician.class, or User.class. others types will do nothing
     * @param controller AppController instance to use
     * @param token      auth token to use to access the server
     */
    private void saveRole(Type type, AppController controller, String token) {
        if (type.equals(Administrator.class)) {
            for (Administrator admin : controller.getAdmins()) {
                if (adminBridge.getExists(admin.getUserName())) {
                    adminBridge.putAdmin(admin, admin.getUserName(), token);
                } else {
                    adminBridge.postAdmin(admin, token);
                }
            }
        } else if (type.equals(Clinician.class)) {
            for (Clinician clinician : controller.getClinicians()) {
                if (clinicianBridge.getExists(clinician.getStaffId())) {
                    clinicianBridge.putClinician(clinician, clinician.getStaffId(), token);
                } else {
                    clinicianBridge.postClinician(clinician, token);
                }
            }
        } else if (type.equals(User.class)) {
            for (User user : controller.getUsers()) {
                if (userBridge.getExists(user.getNhi())) {
                    userBridge.putUser(user, user.getNhi());
                } else {
                    userBridge.postUser(user);

                }
            }
        } else {
            return;
        }
        //make popups
        Log.info("successfully imported data");

    }

    /**
     * Shows a message box popup with either a load confirmation message, or error message based on string passed in
     *
     * @param messageType a String to indicate the message type needing to be shown
     */
    private void messageBoxPopup(String messageType) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");

        if (messageType.equals(ERROR)) {
            errorAlert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    errorAlert.close();
                }
            });
        } else {
            confirmAlert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    confirmAlert.close();
                }
            });
        }
    }

    /**
     * checks if other windows are opened apart from admin overview
     *
     * @return true only if the admin overview is opened, false otherwise
     */
    private boolean isAllWindowsClosed() {
        List<Stage> windows = StageHelper.getStages();
        return windows.size() == 1;
    }

    /**
     * closes all windows apart from admin overview.
     */
    public void closeAllWindows() {
        List<Stage> windows = StageHelper.getStages();
        int numWindows = windows.size();

        //skips the first stage, which is the admin overview
        for (int i = 1; i < numWindows; i++) {
            windows.get(1).close(); //when close, the stage is removed from list
        }
    }

    /**
     * Launches a popup gui that warns user if there
     * are multiple windows opened.
     */
    private void launchAlertUnclosedWindowsGUI() {
        FXMLLoader alertUnclosedWindowsLoader = new FXMLLoader(
                getClass().getResource("/FXML/AlertUnclosedWindows.fxml"));
        Parent root;
        try {
            root = alertUnclosedWindowsLoader.load();
            root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
            // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
            AlertUnclosedWindowsController alertUnclosedWindowsController = alertUnclosedWindowsLoader.getController();
            Stage unclosedWindowStage = new Stage();
            alertUnclosedWindowsController.init(unclosedWindowStage, this);
            unclosedWindowStage.setScene(new Scene(root));
            unclosedWindowStage.show();
        } catch (IOException e) {
            Log.severe("IOException encountered", e);
        }
    }


    /**
     * Opens the create user screen
     */
    @FXML
    void addUser() {

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Create New User Profile");
            newStage.show();
            NewUserController donorController = userLoader.getController();
            donorController.init(AppController.getInstance(), stage, newStage);
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched create new user window");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load create new user window", e);
        }
    }

    /**
     * Launches the user overview screen for a selected user
     *
     * @param overview the selected user.
     */
    @Override
    public void launchUser(String overview) {
        if (overview != null) {
            FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
            Parent root;
            try {
                User user = appController.getUserBridge().getUser(overview);
                root = userLoader.load();
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root));
                UserController userController = userLoader.getController();
                AppController.getInstance().setUserController(userController);
                Collection<PropertyChangeListener> listeners = new ArrayList<>();
                listeners.add(this);
                userController.init(AppController.getInstance(), user, newStage, true, listeners);
                newStage.show();
                Log.info(messageAdmin + administrator.getUserName() + " successfully launched user overview window for User NHI: " + user.getNhi());
            } catch (IOException e) {
                Log.severe(messageAdmin + administrator.getUserName() + " failed to load user overview window for User NHI: " + overview, e);
            }
        }
    }

    /**
     * Launches the clinician overview screen for a selected clinician
     *
     * @param clinician the selected clinician.
     */
    private void launchClinician(Clinician clinician) {
        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
        Parent root;
        try {
            root = clinicianLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMinHeight(800);
            newStage.setMinWidth(1200);
            ClinicianController clinicianController = clinicianLoader.getController();
            clinicianControllers.add(clinicianController);
            Collection<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            clinicianController.init(newStage, AppController.getInstance(), clinician, owner, listeners);
            newStage.show();
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched clinician overview window for Clinician Staff ID:" + clinician.getStaffId());
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load clinician overview window for Clinician Staff ID:" + clinician.getStaffId(), e);
        }
    }

    /**
     * Launches the admin overview screen for a selected admin
     *
     * @param administrator the selected administrator
     */
    private void launchAdmin(Administrator administrator) {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setMinHeight(800);
            newStage.setMinWidth(1200);
            AdministratorViewController adminLoaderController = adminLoader.getController();
            administratorViewControllers.add(adminLoaderController);
            adminLoaderController.init(administrator, AppController.getInstance(), newStage, false, null);
            newStage.show();
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched administrator overview window");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load administrator overview window", e);
        }
    }

    /**
     * Launches the clinician creation screen
     */
    @FXML
    void addClinician() {

        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
        Parent root;
        try {
            root = clinicianLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateClinicianController newClinician = clinicianLoader.getController();
            newClinician.init(null, appController, stage, true, newStage);
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched create new clinician window");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load create new clinician window", e);
        }
    }

    /**
     * Launches admin creation screen
     */
    @FXML
    void addAdmin() {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateAdminController updateAdminController = adminLoader.getController();
            updateAdminController.init(new Administrator(), newStage, true);
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched create new administrator window");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load create new administrator window", e);
        }
    }

    /**
     * Saves all admins when the save menu item is clicked
     */
    @FXML
    void saveClicked() {
        appController.updateAdmin(administrator);
        appController.saveAdmin(administrator);
        administrator.getUndoStack().clear();
        administrator.getRedoStack().clear();
        adminUndoButton.setDisable(true);
        adminRedoButton.setDisable(true);
    }

    /**
     * Logs out and saves the admin
     */
    @FXML
    void logout() {
        checkSave();
        administrator.getUndoStack().clear();
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loginLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loginLoader.getController();
            loginController.init(appController, newStage);
            Log.info(messageAdmin + administrator.getUserName() + " Successfully launched Login window after logout");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " Failed to load Login window after logout", e);
        }
    }

    /**
     * Popup that prompts the clinician to save any unsaved changes before logging out or exiting the application
     */
    private void checkSave() {
        boolean noChanges = administrator.getUndoStack().isEmpty();
        if (!noChanges) {
            Optional<ButtonType> result = UnsavedChangesAlert.getAlertResult();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                appController.updateAdmin(administrator);
                appController.saveAdmin(administrator);

            } else {
                Administrator revertAdmin = administrator.getUndoStack().firstElement().getState();
                appController.updateAdmin(revertAdmin);
            }
        }
    }


    /**
     * Undoes the previous action that changed the admin
     */
    @FXML
    void undo() {
        administrator.undo();
        adminUndoButton.setDisable(administrator.getUndoStack().isEmpty());
        displayDetails();
        Log.info(messageAdmin + administrator.getUserName() + "executed Undo Administrator");
    }

    /**
     * Redoes the previous action that changed the admin
     */
    @FXML
    void redo() {
        administrator.redo();
        adminRedoButton.setDisable(administrator.getRedoStack().isEmpty());
        displayDetails();
        Log.info(messageAdmin + administrator.getUserName() + "executed Redo Administrator");
    }


    /**
     * load the labels on the admin view with the current admins details
     */
    public void displayDetails() {
        if (!administrator.getUserName().isEmpty()) {
            adminUsernameLable.setText(administrator.getUserName());
            adminFirstnameLabel.setText(administrator.getFirstName());
            if ((administrator.getMiddleName() != null) && !administrator.getMiddleName().isEmpty()) {
                adminMiddleNameLabel.setText(administrator.getMiddleName());
            } else {
                adminMiddleNameLabel.setText("");

            }
            if ((administrator.getLastName() != null) && !administrator.getLastName().isEmpty()) {
                adminLastNameLabel.setText(administrator.getLastName());
            } else {
                adminLastNameLabel.setText("");
            }
        }
        adminUndoButton.setDisable(administrator.getUndoStack().isEmpty());
        adminRedoButton.setDisable(administrator.getRedoStack().isEmpty());
        if (administrator.getChanges().isEmpty()) {
            statusBarPageController.updateStatus(administrator.getUserName() + " " + administrator.getChanges().get(administrator.getChanges().size() - 1).getChange());
        }
        stage.setTitle("Administrator");

    }

    /**
     * go to a form to update the admin
     */
    @FXML
    void updateAdmin() {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            UpdateAdminController updateAdminController = adminLoader.getController();
            updateAdminController.init(administrator, newStage, false);
            Log.info(messageAdmin + administrator.getUserName() + " successfully launched update administrator window");
        } catch (IOException e) {
            Log.severe(messageAdmin + administrator.getUserName() + " failed to load update administrator window", e);
        }
    }

    /**
     * Deletes the admin account with a confirmation message
     */
    @FXML
    void deleteAdminAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this administrator?");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            return;
        }
        if (result.get() == ButtonType.OK) {
            administrator.setDeleted(true);
            appController.deleteAdmin(administrator);
            Log.info(messageAdmin + administrator.getUserName() + " Successfully deleted Admin account: ");
            if (owner) {
                logout();
            } else {
                stage.close();
            }
        }
    }

    /**
     * Opens a window to restore recently deleted profiles.
     */
    @FXML
    private void openDeletedProfiles() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage deleteStage = new Stage();
            deleteStage.setScene(new Scene(root));
            deletedUserController.init(true);
            deleteStage.initModality(Modality.APPLICATION_MODAL);
            deleteStage.showAndWait();
        } catch (IOException e) {
            Log.warning(e.getMessage());
        }
    }


    /**
     * updates tables in the admin window with current version of underlying model
     */
    @FXML
    public void refreshTables() {
        transplantWaitListTabPageController.displayWaitListTable();
        displayUserSearchTable();
        displayClinicianSearchTable();
        displayAdminSearchTable();
        for (AdministratorViewController adminController : administratorViewControllers) {
            adminController.refreshTables();
        }
        for (ClinicianController clinicianController : clinicianControllers) {
            clinicianController.refreshTables();
        }
    }


    /**
     * moves the currently active tableview to the next page
     */
    @FXML
    private void goToNextPage() {
        if ((appController.getUserOverviews().size() < ROWS_PER_PAGE && adminUserRadioButton.isSelected())
            || (appController.getClinicians().size() < ROWS_PER_PAGE && adminClinicianRadioButton.isSelected())
            || (appController.getAdmins().size() < ROWS_PER_PAGE && adminAdminRadioButton.isSelected())){
            return;
        }
        if (adminUserRadioButton.isSelected()) {
            userStartIndex += ROWS_PER_PAGE;
            populateUserSearchTable(userStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText(), "");
        } else if (adminClinicianRadioButton.isSelected()) {
            clinicianStartIndex += ROWS_PER_PAGE;
            populateClinicianSearchTable(clinicianStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText());
        } else if (adminAdminRadioButton.isSelected()) {
            adminStartIndex += ROWS_PER_PAGE;
            populateAdminSearchTable(adminStartIndex, ROWS_PER_PAGE, adminSearchField.getText());

        }
    }

    /**
     * moves the currently active tableview to the previous page
     */
    @FXML
    private void goToPrevPage() {
        if (adminUserRadioButton.isSelected()) {
            if (userStartIndex - ROWS_PER_PAGE < 0) {
                return;
            }
            userStartIndex -= ROWS_PER_PAGE;
            populateUserSearchTable(userStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText(), genderComboBox.getValue());
        } else if (adminClinicianRadioButton.isSelected()) {
            if (clinicianStartIndex - ROWS_PER_PAGE < 0) {
                return;
            }
            clinicianStartIndex -= ROWS_PER_PAGE;
            populateClinicianSearchTable(clinicianStartIndex, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText());
        } else if (adminAdminRadioButton.isSelected()) {
            if (adminStartIndex - ROWS_PER_PAGE < 0) {
                return;
            }
            adminStartIndex -= ROWS_PER_PAGE;
            populateAdminSearchTable(adminStartIndex, ROWS_PER_PAGE, adminSearchField.getText());
        }
    }

    /**
     * Fires the request to search in the clinician search table with startIndex = 0
     */
    private void populateClinicianSearchTable() {
        populateClinicianSearchTable(0, ROWS_PER_PAGE, adminSearchField.getText(), regionSearchTextField.getText());
    }

    /**
     * Fires the request to search in the clinician search table with startIndex = 0
     *
     * @param startIndex  Start index to search from
     * @param rowsPerPage number of results to return
     * @param name        name of the clinician
     * @param region      region of the clinician
     */
    private void populateClinicianSearchTable(int startIndex, int rowsPerPage, String name, String region) {
        appController.getClinicians().clear();
        clinicianBridge.getClinicians(startIndex, rowsPerPage, name, region, appController.getToken());

        displayClinicianSearchTable();
    }

    /**
     * Displays the clinicians from the appcontroller. Abstracted to prevent cyclic recursion
     */
    private void displayClinicianSearchTable() {

        ObservableList<Clinician> oClinicians = FXCollections.observableList(new ArrayList<>(appController.getClinicians()));
        SortedList<Clinician> sClinicians = new SortedList<>(oClinicians);
        sClinicians.comparatorProperty().bind(clinicianTableView.comparatorProperty());

        if (!appController.getClinicians().isEmpty()) {
            clinicianTableView.setItems(sClinicians);
        } else {
            clinicianTableView.setItems(null);
            // Do this to prevent threading issues when this method is not called on an FX thread;
            Platform.runLater(() -> clinicianTableView.setPlaceholder(new Label("No clinicians to show")));
        }

        setTableOnClickBehaviour(Clinician.class, clinicianTableView);
        clinicianTableView.refresh();
    }

    /**
     * Fires the request to search in the admin search table with startIndex = 0
     */
    private void populateAdminSearchTable() {
        populateAdminSearchTable(0, ROWS_PER_PAGE, adminSearchField.getText());
    }

    /**
     * Fires the request to search in the admin search table with startIndex = 0
     *
     * @param startIndex  Start index to search from
     * @param rowsPerPage number of results to return
     * @param name        name of the admin
     */
    private void populateAdminSearchTable(int startIndex, int rowsPerPage, String name) {
        appController.getAdmins().clear();
        adminBridge.getAdmins(startIndex, rowsPerPage, name, appController.getToken());

        displayAdminSearchTable();
    }

    private void displayAdminSearchTable() {
        ObservableList<Administrator> oAdmins = FXCollections.observableList(new ArrayList<>(appController.getAdmins()));
        SortedList<Administrator> sAdmins = new SortedList<>(oAdmins);
        sAdmins.comparatorProperty().bind(adminTableView.comparatorProperty());

        if (!appController.getClinicians().isEmpty()) {
            adminTableView.setItems(sAdmins);
        } else {
            adminTableView.setItems(null);
            // Do this to prevent threading issues when this method is not called on an FX thread;
            Platform.runLater(() -> adminTableView.setPlaceholder(new Label("No admins to show")));
        }

        setTableOnClickBehaviour(Administrator.class, adminTableView);
    }

    /**
     * handles events fired by objects this is listening to. Currently only handles UpdateNotificationEvents.
     * upon receiving, the tables and the currently logged-in admin will be updated where relevant
     *
     * @param evt PropertyChangeEvent to be handled
     * @see UpdateNotificationEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        //clinician controller watches user model
        //refresh view/tables etc. on change
        UpdateNotificationEvent event;
        try {
            event = (UpdateNotificationEvent) evt;
        } catch (ClassCastException ex) {
            return;
        }
        if (event == null) {
            return;
        }
        Log.info("refresh listener fired in admin controller");
        if (event.getType().equals(EventTypes.USER_UPDATE) || event.getType().equals(EventTypes.CLINICIAN_UPDATE)) {
            refreshTables();
            availableOrgansViewController.search();
        } else if (event.getType().equals(EventTypes.ADMIN_UPDATE) && administrator.getUserName().equals(event.getOldIdentifier())) {
            try {
                this.administrator = adminBridge.getAdmin(event.getNewIdentifier(), appController.getToken());
                if (administrator != null) {
                    displayDetails(); //TODO: fix when we solve the db race 7/8/18 jb
                }
            } catch (ApiException ex) {
                Log.warning("failed to retrieve updated admin. response code: " + ex.getResponseCode(), ex);
                AlertWindowFactory.generateError(("could not refresh admin from the server. Please check your connection before trying again."));
            }
        }
    }
}
