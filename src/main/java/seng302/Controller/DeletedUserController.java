package seng302.Controller;

import java.time.LocalDate;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import seng302.Exception.UserAlreadyExistsException;
import seng302.Exception.UserNotFoundException;
import seng302.Model.Administrator;
import seng302.Model.Clinician;
import seng302.Model.Organs;
import seng302.Model.User;

public class DeletedUserController {


  @FXML
  private TableView<User> deletedUserTableView;

  @FXML
  private TableView<Clinician> deletedClinicianTableView;

  @FXML
  private TableView<Administrator> deletedAdminTableView;


  @FXML
  private ToggleGroup deletedProfileRadioButtons;

  @FXML
  private RadioButton clinicianRadioButton;

  @FXML
  private RadioButton userRadioButton;

  @FXML
  private RadioButton adminRadioButton;

  @FXML
  public void init(boolean fromAdmin) {
    initUserTableView();

    if (fromAdmin) {
      deletedProfileRadioButtons.getToggles().forEach(radio -> ((RadioButton) radio).setVisible(true));

      deletedProfileRadioButtons.getToggles().forEach(radio -> (radio).selectedProperty().addListener(((observable, oldValue, newValue) -> {
        deletedClinicianTableView.setVisible(false);
        deletedAdminTableView.setVisible(false);
        deletedUserTableView.setVisible(false);

        if (userRadioButton.isSelected()) {
          deletedUserTableView.setVisible(true);

        } else if (clinicianRadioButton.isSelected()) {
          deletedClinicianTableView.setVisible(true);

        } else if (adminRadioButton.isSelected()) {
          deletedAdminTableView.setVisible(true);
        }

      })));

      initUserTableView();
      initClinicianTableView();
      initAdminTableView();
    } else {
      initUserTableView();
    }
  }

  /**
   * Populates the table view with public users that have been deleted in the current session.
   */
  private void initUserTableView() {
    ObservableList<User> oListUsers = FXCollections
        .observableList(AppController.getInstance().getDeletedUsers());

    TableColumn<User, String> fNameColumn = new TableColumn<>("First name");
    fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    fNameColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));

    TableColumn<User, String> lNameColumn = new TableColumn<>("Last name");
    lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lNameColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));
    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

    TableColumn<User, LocalDate> dobColumn = new TableColumn<>("Date of Birth");
    dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));
    dobColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(6));

    TableColumn<User, LocalDate> dodColumn = new TableColumn<>("Date of Death");
    dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));
    dodColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(6));

    TableColumn<User, Integer> ageColumn = new TableColumn<>("Age");
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
    ageColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(10));

    TableColumn<User, String> regionColumn = new TableColumn<>("Region");
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    regionColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(8));

    TableColumn<User, ArrayList<Organs>> organsColumn = new TableColumn<>("Organs");
    organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));
    organsColumn.prefWidthProperty().bind(deletedUserTableView.widthProperty().divide(7));

    deletedUserTableView.getColumns()
        .setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn,
            organsColumn);
    //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
    deletedUserTableView.setItems(oListUsers);
    deletedUserTableView.setRowFactory(
        (searchTableView) -> new TooltipTableRow<>((User user) -> user.getTooltip()));
  }


  /**
   * Populates the table view with clinicians that have been deleted in the current session.
   */
  private void initClinicianTableView() {
    ObservableList<Clinician> oListClinicians = FXCollections.observableList(AppController.getInstance().getDeletedClinicians());

    TableColumn<Clinician, String> staffIDColumn = new TableColumn<>("Staff ID");
    staffIDColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));
    staffIDColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

    TableColumn<Clinician, String> fNameColumn = new TableColumn<>("First name");
    fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    fNameColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

    TableColumn<Clinician, String> lNameColumn = new TableColumn<>("Last name");
    lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lNameColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));
    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

    TableColumn<Clinician, String> regionColumn = new TableColumn<>("Region");
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    regionColumn.prefWidthProperty().bind(deletedClinicianTableView.widthProperty().divide(4));

    deletedClinicianTableView.getColumns()
            .setAll(staffIDColumn, fNameColumn, lNameColumn, regionColumn);

    deletedClinicianTableView.setItems(oListClinicians);
  }

  /**
   * Populates the table view with administrators that have been deleted in the current session.
   */
  private void initAdminTableView() {
    ObservableList<Administrator> oListAdmins = FXCollections.observableList(AppController.getInstance().getDeletedAdmins());

    TableColumn<Administrator, String> usernameColumn = new TableColumn<>("Username");
    usernameColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));
    usernameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));

    TableColumn<Administrator, String> fNameColumn = new TableColumn<>("First name");
    fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
    fNameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));

    TableColumn<Administrator, String> lNameColumn = new TableColumn<>("Last name");
    lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lNameColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));
    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

    TableColumn<Administrator, String> regionColumn = new TableColumn<>("Region");
    regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
    regionColumn.prefWidthProperty().bind(deletedAdminTableView.widthProperty().divide(4));


    deletedAdminTableView.getColumns()
            .setAll(usernameColumn, fNameColumn, lNameColumn, regionColumn);

    deletedAdminTableView.setItems(oListAdmins);
  }


  @FXML
  public void undoDeletedUser() {


    try {
      AppController.getInstance()
          .undoDeletion(deletedUserTableView.selectionModelProperty().getValue().getSelectedItem());
    } catch (UserAlreadyExistsException e) {
      //TODO: Set error label text
    } catch (UserNotFoundException e) {
      //TODO: Set error label text
    }
  }

  public void setErrorLabelText(String text) {
    //TODO: Finish this
  }
}
