package seng302.Controller;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
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
    private FilteredList<User> fListDonors;
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
        displayClinicanTable();
        displayAdminTable();
        displayUserTable();
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

        }));

        adminUserCheckbox.selectedProperty().addListener((observable -> {
            adminClinicianCheckbox.setSelected(false);
            adminAdminCheckbox.setSelected(false);
            clinicianTableView.setVisible(false);
            adminTableView.setVisible(false);
            userTableView.setVisible(true);
            activeTableView = userTableView;

        }));

        adminClinicianCheckbox.selectedProperty().addListener((observable -> {
            adminAdminCheckbox.setSelected(false);
            adminUserCheckbox.setSelected(false);
            clinicianTableView.setVisible(true);
            adminTableView.setVisible(false);
            userTableView.setVisible(false);
            activeTableView = clinicianTableView;

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

        fListDonors = new FilteredList<>(oListDonors);
        fListDonors = filter(fListDonors);
        FilteredList<User> squished = new FilteredList<>(fListDonors);

        SortedList<User> sListDonors = new SortedList<>(squished);
        sListDonors.comparatorProperty().bind(userTableView.comparatorProperty());

        //predicate on this list not working properly
        //should limit the number of items shown to ROWS_PER_PAGE
        //squished = limit(fListDonors, sListDonors);
        //set table columns and contents
        userTableView.getColumns().setAll(fNameColumn, lNameColumn, dobColumn, dodColumn, ageColumn, regionColumn, organsColumn);
        //searchTableView.setItems(FXCollections.observableList(sListDonors.subList(startIndex, endIndex)));
        userTableView.setItems(sListDonors);
        userTableView.setRowFactory((searchTableView) -> new TooltipTableRow<>(User::getTooltip));


        //set on-click behaviour
        userTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                User user = userTableView.getSelectionModel().getSelectedItem();
                launchDonor(user);
            }
        });
    }

    /**
     * @param arrayList An array list of users.
     * @return A list of users.
     */
    private List<User> getSearchData(List<User> arrayList) {
        return arrayList.subList(startIndex, endIndex);
    }


    /**
     * applies a change listener to the input text box and filters a filtered list accordingly
     *
     * @param fListUsers list to be filtered
     * @return filtered list with filter applied
     */
    private FilteredList<User> filter(FilteredList<User> fListUsers) {
        setTextFieldListener(searchTextField, fListUsers);
        setTextFieldListener(regionSearchTextField, fListUsers);
        setCheckBoxListener(donorFilterCheckBox, fListUsers);
        setCheckBoxListener(receiverFilterCheckBox, fListUsers);
        setCheckBoxListener(allCheckBox, fListUsers);
        genderComboBox.valueProperty()
                .addListener((observable -> setFilteredListPredicate(fListUsers)));

        searchTablePagination.setPageCount(searchCount / ROWS_PER_PAGE);
        return fListUsers;
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param inputTextField textfield to add the listener to
     * @param fListUsers     filteredList object of users to set predicate property of
     */
    private void setTextFieldListener(TextField inputTextField, FilteredList<User> fListUsers) {
        inputTextField.textProperty()
                .addListener((observable) -> setFilteredListPredicate(fListUsers));
    }

    /**
     * Method to add the predicate trough the listener
     *
     * @param checkBox   checkBox object to add the listener to
     * @param fListUsers filteredList object of users to set predicate property of
     */
    private void setCheckBoxListener(CheckBox checkBox, FilteredList<User> fListUsers) {
        checkBox.selectedProperty()
                .addListener(((observable) -> setFilteredListPredicate(fListUsers)));
    }

    /**
     * Sets the predicate property of filteredList to filter by specific properties
     *
     * @param fList filteredList object to modify the predicate property of
     */
    private void setFilteredListPredicate(FilteredList<User> fList) {
        searchCount = 0; //refresh the searchCount every time so it recalculates it each search
        fList.predicateProperty().bind(Bindings.createObjectBinding(() -> user -> {
            String lowerCaseFilterText = adminSearchField.getText().toLowerCase();
            boolean regionMatch = AttributeValidation.checkRegionMatches(regionSearchTextField.getText(), user);
            boolean genderMatch = AttributeValidation.checkGenderMatches(genderComboBox.getValue().toString(), user);

            //System.out.println(user);
            if (AttributeValidation.checkTextMatches(lowerCaseFilterText, user.getFirstName()) ||
                    AttributeValidation.checkTextMatches(lowerCaseFilterText, user.getLastName()) &&
                            (regionMatch) && (genderMatch) &&
                            (((user.isDonor() == donorFilterCheckBox.isSelected()) &&
                                    (user.isReceiver() == receiverFilterCheckBox.isSelected())) || allCheckBox.isSelected())) {
                searchCount++;
                return true;
            }

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

        int minIndex = Math.min(endIndex, fListDonors.size());

        SortedList<User> sListDonors = new SortedList<>(FXCollections.observableArrayList(fListDonors.subList(Math.min(startIndex, minIndex), minIndex)));
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
