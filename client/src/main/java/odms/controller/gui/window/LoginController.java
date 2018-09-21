package odms.controller.gui.window;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import odms.bridge.LoginBridge;
import odms.commons.exception.ApiException;
import odms.commons.model.Administrator;
import odms.commons.model.Clinician;
import odms.commons.model.User;
import odms.commons.model._enum.UserType;
import odms.commons.utils.Log;
import odms.controller.AppController;
import odms.controller.gui.popup.utils.AlertWindowFactory;
import odms.view.CLI;

import java.io.IOException;

/**
 * Class for the login functionality of the application
 */
public class LoginController {

    private static final String USER_VIEW_URL = "/FXML/userView.fxml";
    private static final String CLINICIAN_VIEW_URL = "/FXML/clinicianView.fxml";
    private static final String ADMIN_VIEW_URL = "/FXML/adminView.fxml";
    private static final String NEW_USER_VIEW_URL = "/FXML/createNewUser.fxml";
    private static final String HELP_VIEW_URL = "/FXML/loginHelp.fxml";
    private static final String NETWORK_ERROR = "A network error occurred. Please try again or contact your IT department.";
    private static final String UNSPECIFIED_ERROR = "An unspecified error occurred. Please try again or contact your IT department.";
    @FXML
    private TextField userIDTextField;
    @FXML
    private Label userWarningLabel;
    @FXML
    private TextField staffIdTextField;
    @FXML
    private TextField staffPasswordField;
    @FXML
    private Label clinicianWarningLabel;
    @FXML
    private TextField adminUsernameTextField;
    @FXML
    private TextField adminPasswordField;
    @FXML
    private Label adminWarningLabel;
    @FXML
    private TabPane loginTabPane;
    private Stage helpStage = null;
    private AppController appController;
    private Stage stage;
    private LoginBridge loginBridge = AppController.getInstance().getLoginBridge();

    /**
     * Initializes the Login controller.
     *
     * @param appController The applications controller.
     * @param stage         The applications stage.
     */
    public void init(AppController appController, Stage stage) {
        Log.info("starting loginController");
        userWarningLabel.setText("");
        clinicianWarningLabel.setText("");
        adminWarningLabel.setText("");
        this.appController = appController;
        this.stage = stage;
        stage.setTitle("Login");
        appController.setUsername("");
        appController.setName("");
        Scene scene = stage.getScene();

        scene.setOnKeyPressed(e -> {
            int currentPane = loginTabPane.getSelectionModel().getSelectedIndex();
            if (e.getCode() == KeyCode.ENTER) {
                if (currentPane == 0) {
                    loginUser();
                } else if (currentPane == 1) {
                    loginClinician();
                } else if (currentPane == 2) {
                    loginAdmin();
                }
            }
        });
    }

    /**
     * Logs in the user.
     */
    @FXML
    void loginUser() {
        userWarningLabel.setText("");
        appController.setToken(null);
        String wantedDonor = userIDTextField.getText();
        User user = null;

        if (wantedDonor.isEmpty()) {
            userWarningLabel.setText("Please enter an NHI.");
            return;
        } else {
            try {
                user = appController.getUserBridge().getUser(wantedDonor);
            } catch (IOException e) {
                AlertWindowFactory.generateError(e);
            }
        }
        if (user == null || user.isDeleted()) {
            userWarningLabel
                    .setText("User was not found. \nTo register a new user, please click sign up.");
            return;
        }

        appController.getAppointmentsBridge().deleteCancelledAppointments(user.getNhi(), UserType.USER);

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource(USER_VIEW_URL));
        Parent root;
        try {
            root = userLoader.load();
            Log.info("Logging in as a user");
            stage.setScene(new Scene(root));
            UserController userController = userLoader.getController();
            AppController.getInstance().setUserController(userController);
            userController.init(AppController.getInstance(), user, stage, false, null);
        } catch (IOException e) {
            Log.severe("failed to load user window", e);
        }
    }


    /**
     * Logs in the clinician
     */
    @FXML
    void loginClinician() {
        clinicianWarningLabel.setText("");
        String wantedClinician;
        if (staffIdTextField.getText().isEmpty()) {
            clinicianWarningLabel.setText("Please enter your staff id number");
            return;
        } else {
            wantedClinician = staffIdTextField.getText();
        }
        String clinicianPassword = staffPasswordField.getText();
        String token = null;
        try {
            token = loginBridge.loginToServer(wantedClinician, clinicianPassword, "clinician");
        } catch (ApiException ex) {
            clinicianWarningLabel.setText("An error occurred. Please try again later.");
        }

        if (token == null) {
            return;
        }

        Clinician clinician;
        try {
            clinician = appController.getClinicianBridge().getClinician(wantedClinician, token);
        } catch (IOException e) {
            clinicianWarningLabel.setText(UNSPECIFIED_ERROR);
            Log.severe("Invalid request passed to the json handler", e);
            return;
        }
        if (clinician == null || clinician.isDeleted()) {
            clinicianWarningLabel.setText("The Clinician does not exist");
        } else {
            appController.getAppointmentsBridge().deleteCancelledAppointments(clinician.getStaffId(), UserType.CLINICIAN);

            FXMLLoader clinicianLoader = new FXMLLoader(
                    getClass().getResource(CLINICIAN_VIEW_URL));
            Parent root;
            try {
                appController.setName(clinician.getFullName());
                appController.setUsername(clinician.getStaffId());
                appController.setToken(token);
                root = clinicianLoader.load();
                stage.setScene(new Scene(root));
                stage.setMinHeight(800);
                stage.setMinWidth(1200);
                ClinicianController clinicianController = clinicianLoader.getController();
                AppController.getInstance().setClinicianController(clinicianController);
                Log.info("Logging in as a clinician");
                clinicianController.init(stage, appController, clinician, false, null);
            } catch (IOException e) {
                Log.severe("failed to load clinician window", e);
            }
        }
    }

    /**
     * Logs the administrator in
     */
    @FXML
    void loginAdmin() {
        //Checks if the admin login button is clicked and runs the admin login code
        adminWarningLabel.setText("");
        String wantedAdmin;
        if (adminUsernameTextField.getText().isEmpty()) {
            adminWarningLabel.setText("Please enter your Administrator username.");
            return;
        } else {
            wantedAdmin = adminUsernameTextField.getText();
        }
        String adminPassword = adminPasswordField.getText();
        String token;
        try {
            token = loginBridge.loginToServer(wantedAdmin, adminPassword, "admin");
        } catch (ApiException e) {
            adminWarningLabel.setText(UNSPECIFIED_ERROR);
            Log.severe(UNSPECIFIED_ERROR, e);
            return;
        }
        if (token == null) {
            return;
        }
        Administrator administrator;
        try {
            administrator = appController.getAdministratorBridge().getAdmin(wantedAdmin, token);
        } catch (IOException e) {
            Log.severe("invalid response returned", e);
            adminWarningLabel.setText(NETWORK_ERROR);
            return;
        }

        FXMLLoader administratorLoader = new FXMLLoader(
                getClass().getResource(ADMIN_VIEW_URL));
        Parent root;
        try {
            appController.setName(administrator.getFullName());
            appController.setUsername(administrator.getUserName());
            appController.setToken(token);
            root = administratorLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Administrator");
            stage.setMinHeight(800);
            stage.setMinWidth(1200);
            AdministratorViewController administratorController = administratorLoader.getController();
            AppController.getInstance().setAdministratorViewController(administratorController);
            Log.info("Logging in as an administrator");
            administratorController.init(administrator, appController, stage, true);
        } catch (IOException e) {
            Log.severe("failed to load administrator window", e);
        }

    }


    /**
     * Creates either a new user or clinician based on the login window Opens the sign up view based
     * on the login view
     */
    @FXML
    void signUp() {

        FXMLLoader userLoader = new FXMLLoader(getClass().getResource(NEW_USER_VIEW_URL));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(new Scene(root));
            newStage.setTitle("Create New User Profile");
            newStage.show();
            NewUserController userController = userLoader.getController();
            Log.info("Opening new user window");
            userController.init(AppController.getInstance(), stage, newStage);
        } catch (IOException e) {
            Log.severe("failed to load new user window", e);
        }

    }

    /**
     * Displays a pop up window with instructions to help the user on the login page.
     */
    @FXML
    private void helpButton() {

        if (helpStage == null) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(HELP_VIEW_URL));
                Parent root = fxmlLoader.load();
                helpStage = new Stage();
                helpStage.setTitle("Login Help");
                helpStage.setScene(new Scene(root));
                helpStage.setResizable(false);
                helpStage.setOnCloseRequest(event -> helpStage = null);
                helpStage.show();
                Log.info("Successfully launched help window");
            } catch (Exception e) {
                Log.severe("could not load help window", e);
            }
        }
    }


    /**
     * Opens the Command Line version of the application
     */
    @FXML
    void openCLI() {
        stage.hide();

        CLI.main(new String[]{"gui"});
        stage.show();
        FXMLLoader adminLoader = new FXMLLoader(getClass().getResource(ADMIN_VIEW_URL));
        Parent root;
        try {
            root = adminLoader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Administrator");
            AdministratorViewController administratorViewController = adminLoader.getController();
            administratorViewController.init(new Administrator(), appController, stage, true);
            Log.info("Successfully launched CLI");
        } catch (IOException e) {
            Log.severe("could not load CLI", e);
        }
    }
}

