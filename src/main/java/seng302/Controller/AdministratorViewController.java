package seng302.Controller;

import com.sun.javafx.stage.StageHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.*;
import seng302.Service.Log;
import seng302.View.CLI;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class AdministratorViewController implements PropertyChangeListener, TransplantWaitListViewer {

    //<editor-fold desc="FXML items">
    @FXML
    private TableView<?> transplantWaitListTableView;

    @FXML
    private Label succesFailLabel;

    @FXML
    private Label adminLastNameLabel;

    @FXML
    private TableView<Administrator> adminTableView;

    @FXML
    private TableView<Clinician> clinicianTableView;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private Pagination searchTablePagination;

    @FXML
    private Button addAdminButton;

    @FXML
    private MenuItem adminSaveMenu;

    @FXML
    private Tooltip searchToolTip;

    @FXML
    private Label adminFirstnameLabel;

    @FXML
    private Button adminLogoutButton;

    @FXML
    private Button addClinicianButton;

    @FXML
    private CheckBox adminAdminCheckbox;

    @FXML
    private Button adminUndoButton;

    @FXML
    private CheckBox adminClinicianCheckbox;

    @FXML
    private Label searchCountLabel;

    @FXML
    private Label filtersLabel;

    @FXML
    private CheckBox adminUserCheckbox;

    @FXML
    private Button adminRedoButton;

    @FXML
    private Button addUserButton;

    @FXML
    private Button updateButton;

    @FXML
    private Label adminMiddleNameLabel;

    @FXML
    private Label adminUsernameLable;

    @FXML
    private MenuItem adminImportMenu;

    @FXML
    private TextField adminSearchField;

    @FXML
    private TextArea adminCliTextArea;

    @FXML
    private TextField cliInputTextField;

    @FXML
    private Button deleteAdminButton;

    @FXML
    private Label fileNotFoundLabel;

    @FXML
    private TransplantWaitListController transplantWaitListTabPageController;
    @FXML
    private statusBarController statusBarPageController;

    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -2;
    private boolean owner;

    /**
     * Initialises scene for the administrator view
     *
     * @param administrator administrator to view
     * @param appController appController instance to get data from
     * @param stage stage to display on
     */
    public void init(Administrator administrator, AppController appController, Stage stage, boolean owner, Collection<PropertyChangeListener> parentListeners) {
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;
        this.owner = owner;
        statusBarPageController.init(appController);
        displayDetails();
        transplantWaitListTabPageController.init(appController, this);

//add change listeners of parent controllers to the current user
        if (parentListeners != null && !parentListeners.isEmpty()) {
            for (PropertyChangeListener listener : parentListeners) {
                administrator.addPropertyChangeListener(listener);
            }
        }    adminUndoButton.setDisable(true);
    adminRedoButton.setDisable(true);
    if (administrator.getUserName().equals("default")) {
      deleteAdminButton.setDisable(true);
    }

        adminCliTextArea.setEditable(false);
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

        addListeners();
        displayClinicianTable();
        displayAdminTable();
        displayUserTable();
        clinicianTableView.setVisible(false);
        adminTableView.setVisible(false);
    }

  /**
   * Sends the input to CLI and redirects the output stream to a new ByteArrayOutputStream and sends
   * the results to the textArea
   */
  private void sendInputToCLI() {
    PrintStream stdOut = System.out;
    PrintStream stdErr = System.err;
    ByteArrayOutputStream areaOut = new ByteArrayOutputStream();
    System.setOut(new PrintStream(areaOut));
    System.setErr(new PrintStream(areaOut));
    pastCommands.add(cliInputTextField.getText());
    pastCommandIndex = pastCommands.size();
    CLI.parseInput(cliInputTextField.getText(), appController);
    adminCliTextArea.appendText("\n" + areaOut.toString());
    System.setOut(stdOut);
    System.setErr(stdErr);
  }

  /**
   * Utility method to add listeners to required fields
   */
  private void addListeners() {
    adminAdminCheckbox.selectedProperty().addListener((observable -> {
      adminClinicianCheckbox.setSelected(false);
      adminUserCheckbox.setSelected(false);
      clinicianTableView.setVisible(false);
      adminTableView.setVisible(true);
      userTableView.setVisible(false);

    }));

    adminUserCheckbox.selectedProperty().addListener((observable -> {
      adminClinicianCheckbox.setSelected(false);
      adminAdminCheckbox.setSelected(false);
      clinicianTableView.setVisible(false);
      adminTableView.setVisible(false);
      userTableView.setVisible(true);

    }));

    adminClinicianCheckbox.selectedProperty().addListener((observable -> {
      adminAdminCheckbox.setSelected(false);
      adminUserCheckbox.setSelected(false);
      clinicianTableView.setVisible(true);
      adminTableView.setVisible(false);
      userTableView.setVisible(false);

    }));

        userTableView.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchUser(userTableView.getSelectionModel().getSelectedItem());
            }
        });

    clinicianTableView.setOnMouseClicked(event -> {
      if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
        launchClinician(clinicianTableView.getSelectionModel().getSelectedItem());
      }
    });

         adminTableView.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchAdmin(adminTableView.getSelectionModel().getSelectedItem());
            }
        });
    }

    /**
     * Initialises table for the clinician table
     */
    private void displayClinicianTable() {
        ObservableList<Clinician> clinicians = FXCollections.observableArrayList(appController.getClinicians());

        TableColumn<Clinician, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Clinician, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Clinician, String> nhiColumn = new TableColumn<>("Staff Id");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        clinicianTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clinicianTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
        clinicianTableView.setItems(clinicians);
    }

  /**
   * Initialises table for the user table
   */
  private void displayUserTable() {
    ObservableList<User> users = FXCollections.observableArrayList(appController.getUsers());

    TableColumn<User, String> firstNameColumn = new TableColumn<>("First Name");
    firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

    TableColumn<User, String> lastNameColumn = new TableColumn<>("Last Name");
    lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

    TableColumn<User, String> nhiColumn = new TableColumn<>("NHI");
    nhiColumn.setCellValueFactory(new PropertyValueFactory<>("nhi"));

    userTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    userTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
    userTableView.setItems(users);
  }

  /**
   * Initialises the columns for the admin table
   */
  private void displayAdminTable() {
    ObservableList<Administrator> admins = FXCollections
        .observableArrayList(appController.getAdmins());

    TableColumn<Administrator, String> firstNameColumn = new TableColumn<>("First Name");
    firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

    TableColumn<Administrator, String> lastNameColumn = new TableColumn<>("Last Name");
    lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

    TableColumn<Administrator, String> nhiColumn = new TableColumn<>("User Name");
    nhiColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

    adminTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    adminTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
    adminTableView.setItems(admins);

  }


  /**
   * Saves the data to the current file
   */
  @FXML
  void save() {

  }

    /**
     * Imports admins from a file chosen from a fileselector
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importAdmins() throws FileNotFoundException {
        Log.info("Admin "+administrator.getUserName()+" Importing Administrator profiles");
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        boolean invalidFile = false;
        int loadedAdminsAmount;
        if(isAllWindowsClosed()) {
            boolean updated = false;
            Collection<Administrator> existingAdmins = appController.getAdmins();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
                fileNotFoundLabel.setVisible(false);
                try {
                    Collection<Administrator> administrators = JsonHandler.loadAdmins(filename);
                    for (Administrator admin : administrators) {
                        if (admin.getUserName() == null) {
                            invalidFile = true;
                            break;
                        }
                        for (Administrator existingAdmin : existingAdmins) {
                            if (admin.getUserName().equals(existingAdmin.getUserName())) {
                                //appController.updateAdmins(admin);
                                updated = true;
                                break;
                            }
                        }
                        if (!updated) {
                            appController.addAdmin(admin);
                        } else {
                            updated = false;
                        }
                    }
                    loadedAdminsAmount = administrators.size();
                }
                catch (FileNotFoundException e) {
                    Log.severe("File not found", e);
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    throw e;
                }
                if (invalidFile) {
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.warning("Incorrect file loaded - leads to NullPointerException.");
                } else {
                    confirmAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.info("successfully imported " + loadedAdminsAmount + " Admin profiles");
                    System.out.println(loadedAdminsAmount + " admins were successfully loaded.");
                }
            } else {
                Log.warning("File name not found");
                fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }
    }

    /**
     * Imports clinicians from a file chosen from a fileselector
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importClinicians() throws FileNotFoundException {
        Log.info("Admin " + administrator.getUserName() + " Importing Clinician profiles");
        boolean invalidFile = false;
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        int loadedCliniciansAmount;
        if (isAllWindowsClosed()) {
            boolean updated = false;
            Collection<Clinician> existingClinicians = appController.getClinicians();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
                fileNotFoundLabel.setVisible(false);
                try {
                    Collection<Clinician> clinicians = JsonHandler.loadClinicians(filename);
                    for (Clinician clinician : clinicians) {
                        if (clinician.getStaffId() == null) {
                            invalidFile = true;
                            break;
                        }
                        for (Clinician existingClinician : existingClinicians) {
                            if (clinician.getStaffId().equals(existingClinician.getStaffId())) {
                                appController.updateClinicians(clinician);
                                updated = true;
                                break;
                            }
                        }
                        if (!updated) {
                            appController.addClinician(clinician);
                        } else {
                            updated = false;
                        }
                    }
                    loadedCliniciansAmount = clinicians.size();
                } catch (FileNotFoundException e) {
                    Log.severe("File not found", e);
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    throw e;
                }
                if (invalidFile) {
                    errorAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.warning("Incorrect file loaded - leads to NullPointerException.");
                } else {
                    confirmAlert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK");
                        }
                    });
                    Log.info("successfully imported " + loadedCliniciansAmount + " Clinician profiles");
                    System.out.println(loadedCliniciansAmount + " clinicians were successfully loaded.");
                }
            } else {
                Log.warning("File name not found");
                fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }
  }

    /**
     * Imports Users from a file chosen from a fileselector
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importUsers() throws FileNotFoundException {
        Log.info("Admin "+administrator.getUserName()+" Importing User profiles");
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText("Error!");
        errorAlert.setContentText("Invalid file loaded.");
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setHeaderText("Load Confirmation");
        confirmAlert.setContentText("File successfully loaded.");
        if(isAllWindowsClosed()) {
            boolean updated = false;
            boolean invalidFile = false;
            int loadedUsersAmount;
            List<User> existingUsers = appController.getUsers();
            String filename;
            filename = FileSelectorController.getFileSelector(stage);
            if (filename != null) {
            try {
                Collection<User> users = JsonHandler.loadUsers(filename);
                for (User user : users) {
                    if (user.getNhi() == null) {
                        invalidFile = true;
                        break;
                    } else {
                        for (User existingUser : existingUsers) {
                            if (user.getNhi().equals(existingUser.getNhi())) {
                                appController.update(user);
                                updated = true;
                                break;
                            }
                        }
                    }
                    if (!updated) {
                        appController.addUser(user);
                    } else {
                        updated = false;
                    }
                }
                loadedUsersAmount = users.size();
            } catch (FileNotFoundException e){
                Log.severe("File not found", e);
                errorAlert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK");
                    }
                });
                throw e;
            }
            if (invalidFile) {
                errorAlert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK");
                    }
                });
                Log.warning("Incorrect file loaded - leads to NullPointerException.");
            } else {
                confirmAlert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        System.out.println("Pressed OK");
                    }
                });
                Log.info("successfully imported " + loadedUsersAmount + " Users profiles");
                System.out.println(loadedUsersAmount + " users were successfully loaded.");
            }
            } else {
            Log.warning("File name not found");
            fileNotFoundLabel.setVisible(true);
            }
        } else {
            launchAlertUnclosedWindowsGUI();
        }

    }

    /**
     * checks if other windows are opened apart from admin overview
     * @return true only if the admin overview is opened, false otherwise
     */
    private boolean isAllWindowsClosed(){
        List<Stage> windows = StageHelper.getStages();
        return windows.size() == 1;
    }

    /**
     * closes all windows apart from admin overview.
     */
    public void CloseAllWindows(){
        List<Stage> windows = StageHelper.getStages();
        int numWindows = windows.size();

        //skips the first stage, which is the admin overview
        for(int i=1; i < numWindows; i++){
            windows.get(1).close(); //when close, the stage is removed from list
        }
    }

    /**
     * Launches a popup gui that warns user if there
     * are multiple windows opened.
     */
    private void launchAlertUnclosedWindowsGUI() {
        FXMLLoader AlertUnclosedWindowsLoader = new FXMLLoader(
                getClass().getResource("/FXML/AlertUnclosedWindows.fxml"));
        Parent root;
        try {
            root = AlertUnclosedWindowsLoader.load();
            root.requestFocus(); //Currently the below code thinks that focus = selected so will always take the focused
            // thing in currentDiseases over the selected thing in pastDiseases. Trying to fix
            AlertUnclosedWindowsController alertUnclosedWindowsController = AlertUnclosedWindowsLoader.getController();
            Stage stage = new Stage();
            alertUnclosedWindowsController.init(stage, this);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  /**
   * Close the tab
   */
  @FXML
  void close() {

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
            Log.info("Admin "+administrator.getUserName()+" successfully launched create new user window");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" failed to load create new user window", e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the user overview screen for a selected user
     * @param user the selected user.
     */
    @Override
    public void launchUser(User user) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            Collection<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            userController.init(AppController.getInstance(), user, newStage, true, listeners);
            newStage.show();
            Log.info("Admin "+administrator.getUserName()+" successfully launched user overview window for User NHI: "+user.getNhi());
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+ " failed to load user overview window for User NHI: "+user.getNhi(), e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the clinician overview screen for a selected clinician
     * @param clinician the selected clinician.
     */
    private void launchClinician(Clinician clinician) {
        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
        Parent root;
        try {
            root = clinicianLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            ClinicianController clinicianController = clinicianLoader.getController();
            Collection<PropertyChangeListener> listeners = new ArrayList<>();
            listeners.add(this);
            clinicianController.init(newStage, AppController.getInstance(), clinician, owner, listeners);
            //clinicianController.init(newStage, AppController.getInstance(), clinician, true);
            newStage.show();
            Log.info("Admin "+administrator.getUserName()+ " successfully launched clinician overview window for Clinician Staff ID:" +clinician.getStaffId());
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+ " failed to load clinician overview window for Clinician Staff ID:" +clinician.getStaffId(), e);
            e.printStackTrace();
        }
    }

    /**
     * Launches the admin overview screen for a selected admin
     * @param administrator the selected administrator
     */
    private void launchAdmin(Administrator administrator) {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root;
        try {
            root = adminLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            AdministratorViewController adminLoaderController = adminLoader.getController();
            adminLoaderController.init(administrator, AppController.getInstance(), newStage, false, null);
            newStage.show();
            Log.info("Admin "+administrator.getUserName()+ " successfully launched administrator overview window");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" failed to load administrator overview window", e);
            e.printStackTrace();
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
            Log.info("Admin "+administrator.getUserName()+" successfully launched create new clinician window");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" failed to load create new clinician window", e);
            e.printStackTrace();
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
            Log.info("Admin "+administrator.getUserName()+" successfully launched create new administrator window");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" failed to load create new administrator window", e);
            e.printStackTrace();
        }
    }

    /**
     * Logs out and saves the admin
     */
    @FXML
    void logout() {
        //check about saving
        appController.updateAdmin(administrator);
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
        Parent root;
        try {
            root = loginLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loginLoader.getController();
            loginController.init(appController,newStage);
            Log.info("Admin "+administrator.getUserName()+" Successfully launched Login window after logout");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" Failed to load Login window after logout", e);
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
        Log.info("Admin "+administrator.getUserName()+"executed Undo Administrator");
    }

    /**
     * Redoes the previous action that changed the admin
     */
    @FXML
    void redo() {
        administrator.redo();
        adminRedoButton.setDisable(administrator.getRedoStack().isEmpty());
        displayDetails();
        Log.info("Admin "+administrator.getUserName()+"executed Redo Administrator");
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
        if (administrator.getChanges().size() > 0) {
            statusBarPageController.updateStatus(administrator.getUserName() + " " + administrator.getChanges().get(administrator.getChanges().size() - 1).getChange());
        }
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
            Log.info("Admin "+administrator.getUserName()+" successfully launched update administrator window");
        } catch (IOException e) {
            Log.severe("Admin "+administrator.getUserName()+" failed to load update administrator window", e);
            e.printStackTrace();
        }
    }

    /**
     * Deletes the admin account with a confirmation message
     */
    @FXML
    void deleteAdminAccount() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to delete this administrator?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            appController.deleteAdmin(administrator);
            Log.info("Admin " + administrator.getUserName() + " Successfully deleted Admin account: ");
            if (owner) {
                logout();
            } else {
                stage.close();
            }
        }
    }

    @FXML
    private void openDeletedProfiles() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            deletedUserController.init(true);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            Log.warning(e.getMessage());
        }
    }

    /**
     * updates tables in the admin window with current version of underlying model
     */
    public void refreshTables() {
        transplantWaitListTabPageController.populateWaitListTable();
        adminTableView.refresh();
        clinicianTableView.refresh();
        userTableView.refresh();
    }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    //watches users and clinicians
    //refresh view on change
      //if/else not strictly necessary at this stage
      if (evt.getPropertyName().equals(EventTypes.USER_UPDATE.name())) {
          refreshTables();
      } else if (evt.getPropertyName().equals(EventTypes.CLINICIAN_UPDATE.name())) {
          refreshTables();
      }
  }
}
