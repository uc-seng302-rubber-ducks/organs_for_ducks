package seng302.Controller;

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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.Administrator;

import java.io.IOException;

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
    private Button deleteAdminButton;

    private Stage stage;
    private AppController appController;
    private Administrator administrator;

    public void init(Administrator administrator, AppController appController, Stage stage){
        this.stage = stage;
        this.appController = appController;
        this.administrator = administrator;
        //displayDetails();


        if (administrator.getUserName().equals("default")) {
            deleteAdminButton.setDisable(true);
        }
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
        FXMLLoader adminLoader =  new FXMLLoader(getClass().getResource("/FXML/updateAdmin.fxml"));
        Parent root = null;
        try{
            root = adminLoader.load();
        } catch (IOException e){
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
    void displayDetails(){
        adminUsernameLable.setText(administrator.getUserName());
        adminFirstnameLabel.setText(administrator.getFirstName());
        if (!administrator.getMiddleName().isEmpty()){
            adminMiddleNameLabel.setText(administrator.getMiddleName());
        }
        if (!administrator.getLastName().isEmpty()){
            adminLastNameLabel.setText(administrator.getLastName());
        }
    }

  /**
   * go to a form to update the admin
   */
  @FXML
    void updateAdmin(){
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
      updateAdminController.init(administrator,appController,newStage);

    }

    @FXML
    void deleteAdminAccount() {

    }

}
