package seng302.Controller;

import java.lang.reflect.Array;
import java.time.LocalDate;
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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.List;

import seng302.Model.Donor;
import seng302.Model.Organs;
import seng302.Model.User;
import seng302.Model.Clinician;

public class ClinicianController {

  private final int ROWS_PER_PAGE = 30;
  private int startIndex = 0;
  private int endIndex;

  @FXML
  private Button undoButton;

  @FXML
  private Label staffIdLabel;

  @FXML
  private Label fNameLabel;

  @FXML
  private Label mNameLabel;

  @FXML
  private Label lNameLabel;

  @FXML
  private Label addressLabel;

  @FXML
  private Label regionLabel;

  @FXML
  private TextField searchTextField;

  @FXML
  private Tooltip searchToolTip;

  @FXML
  private TableView<User> searchTableView;

  @FXML
  private Pagination searchTablePagination;

  @FXML
  private Label searchCountLabel;

  private Stage stage;
  private AppController appController;
  private Clinician clinician;
  private ArrayList<User> users;
  private ArrayList<Stage> openStages;
  private FilteredList<User> fListDonors;

  //Initiliase table columns as class level so it is accessible for sorting in pagination methods
  TableColumn<User, String> fNameColumn;
  TableColumn<User, String> lNameColumn;
  TableColumn<User, String> dobColumn;
  TableColumn<User, String> dodColumn;
  TableColumn<User, String> ageColumn;
  TableColumn<User, HashSet<Organs>> organsColumn;


  private static int searchCount = 0;


    /**
     * Initializes the controller class for the clinician overview.
     * @param stage The applications stage.
     * @param appController the applications controller.
     * @param clinician The current clinician.
     */
  public void init(Stage stage, AppController appController, Clinician clinician) {
    this.stage = stage;
    this.appController = appController;
    this.clinician = clinician;
    stage.setResizable(true);
    showClinician();
    users = appController.getUsers();
    searchCount = users.size();
    initSearchTable();
    searchCountLabel.setText("Showing results "+(searchCount == 0 ? startIndex : startIndex+1) + " - " + (endIndex) + " of " + searchCount);
    openStages = new ArrayList<>();
    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
      public void handle(WindowEvent we){
        if(!openStages.isEmpty()){
          for (Stage s : openStages){
            s.close();
          }
        }
      }
    });
    int pageCount = searchCount / ROWS_PER_PAGE;
    searchTablePagination.setPageCount(pageCount > 0 ? pageCount : 1);
    searchTablePagination.currentPageIndexProperty().addListener(((observable, oldValue, newValue) -> changePage(newValue.intValue())));
    //searchPagination = new Pagination((users.size() / ROWS_PER_PAGE + 1), 0);
  }


  private void showClinician() {
    System.out.println(clinician.getMiddleName());
    staffIdLabel.setText(clinician.getStaffId());
    fNameLabel.setText(clinician.getFirstName());
    mNameLabel.setText(clinician.getMiddleName());
    lNameLabel.setText(clinician.getLastName());
    addressLabel.setText(clinician.getWorkAddress());
    regionLabel.setText(clinician.getRegion());
  }

  /**
   * initialises the search table, abstracted from main init function for clarity
   */
  private void initSearchTable() {
    endIndex = Math.min(startIndex+ROWS_PER_PAGE, users.size());
    if (users == null || users.isEmpty()) {
      return;
    }

    List<User> usersSublist = getSearchData(users);
    //set up lists
    //table contents are SortedList of a FilteredList of an ObservableList of an ArrayList
    ObservableList<User> oListDonors = FXCollections.observableList(users);

    fNameColumn = new TableColumn<>("First name");
    fNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

    lNameColumn = new TableColumn<>("Last name");
    lNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);

    dobColumn = new TableColumn<>("Date of Birth");
    dobColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfBirth"));

    dodColumn = new TableColumn<>("Date of Death");
    dodColumn.setCellValueFactory(new PropertyValueFactory<>("dateOfDeath"));

    ageColumn = new TableColumn<>("Age");
    ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

    organsColumn = new TableColumn<>("Organs");
    organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

    //TODO add more columns as wanted/needed
    fListDonors = new FilteredList<>(oListDonors);
    fListDonors = filter(searchTextField, fListDonors);
    FilteredList<User> squished = new FilteredList<>(fListDonors);

    SortedList<User> sListDonors = new SortedList<>(squished);
    sListDonors.comparatorProperty().bind(searchTableView.comparatorProperty());

    //TODO predicate on this list not working properly
    //should limit the number of items shown to ROWS_PER_PAGE
    //squished = limit(fListDonors, sListDonors);
    //set table columns and contents
    searchTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, organsColumn);
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

    /**
     *
     * @param arrayList An array list of users.
     * @return A list of users.
     */
  private List<User> getSearchData(ArrayList<User> arrayList) {
    return arrayList.subList(startIndex, endIndex);
  }


    /**
     *
     * @param pageIndex the current page.
     * @return the search table view node.
     */
  private Node changePage(int pageIndex) {
    startIndex = pageIndex * ROWS_PER_PAGE;
    endIndex = Math.min(startIndex+ROWS_PER_PAGE, users.size());

    int minIndex = Math.min(endIndex, fListDonors.size());

    SortedList<User> sListDonors = new SortedList<>(FXCollections.observableArrayList(fListDonors.subList(Math.min(startIndex, minIndex), minIndex)));
    sListDonors.comparatorProperty().bind(searchTableView.comparatorProperty());

    lNameColumn.setSortType(TableColumn.SortType.ASCENDING);
    searchTableView.setItems(sListDonors);


    int pageCount = searchCount / ROWS_PER_PAGE;
    searchTablePagination.setPageCount(pageCount > 0 ? pageCount : 1);
    searchCountLabel.setText("Showing results "+(searchCount == 0 ? startIndex : startIndex+1) + " - " + (minIndex) + " of " + searchCount);

    return searchTableView;
  }

    /**
     *
     * @param user the selected user.
     */
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
  private FilteredList<User> filter(TextField inputTextField, FilteredList<User> fListUsers) {
    inputTextField.textProperty().addListener((observable, oldValue, newValue) -> {
      searchCount = 0;
      fListUsers.predicateProperty().bind(Bindings.createObjectBinding(() -> donor -> {
        if (newValue == null || newValue.isEmpty()) {
          searchCount++;
          return true;
        }
        String lowerCaseFilterText = newValue.toLowerCase();
        if ((donor.getFirstName().toLowerCase()).startsWith(lowerCaseFilterText) ||
                (donor.getLastName().toLowerCase().startsWith(lowerCaseFilterText))) {
          searchCount++;
          return true;
        }
        //if (other test case) return true
        return false;
      }));
      changePage(searchTablePagination.getCurrentPageIndex());
    });
    searchTablePagination.setPageCount(searchCount / ROWS_PER_PAGE);
    return fListUsers;
  }

  @FXML
  void undo(ActionEvent event) {

  }

  /**
   * Returns the user to the login screen
   * @param event An action event
   */
  @FXML
  void logout(ActionEvent event) {
    //confirm(new ActionEvent());
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/loginView.fxml"));
    Parent root = null;
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    stage.setScene(new Scene(root));
    LoginController loginController = loader.getController();
    loginController.init(AppController.getInstance(), stage);
    stage.show();
  }

  /**
   * Opens an edit window for the clinicians personal details
   * @param event An action event
   */
  @FXML
  void edit(ActionEvent event) {
    FXMLLoader updateLoader = new FXMLLoader(getClass().getResource("/FXML/updateClinician.fxml"));
    Parent root = null;
    System.out.println(updateLoader);
    try {
      root = updateLoader.load();
      UpdateClinicianController updateClinicianController = updateLoader.getController();
      Stage stage = new Stage();
      updateClinicianController.init(clinician, appController, stage, false);
      stage.setScene(new Scene(root));
      stage.initModality(Modality.APPLICATION_MODAL); // background window is no longer selectable
      stage.showAndWait();
      showClinician();

    } catch (IOException e){
      e.printStackTrace();
    }
  }
//
//  @FXML
//  void goToNextPage() {
//    if(currentIndex + ROWS_PER_PAGE >= users.size()) {
//      initSearchTable(currentIndex);
//    } else {
//      initSearchTable(currentIndex + ROWS_PER_PAGE);
//      currentIndex += ROWS_PER_PAGE;
//    }
//  }
//
//  @FXML
//  void goToPrevPage() {
//    if(currentIndex - ROWS_PER_PAGE < 0) {
//      initSearchTable(0);
//    } else {
//      initSearchTable(currentIndex - ROWS_PER_PAGE);
//      currentIndex -= ROWS_PER_PAGE;
//    }
//
//  }
}
