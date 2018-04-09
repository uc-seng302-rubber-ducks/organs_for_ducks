package seng302.Controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import javafx.beans.binding.Bindings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.joda.time.DateTime;
import seng302.Model.Clinician;

import java.io.IOException;
import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.User;

public class ClinicianController {

  private final int ROWS_PER_PAGE = 30;
  @FXML
  private TextField regionTextField;

  @FXML
  private TextField addressTextField;

  @FXML
  private PasswordField conformPasswordField;

  @FXML
  private TextField nameTextField;

  @FXML
  private Button logoutButton;

  @FXML
  private PasswordField passwordField;

  @FXML
  private Button confirmButton;

  @FXML
  private Button undoButton;

  @FXML
  private Label staffIdLabel;

  @FXML
  private Label warningLabel;

  @FXML
  private TextField searchTextField;


  @FXML
  private Tooltip searchToolTip;

  @FXML
  private TableView<User> searchTableView;

  private Stage stage;
  private AppController appController;
  private Clinician clinician;
  private ArrayList<User> users;
  private ArrayList<Stage> openStages;

  private static int currentIndex = 0;

  public void init(Stage stage, AppController appController, Clinician clinician) {
    warningLabel.setText("");
    this.stage = stage;
    this.appController = appController;
    this.clinician = clinician;
    nameTextField.setText(clinician.getName());
    staffIdLabel.setText(String.valueOf(clinician.getStaffId()));
    addressTextField.setText(clinician.getWorkAddress());
    regionTextField.setText(clinician.getRegion());
    users = appController.getUsers();
    initSearchTable(0);
    openStages = new ArrayList<>();
    stage.setOnCloseRequest(new EventHandler<WindowEvent>(){
      public void handle(WindowEvent we){
        if(!openStages.isEmpty()){
          for (Stage s : openStages){
            s.close();
          };
        };
      };
    });
    //searchPagination = new Pagination((users.size() / ROWS_PER_PAGE + 1), 0);


  }


  /**
   * initialises the search table, abstracted from main init function for clarity
   */
  private void initSearchTable(int startIndex) {
    int endIndex = Math.min(startIndex+ROWS_PER_PAGE, users.size());
    if (users == null || users.isEmpty()) {
      return;
    }
    //set up lists
    //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
    ObservableList<User> oListDonors = FXCollections.observableArrayList(users);

    TableColumn<User, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<User, Integer> dobColumn = new TableColumn<>("Date of Birth");
    dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

    TableColumn<User, Integer> dodColumn = new TableColumn<>("Date of Death");
    dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

    TableColumn<User, Integer> ageColumn = new TableColumn<>("Age");
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

    TableColumn<User, HashSet<Organs>> organsColumn = new TableColumn<>("Organs");
    organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

    //TODO add more columns as wanted/needed
    FilteredList<User> fListDonors = new FilteredList<>(oListDonors);
    fListDonors = filter(searchTextField, fListDonors);
    FilteredList<User> squished = new FilteredList<>(fListDonors);

    SortedList<User> sListDonors = new SortedList<>(squished);
    sListDonors.comparatorProperty().bind(searchTableView.comparatorProperty());

    //TODO predicate on this list not working properly
    //should limit the number of items shown to ROWS_PER_PAGE
    //squished = limit(fListDonors, sListDonors);
    //set table columns and contents
    searchTableView.getColumns().setAll(nameColumn, dobColumn, dodColumn, ageColumn, organsColumn);
    //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
    searchTableView.setItems(sListDonors);
    searchTableView.setRowFactory((searchTableView) ->{
      return new TooltipTableRow<User>((User user) ->{
        return user.getTooltip();
      });
    });


    //set on-click behaviour
    searchTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          User user = searchTableView.getSelectionModel().getSelectedItem();
          launchDonor(user);
        }
      }
    });


  }

  private void launchDonor(User user){
    FXMLLoader donorLoader = new FXMLLoader(getClass().getResource("/FXML/donorView.fxml"));
    Parent root = null;
    try {
      root = donorLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Stage donorStage = new Stage();
    donorStage.setScene(new Scene(root));
    openStages.add(donorStage);
    DonorController donorController = donorLoader.getController();
    AppController.getInstance().setDonorController(donorController);
    donorController.init(AppController.getInstance(), user, donorStage,true);
    donorStage.show();
  }

  /**
   *  applies a change listener to the input text box and filters a filtered list accordingly
   * @param inputTextField text field from which the list will be filtered
   * @param fListUsers list to be filtered
   * @return filtered list with filter applied
   */
  private static FilteredList<User> filter(TextField inputTextField, FilteredList<User> fListUsers) {
    inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      fListUsers.predicateProperty().bind(Bindings.createObjectBinding(() -> Donor -> {
        if (newValue == null || newValue.isEmpty()) {
          return true;
        }
        String lowerCaseFilterText = newValue.toLowerCase();
        if (Donor.getName().toLowerCase().contains(lowerCaseFilterText)) {
          return true;
        }
        //if (other test case) return true
        return false;
      }));
    });
    return fListUsers;
  }

  private static FilteredList<User> limit(FilteredList<User> filteredList, SortedList<User> sortedList) {
    filteredList.setPredicate(Donor -> {
      if (sortedList.indexOf(Donor) > 30 ) {
        return false;
      }
      return true;
    });
    return filteredList;
  };
  @FXML
  void undo(ActionEvent event) {

  }

  @FXML
  void logout(ActionEvent event) {
    confirm(new ActionEvent());
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    LoginController loginController = loader.getController();
    loginController.init(AppController.getInstance(), stage);
    stage.setScene(new Scene(root));
    stage.show();
  }

  @FXML
  void confirm(ActionEvent event) {
    warningLabel.setText("");
    clinician.setName(nameTextField.getText());
    clinician.setWorkAddress(addressTextField.getText());
    clinician.setRegion(regionTextField.getText());
    if(!passwordField.getText().equals("")) {
      if (passwordField.getText().equals(conformPasswordField.getText())) {
        clinician.setPassword(passwordField.getText());
      } else {
        warningLabel.setText("Passwords did not match.\n Password was not updated.");
      }
    }
    clinician.setDateLastModified(LocalDateTime.now());
    appController.updateClinicians(clinician);

  }

  @FXML
  void goToNextPage() {
    if(currentIndex + ROWS_PER_PAGE >= users.size()) {
      initSearchTable(currentIndex);
    } else {
      initSearchTable(currentIndex + ROWS_PER_PAGE);
      currentIndex += ROWS_PER_PAGE;
    }
  }

  @FXML
  void goToPrevPage() {
    if(currentIndex - ROWS_PER_PAGE < 0) {
      initSearchTable(0);
    } else {
      initSearchTable(currentIndex - ROWS_PER_PAGE);
      currentIndex -= ROWS_PER_PAGE;
    }

  }
}
