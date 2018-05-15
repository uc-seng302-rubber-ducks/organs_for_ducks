package seng302.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng302.Model.Administrator;

import seng302.Model.Clinician;
import seng302.Model.User;
import seng302.View.CLI;

public class AdministratorViewController {

    @FXML
    private TableView<?> transplantWaitListTableView;

    @FXML
    private Label succesFailLabel;

    @FXML
    private CheckBox lungCheckBox;

    @FXML
    private CheckBox middleEarCheckBox;

    @FXML
    private CheckBox pancreasCheckBox;

    @FXML
    private TextField waitingRegionTextfield;

    @FXML
    private CheckBox boneMarrowCheckBox;

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
    private CheckBox corneaCheckBox;

    @FXML
    private Tooltip searchToolTip;

    @FXML
    private CheckBox connectiveTissueCheckBox;

    @FXML
    private Label adminFirstnameLabel;

    @FXML
    private CheckBox skinCheckBox;

    @FXML
    private Button adminLogoutButton;

    @FXML
    private Button addClinicianButton;

    @FXML
    private CheckBox boneCheckBox;

    @FXML
    private CheckBox adminAdminCheckbox;

    @FXML
    private CheckBox heartCheckBox;

    @FXML
    private Button adminUndoButton;

    @FXML
    private CheckBox adminClinicianChecknbox;

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
    private CheckBox intestineCheckBox;

    @FXML
    private CheckBox kidneyCheckBox;

    @FXML
    private MenuItem adminImportMenu;

    @FXML
    private TextField adminSearchField;

    @FXML
    private CheckBox liverCheckBox;

    @FXML
    private TextArea adminCliTextArea;

    @FXML
    private TextField cliInputTextField;

    @FXML
    private Button deleteAdminButton;

    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -1;

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
            adminClinicianChecknbox.setSelected(false);
            adminUserCheckbox.setSelected(false);
            clinicianTableView.setVisible(false);
            adminTableView.setVisible(true);
            userTableView.setVisible(false);

        }));

        adminUserCheckbox.selectedProperty().addListener((observable -> {
            adminClinicianChecknbox.setSelected(false);
            adminAdminCheckbox.setSelected(false);
            clinicianTableView.setVisible(false);
            adminTableView.setVisible(false);
            userTableView.setVisible(true);

        }));

        adminClinicianChecknbox.selectedProperty().addListener((observable -> {
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


    @FXML
    void save(ActionEvent event) {

    }

    @FXML
    void importData(ActionEvent event) {

    }

    @FXML
    void close(ActionEvent event) {

    }

    @FXML
    void addUser(ActionEvent event) {

        FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/createNewUser.fxml"));
        Parent root = null;
        try {
            root = donorLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Create New User Profile");
        newStage.show();
        NewUserController donorController = donorLoader.getController();
        donorController.init(AppController.getInstance(), stage, newStage);
    }

    /**
     * @param user the selected user.
     */
    private void launchDonor(User user) {
        FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root = null;
        try {
            root = donorLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        DonorController donorController = donorLoader.getController();
        AppController.getInstance().setDonorController(donorController);
        donorController.init(AppController.getInstance(), user, newStage, true);
        newStage.show();
    }

    /**
     * @param clinician the selected clinician.
     */
    private void launchClinician(Clinician clinician) {
        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/clinicianView.fxml"));
        Parent root = null;
        try {
            root = clinicianLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        ClinicianController clinicianController = clinicianLoader.getController();
        clinicianController.init(newStage, AppController.getInstance(), clinician);
        newStage.show();
    }

    /**
     * @param administrator the selected administrator
     */
    private void launchAdmin(Administrator administrator) {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/adminView.fxml"));
        Parent root = null;
        try {
            root = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        AdministratorViewController adminLoaderController = adminLoader.getController();
        adminLoaderController.init(administrator, AppController.getInstance(), newStage);
        newStage.show();
    }

    @FXML
    void addClinician(ActionEvent event) {

        FXMLLoader clinicianLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
        Parent root = null;
        try {
            root = clinicianLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        UpdateClinicianController newClinician = clinicianLoader.getController();
        newClinician.init(null, appController, stage, true, newStage);

    }

    @FXML
    void addAdmin(ActionEvent event) {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root = null;
        try {
            root = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        newStage.setScene(new Scene(root));
        newStage.show();
        UpdateAdminController updateAdminController = adminLoader.getController();
        updateAdminController.init(new Administrator(), appController, newStage);
    }

    @FXML
    void logout(ActionEvent event) {

    }

    @FXML
    void undo(ActionEvent event) {

    }

    @FXML
    void redo(ActionEvent event) {

    }


    /**
     * load the labels on the admin view with the current admins details
     */
    void displayDetails() {
        if (!administrator.getUserName().isEmpty()) {
            adminUsernameLable.setText(administrator.getUserName());
            if (!administrator.getUserName().equals("default")) {
                adminFirstnameLabel.setText(administrator.getFirstName());
                if (!administrator.getMiddleName().isEmpty()) {
                    adminMiddleNameLabel.setText(administrator.getMiddleName());
                }
                if (!administrator.getLastName().isEmpty()) {
                    adminLastNameLabel.setText(administrator.getLastName());
                }
            }
        }
    }

    /**
     * go to a form to update the admin
     */
    @FXML
    void updateAdmin() {
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root = null;
        try {
            root = adminLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.show();
        UpdateAdminController updateAdminController = adminLoader.getController();
        updateAdminController.init(administrator, appController, newStage);

    }

    @FXML
    void deleteAdminAccount() {

    }

}
