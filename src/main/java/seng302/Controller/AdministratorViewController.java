package seng302.Controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.Administrator;
import seng302.Model.Clinician;
import seng302.Model.JsonHandler;
import seng302.Model.User;

import java.util.List;

import seng302.View.CLI;

public class AdministratorViewController {

    //<editor-fold desc="FXML stuff">
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

    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -1;

    /**
     * Initialises scene for the administrator view
     *
     * @param administrator administrator to view
     * @param appController appController instance to get data from
     * @param stage stage to display on
     */
    public void init(Administrator administrator, AppController appController, Stage stage) {
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;
        displayDetails();

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
                    pastCommandIndex = pastCommandIndex == 0 ? 0 : pastCommandIndex-1; // makes sure pastCommandIndex is never < 0
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                }
            } else if (e.getCode() == KeyCode.DOWN) {
                if (pastCommandIndex < pastCommands.size()-1) {
                    pastCommandIndex++;
                    cliInputTextField.setText(pastCommands.get(pastCommandIndex));
                } else if (pastCommandIndex == pastCommands.size()-1) {
                    pastCommandIndex++;
                    cliInputTextField.setText("");
                }
            }
        });

        addListeners();
        displayClinicanTable();
        displayAdminTable();
        displayUserTable();
        clinicianTableView.setVisible(false);
        adminTableView.setVisible(false);
    }

    /**
     * Sends the input to CLI and redirects the output stream to a new ByteArrayOutputStream
     * and sends the results to the textArea
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
                launchDonor(userTableView.getSelectionModel().getSelectedItem());
            }
        });

        clinicianTableView.setOnMouseClicked(event -> {
            if(event.getClickCount() ==2 && event.getButton() == MouseButton.PRIMARY){
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
    private void displayClinicanTable() {
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
        ObservableList<Administrator> admins = FXCollections.observableArrayList(appController.getAdmins());

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
        String filename;
        filename = FileSelectorController.getFileSelector(stage);
        if (filename != null) {
            //JsonHandler.loadAdmins(filename);
        }
    }

    /**
     * Imports clinicians from a file chosen from a fileselector
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importClinicians() throws FileNotFoundException {
        String filename;
        filename = FileSelectorController.getFileSelector(stage);
        if (filename != null) {
            List<Clinician> clinicians = JsonHandler.loadClinicians(filename);
            System.out.println(clinicians.size() + " clinicians were successfully loaded");
        }

    }

    /**
     * Imports Users from a file chosen from a fileselector
     * @throws FileNotFoundException if the specified file is not found
     */
    @FXML
    void importUsers() throws FileNotFoundException {
        String filename;
        filename = FileSelectorController.getFileSelector(stage);
        if (filename != null) {
            List<User> users = JsonHandler.loadUsers(filename);
            System.out.println(users.size() + " donors were successfully loaded");
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

        FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root;
        try {
            root = donorLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Create New User Profile");
            newStage.show();
            NewUserController donorController = donorLoader.getController();
            donorController.init(AppController.getInstance(), stage, newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the donor overview screen for a selected user
     * @param user the selected user.
     */
    private void launchDonor(User user) {
        FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = donorLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            UserController userController = donorLoader.getController();
            AppController.getInstance().setUserController(userController);
            userController.init(AppController.getInstance(), user, newStage, true);
            newStage.show();
        } catch (IOException e) {
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
            clinicianController.init(newStage, AppController.getInstance(), clinician);
            newStage.show();
        } catch (IOException e) {
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
            adminLoaderController.init(administrator, AppController.getInstance(), newStage);
            newStage.show();
        } catch (IOException e) {
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

        } catch (IOException e) {
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
            updateAdminController.init(new Administrator(), newStage);
        } catch (IOException e) {
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
        Parent root = null;
        try {
            root = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        stage.close();
        LoginController loginController = loginLoader.getController();
        loginController.init(appController,newStage);
        //UpdateClinicianController newClinician = loginLoader.getController();
        //newClinician.init(null, appController, stage, true, newStage);
    }

    /**
     * Undoes the previous action that changed the admin
     */
    @FXML
    void undo() {

    }

    /**
     * Redoes the previous action that changed the admin
     */
    @FXML
    void redo() {

    }


    /**
     * load the labels on the admin view with the current admins details
     */
    private void displayDetails() {
        if (!administrator.getUserName().isEmpty()) {
            adminUsernameLable.setText(administrator.getUserName());
            adminFirstnameLabel.setText(administrator.getFirstName());
            if (!administrator.getMiddleName().isEmpty()) {
                adminMiddleNameLabel.setText(administrator.getMiddleName());
            } else {
                adminMiddleNameLabel.setText("");

            }
            if (!administrator.getLastName().isEmpty()) {
                adminLastNameLabel.setText(administrator.getLastName());
            } else {
                adminLastNameLabel.setText("");
            }
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
            updateAdminController.init(administrator, newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Deletes the admin account with a confirmation message
     */
    @FXML
    void deleteAdminAccount() {

    }

}
