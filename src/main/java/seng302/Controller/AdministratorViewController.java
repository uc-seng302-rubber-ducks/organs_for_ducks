package seng302.Controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import seng302.Model.*;

import java.util.HashSet;
import java.util.List;

import seng302.Service.AttributeValidation;
import seng302.Model.Administrator;
import seng302.Model.Clinician;
import seng302.Model.JsonHandler;
import seng302.Model.User;
import seng302.Service.Log;
import seng302.View.CLI;

public class AdministratorViewController {

    //<editor-fold desc="FXML stuff">
    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableView<?> transplantWaitListTableView;

    @FXML
    private CheckBox middleEarCheckBox;

    @FXML
    private CheckBox pancreasCheckBox;

    @FXML
    private TextField waitingRegionTextfield;

    @FXML
    private MenuItem importUsersMenuItem;

    @FXML
    private Label adminLastNameLabel;

    @FXML
    private Button addAdminButton;

    @FXML
    private CheckBox allCheckBox;

    @FXML
    private TableView<Clinician> clinicianTableView;

    @FXML
    private Tooltip searchToolTip;

    @FXML
    private TextField cliInputTextField;

    @FXML
    private Tooltip searchToolTip1;

    @FXML
    private Tooltip searchToolTip2;

    @FXML
    private CheckBox skinCheckBox;

    @FXML
    private Button addClinicianButton;

    @FXML
    private TableView<Administrator> adminTableView;

    @FXML
    private TextArea adminCliTextArea;

    @FXML
    private CheckBox boneCheckBox;

    @FXML
    private CheckBox adminAdminCheckbox;

    @FXML
    private CheckBox heartCheckBox;

    @FXML
    private AnchorPane filterAnchorPane;

    @FXML
    private Button recentlyDeletedButton;

    @FXML
    private Label filtersLabel;

    @FXML
    private CheckBox adminUserCheckbox;

    @FXML
    private Button adminRedoButton;

    @FXML
    private Label adminUsernameLable;

    @FXML
    private Button expandButton;

    @FXML
    private CheckBox kidneyCheckBox;

    @FXML
    private TextField adminSearchField;

    @FXML
    private CheckBox liverCheckBox;

    @FXML
    private Label succesFailLabel;

    @FXML
    private CheckBox lungCheckBox;

    @FXML
    private CheckBox boneMarrowCheckBox;

    @FXML
    private TextField searchTextField;

    @FXML
    private Pagination searchTablePagination;

    @FXML
    private Button deleteAdminButton;

    @FXML
    private MenuItem adminSaveMenu;

    @FXML
    private CheckBox corneaCheckBox;

    @FXML
    private ComboBox<?> genderComboBox;

    @FXML
    private CheckBox connectiveTissueCheckBox;

    @FXML
    private Button updateButton;

    @FXML
    private Label adminFirstnameLabel;

    @FXML
    private CheckBox adminClinicianCheckbox;

    @FXML
    private CheckBox donorFilterCheckBox;

    @FXML
    private MenuItem importCliniciansMenuItem;

    @FXML
    private Button adminLogoutButton;

    @FXML
    private MenuItem importAdminsMenuItem;

    @FXML
    private Button adminUndoButton;

    @FXML
    private Label searchCountLabel;

    @FXML
    private Button addUserButton;

    @FXML
    private Label adminMiddleNameLabel;

    @FXML
    private CheckBox receiverFilterCheckBox;

    @FXML
    private CheckBox intestineCheckBox;

    @FXML
    private Label donorStatusLabel;

    @FXML
    private Label birthGenderLabel;

    @FXML
    private TextField regionSearchTextField;
    //</editor-fold>

    private Stage stage;
    private AppController appController;
    private Administrator administrator;
    private ArrayList<String> pastCommands = new ArrayList<>();
    private int pastCommandIndex = -1;
    private final int ROWS_PER_PAGE = 30;
    private int startIndex = 0;
    private int endIndex;
    private FilteredList<User> fListUsers;
    private FilteredList<Clinician> fListClinicians;
    private FilteredList<Administrator> fListAdmins;
    private TableColumn<User, String> lNameColumn;
    private static int searchCount = 0;
    private boolean filterVisible = false;
    private TableView<?> activeTableView;


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
        initClinicianSearchTable();
        initAdminSearchTable();
        initUserSearchTable();
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
            activeTableView = adminTableView;
            userSpecificFilters(false);

        }));

        adminUserCheckbox.selectedProperty().addListener((observable -> {
            adminClinicianCheckbox.setSelected(false);
            adminAdminCheckbox.setSelected(false);
            clinicianTableView.setVisible(false);
            adminTableView.setVisible(false);
            userTableView.setVisible(true);
            activeTableView = userTableView;
            userSpecificFilters(true);

        }));

        adminClinicianCheckbox.selectedProperty().addListener((observable -> {
            adminAdminCheckbox.setSelected(false);
            adminUserCheckbox.setSelected(false);
            clinicianTableView.setVisible(true);
            adminTableView.setVisible(false);
            userTableView.setVisible(false);
            userSpecificFilters(false);
        }));

        userTableView.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                launchUser(userTableView.getSelectionModel().getSelectedItem());
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
     * Takes a boolean on weather specific fields should be visable and then set them according to the boolean
     *
     * @param shouldSee if the fields should be visible or not
     */
    private void userSpecificFilters(boolean shouldSee){
        donorStatusLabel.setVisible(shouldSee);
        donorFilterCheckBox.setVisible(shouldSee);
        receiverFilterCheckBox.setVisible(shouldSee);
        allCheckBox.setVisible(shouldSee);
        birthGenderLabel.setVisible(shouldSee);
        genderComboBox.setVisible(shouldSee);
    }


    /**
     * Initialises table for the clinician table
     */
    private void initClinicianSearchTable() {
        ObservableList<Clinician> clinicians = FXCollections.observableArrayList(appController.getClinicians());

        TableColumn<Clinician, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Clinician, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Clinician, String> nhiColumn = new TableColumn<>("Staff Id");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("staffId"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        fListClinicians = new FilteredList<>(clinicians);
        fListClinicians = filter(fListClinicians);
        FilteredList<Clinician> squished = new FilteredList<>(fListClinicians);

        SortedList<Clinician> clinicianSortedList = new SortedList<>(squished);
        clinicianSortedList.comparatorProperty().bind(clinicianTableView.comparatorProperty());

        clinicianTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        clinicianTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
        clinicianTableView.setItems(clinicianSortedList);

    }

    /**
     * Initialises the columns for the admin table
     */
    private void initAdminSearchTable() {
        ObservableList<Administrator> admins = FXCollections.observableArrayList(appController.getAdmins());

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, admins.size());
        if (admins.isEmpty()) {
            return;
        }

        TableColumn<Administrator, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<Administrator, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Administrator, String> nhiColumn = new TableColumn<>("User Name");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<Administrator, String> regionColumn = new TableColumn<>("Region");
        nhiColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        lastNameColumn.setSortType(TableColumn.SortType.ASCENDING);

        fListAdmins = new FilteredList<>(admins);
        fListAdmins = filter(fListAdmins);
        FilteredList<Administrator> squished = new FilteredList<>(fListAdmins);

        SortedList<Administrator> administratorSortedList = new SortedList<>(squished);
        administratorSortedList.comparatorProperty().bind(adminTableView.comparatorProperty());
        
        adminTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        adminTableView.getColumns().addAll(nhiColumn, firstNameColumn, lastNameColumn);
        adminTableView.setItems(administratorSortedList);

    }

    /**
     * initialises the search table, abstracted from main init function for clarity
     */
    private void initUserSearchTable() {
        TableColumn<User, String> fNameColumn;
        TableColumn<User, String> dobColumn;
        TableColumn<User, String> dodColumn;
        TableColumn<User, String> ageColumn;
        TableColumn<User, HashSet<Organs>> organsColumn;
        TableColumn<User, String> regionColumn;
        List<User> users = appController.getUsers();

        endIndex = Math.min(startIndex + ROWS_PER_PAGE, users.size());
        if (users.isEmpty()) {
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

        regionColumn = new TableColumn<>("Region");
        regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));

        organsColumn = new TableColumn<>("Organs");
        organsColumn.setCellValueFactory(new PropertyValueFactory<>("organs"));

        //add more columns as wanted/needed

        fListUsers = new FilteredList<>(oListDonors);
        fListUsers = filter(fListUsers);
        FilteredList<User> squished = new FilteredList<>(fListUsers);

        SortedList<User> sListUsers = new SortedList<>(squished);
        sListUsers.comparatorProperty().bind(userTableView.comparatorProperty());

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //squished = limit(fListDonors, sListDonors);
        //set table columns and contents
        userTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
        userTableView.setItems(sListUsers);
        userTableView.setRowFactory((searchTableView) -> new TooltipTableRow<>(User::getTooltip));

    }

    /**
     * Takes a list and returns a sub section of it.
     *
     * Works for all types
     *
     * @param list A list of any Type.
     * @return A list of supplied type.
     */
    private <T> List<T> getSearchData(List<T> list) {
        return list.subList(startIndex, endIndex);
    }


    /**
     * applies a change listener to the input text box and filters a filtered list accordingly
     *
     * @param toFilter list to be filtered
     * @return filtered list with filter applied
     */

    private <T> FilteredList<T> filter(FilteredList<T> toFilter) {
        setTextFieldListener(adminSearchField, toFilter);
        setTextFieldListener(regionSearchTextField, toFilter);
        setCheckBoxListener(donorFilterCheckBox, toFilter);
        setCheckBoxListener(receiverFilterCheckBox, toFilter);
        setCheckBoxListener(allCheckBox, toFilter);
        genderComboBox.valueProperty()
                .addListener((observable -> setFilteredListPredicate(toFilter)));

        searchTablePagination.setPageCount(searchCount / ROWS_PER_PAGE);
        return toFilter;
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param inputTextField textfield to add the listener to
     * @param filteredList     filteredList  of objects to set predicate property of
     */
    private <T> void setTextFieldListener(TextField inputTextField, FilteredList<T> filteredList) {
        inputTextField.textProperty()
                .addListener((observable) -> setFilteredListPredicate(filteredList));
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param checkBox   checkBox object to add the listener to
     * @param filteredList filteredList of object T to set predicate property of
     */
    private <T> void setCheckBoxListener(CheckBox checkBox, FilteredList<T> filteredList) {
        checkBox.selectedProperty()
                .addListener(((observable) -> setFilteredListPredicate(filteredList)));
    }

    /**
     * Sets the predicate property of filteredList to filter by specific properties
     *
     * @param fList filteredList object to modify the predicate property of
     */
    private <T> void setFilteredListPredicate(FilteredList<T> fList) {
        searchCount = 0; //refresh the searchCount every time so it recalculates it each search
        fList.predicateProperty().bind(Bindings.createObjectBinding(() -> objectToFilter -> {
            String lowerCaseFilterText = adminSearchField.getText().toLowerCase();
            boolean regionMatch = AttributeValidation.checkRegionMatches(regionSearchTextField.getText(), objectToFilter);
            //boolean genderMatch = AttributeValidation.checkGenderMatches(genderComboBox.getValue().toString(), objectToFilter);

            String fName = null;
            String lName = null;
            try {
                fName = (String) objectToFilter.getClass().getMethod("getFirstName").invoke(objectToFilter); //if this breaks just ignore it,
                lName = (String) objectToFilter.getClass().getMethod("getLastName").invoke(objectToFilter); // it will fix itself and not cause problems
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            if ((AttributeValidation.checkTextMatches(lowerCaseFilterText, fName) ||
                    AttributeValidation.checkTextMatches(lowerCaseFilterText, lName)) &&
                            (regionMatch)){// && (genderMatch)) {
                searchCount++;
                return true;
            }/* TODO: reimplement and remove this
             &&
            (((user.isDonor() == donorFilterCheckBox.isSelected()) &&
                    (user.isReceiver() == receiverFilterCheckBox.isSelected())) || allCheckBox.isSelected()))*/
            //if (other test case) return true
            return false;
        }));
        changePage(searchTablePagination.getCurrentPageIndex());
    }

    /**
     * @param pageIndex the current page.
     * @return the search table view node.
     */
    private Node changePage(int pageIndex) {
        startIndex = pageIndex * ROWS_PER_PAGE;
        endIndex = Math.min(startIndex + ROWS_PER_PAGE, appController.getUsers().size());

        int minIndex = Math.min(endIndex, fListUsers.size());

        SortedList<User> sListDonors = new SortedList<>(FXCollections.observableArrayList(fListUsers.subList(Math.min(startIndex, minIndex), minIndex)));
        sListDonors.comparatorProperty().bind(userTableView.comparatorProperty());

        lNameColumn.setSortType(TableColumn.SortType.ASCENDING);
        userTableView.setItems(sListDonors);


        int pageCount = searchCount / ROWS_PER_PAGE;
        searchTablePagination.setPageCount(pageCount > 0 ? pageCount + 1 : 1);
        searchCountLabel.setText("Showing results " + (searchCount == 0 ? startIndex : startIndex + 1) + " - " + (minIndex) + " of " + searchCount);

        return userTableView;
    }

    /**
     * Saves the data to the current file
     */
    @FXML
    void save() {

    }


    /**
     * Callback method to change the divider position to show advanced filtering options in the GUI
     */
    @FXML
    private void expandFilter() {
        double dividerPos = filterVisible ? 44 : 150;
        filterAnchorPane.setMinHeight(dividerPos);
        filterAnchorPane.setMaxHeight(dividerPos);
        filterVisible = !filterVisible;
        expandButton.setText(filterVisible ? "▲" : "▼");
    }

    /**
     * Loads the recently deleted users window
     */
    @FXML
    public void loadRecentlyDeleted() {
        FXMLLoader deletedUserLoader = new FXMLLoader(
                getClass().getResource("/FXML/deletedUsersView.fxml"));
        Parent root;
        try {
            root = deletedUserLoader.load();
            DeletedUserController deletedUserController = deletedUserLoader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            deletedUserController.init();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            System.out.println(users.size() + " users were successfully loaded");
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
            NewUserController userController = userLoader.getController();
            userController.init(AppController.getInstance(), stage, newStage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches the user overview screen for a selected user
     * @param user the selected user.
     */
    private void launchUser(User user) {
        FXMLLoader userLoader = new FXMLLoader(getClass().getResource("/FXML/userView.fxml"));
        Parent root;
        try {
            root = userLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            UserController userController = userLoader.getController();
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
        Parent root;
        try {
            root = loginLoader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.show();
            stage.close();
            LoginController loginController = loginLoader.getController();
            loginController.init(appController,newStage);

        } catch (IOException e) {
            Log.warning(e.getMessage(), e);
        }
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
