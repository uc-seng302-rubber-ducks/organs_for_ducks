package seng302.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import seng302.Model.Administrator;
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
    private TableView<?> searchTableView;

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

    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -1;

    public void init(Stage stage, AppController appController, Administrator administrator){
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;

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
            e.printStackTrace();}

        Stage newStage = new Stage();
        newStage.setScene(new Scene(root));
        newStage.setTitle("Create New User Profile");
        newStage.show();
        NewUserController donorController =  donorLoader.getController();
        donorController.init(AppController.getInstance(),  stage, newStage);
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

}
