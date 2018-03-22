package seng302.Controller;

import java.util.ArrayList;
import java.util.HashSet;
import javafx.beans.binding.Bindings;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.Years;
import seng302.Controller.AppController;
import seng302.Model.Clinician;

import java.io.IOException;
import seng302.Model.Donor;
import seng302.Model.Organs;

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
  private TableView<Donor> searchTableView;

  private Stage stage;
  private AppController appController;
  private Clinician clinician;
  private ArrayList<Donor> donors;
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
    donors = appController.getDonors();
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
    //searchPagination = new Pagination((donors.size() / ROWS_PER_PAGE + 1), 0);


  }


  /**
   * initialises the search table, abstracted from main init function for clarity
   */
  private void initSearchTable(int startIndex) {
    int endIndex = Math.min(startIndex+ROWS_PER_PAGE, donors.size());
    if (donors == null || donors.isEmpty()) {
      return;
    }
    //set up lists
    //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
    ObservableList<Donor> oListDonors = FXCollections.observableArrayList(donors);

    TableColumn<Donor, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<Donor, Integer> dobColumn = new TableColumn<>("Date of Birth");
    dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

    TableColumn<Donor, Integer> dodColumn = new TableColumn<>("Date of Death");
    dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

    TableColumn<Donor, Integer> ageColumn = new TableColumn<>("Age");
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

    TableColumn<Donor, HashSet<Organs>> organsColumn = new TableColumn<>("Organs");
    organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

    //TODO add more columns as wanted/needed
    FilteredList<Donor> fListDonors = new FilteredList<>(oListDonors);
    fListDonors = filter(searchTextField, fListDonors);
    FilteredList<Donor> squished = new FilteredList<>(fListDonors);

    SortedList<Donor> sListDonors = new SortedList<>(squished);
    sListDonors.comparatorProperty().bind(searchTableView.comparatorProperty());

    //TODO predicate on this list not working properly
    //should limit the number of items shown to ROWS_PER_PAGE
    //squished = limit(fListDonors, sListDonors);
    //set table columns and contents
    searchTableView.getColumns().setAll(nameColumn, dobColumn, dodColumn);
    searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
    searchTableView.setRowFactory((searchTableView) ->{
      return new TooltipTableRow<Donor>((Donor donor) ->{
        return donor.getTooltip();
      });
    });


    //set on-click behaviour
    searchTableView.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
          Donor donor = searchTableView.getSelectionModel().getSelectedItem();
          launchDonor(donor);
        }
      }
    });


  }

  private void launchDonor(Donor donor){
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
    donorController.init(AppController.getInstance(), donor, donorStage,true);
    donorStage.show();
  }

  /**
   *  applies a change listener to the input text box and filters a filtered list accordingly
   * @param inputTextField text field from which the list will be filtered
   * @param fListDonors list to be filtered
   * @return filtered list with filter applied
   */
  private static FilteredList<Donor> filter(TextField inputTextField, FilteredList<Donor> fListDonors) {
    inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      fListDonors.predicateProperty().bind(Bindings.createObjectBinding(() -> Donor -> {
        if (newValue == null || newValue.isEmpty()) {
          return true;
        }
        String lowerCaseFilterText = newValue.toLowerCase();
        if (Donor.getName().contains(lowerCaseFilterText)) {
          return true;
        }
        //if (other test case) return true
        return false;
      }));
    });
    return fListDonors;
  }

  private static FilteredList<Donor> limit(FilteredList<Donor> filteredList, SortedList<Donor> sortedList) {
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
    clinician.setName(nameTextField.getText());
    clinician.setWorkAddress(addressTextField.getText());
    clinician.setRegion(regionTextField.getText());
    if (passwordField.getText().equals(conformPasswordField.getText()) && !passwordField.getText()
        .equals("")) {
      clinician.setPassword(passwordField.getText());
    } else {
      warningLabel.setText("Passwords did not match.\n Password was not updated.");
    }
    clinician.setDateLastModified(DateTime.now());
    appController.updateClinicians(clinician);

  }

  @FXML
  void goToNextPage() {
    if(currentIndex + ROWS_PER_PAGE >= donors.size()) {
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
